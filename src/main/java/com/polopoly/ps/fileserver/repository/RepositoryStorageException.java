package com.polopoly.ps.fileserver.repository;

public class RepositoryStorageException extends Exception {
    RepositoryStorageException(String message, Exception e) {
        super(message, e);
    }
}
