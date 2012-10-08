package com.polopoly.ps.fileserver.resources;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.google.inject.Inject;
import com.polopoly.ps.fileserver.mime.MimeDetector;
import com.polopoly.ps.fileserver.repository.FileResource;
import com.polopoly.ps.fileserver.repository.Repository;
import com.polopoly.ps.fileserver.repository.RepositoryStorageException;
import com.polopoly.ps.fileserver.util.ExpiresDateUtil;


@Path(FileResourceResource.RESOURCE + "/{id}{filename:(/filename/[^/]+?)?}")
public class FileResourceResource {
    private static final String CLASS = FileResourceResource.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);
    public static final String RESOURCE = "file";

    @Context
    private UriInfo uriInfo;

    private final Repository repository;
    private final MimeDetector mimeDetector;

    @Inject
    public FileResourceResource(Repository repository, MimeDetector mimeDetector) {
        this.repository = repository;
        this.mimeDetector = mimeDetector;
    }

    @POST
    public Response postFileResource(@Context HttpHeaders headers, @PathParam("id") final String filename,
                                     @PathParam("filename") final String unused, byte[] data) {
        LOG.log(Level.INFO, "POSTing file " + filename);
        String id = null;
        try {
            id = repository.addFileResource(new FileResource(filename, data));
        } catch (RepositoryStorageException e) {
            LOG.log(Level.WARNING, "Could not store file " + filename, e);
            return Response.serverError().build();
        }
        String uriString = uriInfo.getBaseUri().toString() + RESOURCE + "/" + id + "/filename/" + filename;
        String metadataUriString = uriInfo.getBaseUri().toString() + MetadataResource.RESOURCE + "/" + id + "/filename/" + filename + ".metadata";
        URI uri = null;
        
        try {
            uri = new URI(uriString);
        } catch (URISyntaxException e) {
            LOG.log(Level.WARNING, "Could not create uri for file " + filename, e);
            return Response.serverError().build();
        }
        LOG.log(Level.INFO, "Successfully posted " + filename + " as " + id + ", unused is " + unused);
        return Response.created(uri).header("Metadata", metadataUriString).build();
    }

    @GET
    public Response getFileResource(@PathParam("id") String id, @PathParam("filename") String filename) {
        LOG.log(Level.INFO, "GETting file " + id);

        FileResource file;
        try {
            file = repository.getFileResource(id);
        } catch (RepositoryStorageException e) {
            LOG.log(Level.INFO, "Failed request for " + uriInfo.getAbsolutePath());
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(file.getData(), getContentType(file))
            .header("Expires", ExpiresDateUtil.getInfinateExpiresDate()).build();
    }

    @DELETE
    public Response deleteFileResource(@PathParam("id") String id) {
        try {
            repository.deleteFileResource(id);
            return Response.status(Status.NO_CONTENT).build();
        } catch (RepositoryStorageException e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String getContentType(FileResource file) {
        String mimeType = null;
        try {
            //This is slow (but probably faster than the old check)
            java.io.InputStream is = new BufferedInputStream(new ByteArrayInputStream(file.getData()));
            mimeType = URLConnection.guessContentTypeFromStream(is);
            is.close();
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Encountered exception while trying to determine mime type from file, message: " + e.getMessage());
        }
        if (mimeType == null || mimeType.isEmpty()) {
            //Fall back to old check
            mimeType = mimeDetector.getMimeType(file);
        }
        return mimeType;
    }

}
