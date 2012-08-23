package com.polopoly.ps.fileserver.repository;


import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.MimetypesFileTypeMap;

import com.polopoly.ps.fileserver.mime.Metadata;

public class FileResource {
    private static final String CLASS = FileSystemRepository.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);
    private byte[] data;
    private Metadata metaData;

    public FileResource(String filename, byte[] data) {
        LOG.log(Level.FINE, "Creating file resource for " + filename);
        this.data = data;
        String mimeType = new MimetypesFileTypeMap().getContentType(filename);
        this.metaData = new Metadata(filename, mimeType);
    }

    public byte[] getData() {
        return data;
    }

    public Metadata getMetadata() {
        return metaData;
    }
}
