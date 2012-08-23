package com.polopoly.ps.fileserver.mime;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Metadata {
    private static final String FILENAME = "filename";
    private static final String MIMETYPE = "mimeType";
    private static final String PATH     = "path";

    private static final String CLASS = Metadata.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);

    private String mimeType = "";
    private String filename = "";
    private String path = "";
    
    public Metadata(String filename, String mimeType) {
        LOG.log(Level.FINE, "Creating metadata " + filename);
        this.filename = filename;
        this.mimeType = mimeType;
    }
    
    public Metadata() {
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getFilename() {
        return filename;
    }

    public String getPath() {
        return path; 
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(FILENAME + "=" + getFilename() + "\n");
        b.append(MIMETYPE + "=" + getMimeType() + "\n");
        b.append(PATH + "=" + getPath() + "\n");
        return b.toString();
    }
    
    public Metadata load(String stringRepresentation) {
        String[] lines = stringRepresentation.split("\n");
        for (int i = 0; i < lines.length; ++i) {
            parseLine(lines[i]);
        }
        return this;
    }

    private void parseLine(String line) {
        String[] pair = line.split("=");
        if (pair.length == 2) {
            String key = pair[0];
            String value = pair[1];
            if (key.equals(FILENAME)) {
                filename = value;
            } else if (key.equals(MIMETYPE)) {
                mimeType = value;
            } else if (key.equals(PATH)) {
                path = value;
            }
        }
    }
}
