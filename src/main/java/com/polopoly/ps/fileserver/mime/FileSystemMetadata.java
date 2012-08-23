package com.polopoly.ps.fileserver.mime;

import com.polopoly.ps.fileserver.mime.Metadata;

public class FileSystemMetadata extends Metadata {
    private String path;
    
    public String getPath() {
        return path;
    }
    
    @Override 
    public String toString() {
        String ret = super.toString();
        return ret +"path=" + getPath() + "\n";
    }
}
