package example.fileserver.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import example.fileserver.util.FileSystemUtils;
import example.fileserver.mime.Metadata;

@Singleton
public class FileSystemRepository implements Repository {
    private static final String CLASS = FileSystemRepository.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);

    protected NamingScheme namingScheme;
    private String repositoryDirectory;

    public String getPath() {
        return this.repositoryDirectory;
    }

    @Inject
    public FileSystemRepository(@Named("RepositoryDirectory") String repositoryDirectory, NamingScheme namingScheme) {
        this.repositoryDirectory = repositoryDirectory;
        this.namingScheme = namingScheme;
        FileSystemUtils.createDirectory(repositoryDirectory);
    }

    @Override
    public synchronized String addFileResource(FileResource fileResource) throws RepositoryStorageException {
        try {
            final String id = namingScheme.newFile();
            String dirPath = repositoryDirectory + namingScheme.getPath(id);

            assertDirectoryPresent(dirPath);

            writeFileResourceToDisk(fileResource, dirPath, id);

            writeMetadataToDisk(fileResource, dirPath, id);

            return id;

        } catch (Exception e) {
            throw new RepositoryStorageException("Could not add file resource " + fileResource, e);
        }
    }

    protected void assertDirectoryPresent(String dirPath) throws FileNotFoundException {
        File dir = new File(dirPath);
        if (dir.exists() != true) {
            dir.mkdirs();
        }
    }

    protected void writeMetadataToDisk(FileResource fileResourcee, String dirPath, String id) throws IOException {
        File file = new File(dirPath + id + ".metadata");
        file.createNewFile();
        OutputStream output = null;
        try {
            output = new FileOutputStream(file);
            output.write(fileResourcee.getMetadata().toString().getBytes());
            output.flush();
        } catch (FileNotFoundException e) {
            throw new IOException("File " + file.getAbsoluteFile() + " was expected to exist already.");
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    protected void writeFileResourceToDisk(FileResource fileResource, String dirPath, String id) throws IOException {
        File file = new File(dirPath + id);
        file.createNewFile();
        OutputStream output = null;
        try {
            output = new FileOutputStream(file);
            LOG.log(Level.FINE, "Writing " + fileResource.getData().length + " bytes to " + file.getAbsoluteFile());
            output.write(fileResource.getData());
            fileResource.getMetadata().setPath(file.getCanonicalPath());
        } catch (FileNotFoundException e) {
            throw new IOException("File " + file.getAbsoluteFile() + " was expected to exist already.");
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    @Override
    public FileResource getFileResource(String id) throws RepositoryStorageException {
        byte[] data = null;
        String path = null;
        try {
            path = namingScheme.getPath(id);
            data = FileSystemUtils.getBytesFromFile(repositoryDirectory + path + id);
        } catch (IOException e) {
            throw new RepositoryStorageException("Could not open file " + repositoryDirectory + path + id, e);
        }
        return new FileResource(id, data);
    }

    @Override
    public Metadata getMetadata(String id) throws RepositoryStorageException {
        byte[] data = null;
        String path = null;
        try {
            path = namingScheme.getPath(id);
            data = FileSystemUtils.getBytesFromFile(repositoryDirectory + path + id + ".metadata");
        } catch (IOException e) {
            throw new RepositoryStorageException("Could not open file " + repositoryDirectory + path + id, e);
        }
        return (new Metadata()).load(new String(data));
    }

}
