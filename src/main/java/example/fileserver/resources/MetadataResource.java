package example.fileserver.resources;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import com.google.inject.Inject;

import example.fileserver.mime.Metadata;
import example.fileserver.util.ExpiresDateUtil;
import example.fileserver.repository.Repository;
import example.fileserver.repository.RepositoryStorageException;

@Path(MetadataResource.RESOURCE + "/{id}{filename:(/filename/[^/]+?)?}")
public class MetadataResource
{
    private static final String CLASS = MetadataResource.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);
    public static final String RESOURCE = "metadata";

    @Context
    private UriInfo uriInfo;
    
    private Repository repository;

    @Inject
    public MetadataResource(Repository repository) {
        this.repository = repository;
    }

    @GET
    public Response getMetadata(@PathParam("id") String id, @PathParam("filename") String filename)
    {
        LOG.log(Level.INFO, "GETting file metadata for " + id);
        Metadata metadata;
        try {
            metadata = repository.getMetadata(id);
            // TODO: It should be possible to detect system character encoding
            // (used in String.getBytes)and set the applicable response header
            // correctly.
            return Response.ok(metadata.toString().getBytes(),"text/plain")
                              .header("Expires", ExpiresDateUtil.getInfinateExpiresDate())
                              .build();
        } catch (RepositoryStorageException e) {
            LOG.log(Level.INFO, "Failed text/plain request for " + uriInfo.getAbsolutePath(), e);
            return Response.status(Status.NOT_FOUND).build();
        }
    }
}
