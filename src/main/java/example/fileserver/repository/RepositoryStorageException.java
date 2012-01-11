package example.fileserver.repository;

public class RepositoryStorageException extends Exception {
    RepositoryStorageException(String message, Exception e) {
        super(message, e);
    }
}
