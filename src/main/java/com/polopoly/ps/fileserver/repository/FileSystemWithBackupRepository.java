package com.polopoly.ps.fileserver.repository;

import java.io.FileNotFoundException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.polopoly.ps.fileserver.util.FileSystemUtils;


/**
 * This repository implementation allows to specify two repository
 * folders: a main repository and a backup repository.
 * The idea is that in a production environment the
 * backup repository folder is a network mount that you can then
 * use for failover...
 *
 *
 * All reads will be performed <em>exclusively</em> from the main repository.
 * Writes will be executed on both main and backup repositories, however
 * writes on the backup repository are asynchronous and will therefore
 * be non-blocking for the WS.
 *
 * A write error on the backup repository will be logged on a specific log handler, no
 * other retry strategy / error recovery will be attempted.
 *
 * @author gmola
 *         date: 12/14/11
 */
@Singleton
public class FileSystemWithBackupRepository extends FileSystemRepository {

    private static final int NUM_THREADS = 5;
    private static final int MAX_THREADS = 10;

    private static final Logger BACKUP_REPO_ERROR_LOG = Logger.getLogger("syncErrorLogger");

    private final String backupRepositoryDirectory;
    private final ThreadPoolExecutor threadPoolExecutor;

    @Inject
    public FileSystemWithBackupRepository(@Named("RepositoryDirectory") String repositoryDirectory,
                                          @Named("BackupRepositoryDirectory") String backupRepositoryDirectory,
                                          NamingScheme namingScheme) {
        super(repositoryDirectory, namingScheme);
        this.backupRepositoryDirectory = backupRepositoryDirectory;
        FileSystemUtils.createDirectory(backupRepositoryDirectory);

        // thread pool for the writer
        threadPoolExecutor = new ThreadPoolExecutor(NUM_THREADS, MAX_THREADS,
                30000L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }
    
    @Override
    public String addFileResource(FileResource fileResource) throws RepositoryStorageException {
        String id = super.addFileResource(fileResource);
        backup(id, fileResource);
        return id;
    }

    private void backup(String id, FileResource fileResource) {
        threadPoolExecutor.execute(new Writer(id, fileResource));
    }


    private class Writer implements Runnable {

        private final String id;
        private final FileResource fileResource;

        private Writer(String id, FileResource fileResource) {
            this.id = id;
            this.fileResource = fileResource;
        }

        @Override
        public void run() {
            String dirPath = getDirPath(id);
            try {

                assertDirectoryPresent(dirPath);

                writeFileResourceToDisk(fileResource, dirPath, id);
                writeMetadataToDisk(fileResource, dirPath, id);

            } catch (Exception e) {
                BACKUP_REPO_ERROR_LOG.log(Level.SEVERE, "write error: " + id + ", at path: " + dirPath + id, e);
            }
        }

        private String getDirPath(String id) {
            try {
                return backupRepositoryDirectory + namingScheme.getPath(id);
            } catch (FileNotFoundException e) {
                BACKUP_REPO_ERROR_LOG.severe("fatal! cannot determine file path for file with id: " + id);
                throw new RuntimeException(e);
            }
        }
    }

}
