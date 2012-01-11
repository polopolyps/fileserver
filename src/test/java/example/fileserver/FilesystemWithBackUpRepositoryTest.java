package example.fileserver;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import example.fileserver.counter.FileCounter;
import example.fileserver.guice.Configuration;
import example.fileserver.mocks.MockFileCounter;
import example.fileserver.repository.*;
import example.fileserver.util.FileSystemUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * @author gmola
 *         date: 12/14/11
 */
public class FilesystemWithBackUpRepositoryTest {

    private static final Configuration CONF = new Configuration("test");


    private Injector injector = Guice.createInjector(new AbstractModule() {
        @Override
        protected void configure() {

            bindConstant().annotatedWith(Names.named("RepositoryDirectory")).to(CONF.getRepositoryDirectory());
            bindConstant().annotatedWith(Names.named("BackupRepositoryDirectory")).to(CONF.getBackupRepositoryDirectory());

            bind(NamingScheme.class).to(UnbalancedTreeNamingScheme.class);
            bind(FileCounter.class).to(MockFileCounter.class);
        }
    });

    private FileSystemWithBackupRepository repository;
    private final String IMAGE = "src/test/fixtures/images/test_image.jpg";
    private NamingScheme namingScheme;

    @Before
    public void initRepo() throws Exception
    {
        namingScheme = injector.getInstance(NamingScheme.class);
        repository = injector.getInstance(FileSystemWithBackupRepository.class);
        cleanTemporaryFolders();
    }

    @AfterClass
    public static void cleanTemporaryFolders() {
        FileSystemUtils.deleteDirectory(new File(CONF.getRepositoryDirectory() + "0-99"));
        FileSystemUtils.deleteDirectory(new File(CONF.getBackupRepositoryDirectory() + "0-99"));
    }


    @Test( expected = RepositoryStorageException.class )
    public void getNonExistingImage() throws RepositoryStorageException {
        repository.getFileResource(IMAGE);
    }

    private FileResource fileResource(String filename) throws IOException {
        return new FileResource(IMAGE, FileSystemUtils.getBytesFromFile(filename));
    }

    @Test
    public void addOneFile() throws IOException, RepositoryStorageException, InterruptedException {
        String id = repository.addFileResource(fileResource(IMAGE));

        assertFileExistsInMainRepo(id);
        assertFileExistsInBackupRepo(id);
    }

    @Test
    public void addOneHundredFiles() throws IOException, RepositoryStorageException, InterruptedException {
        for (int i = 0; i < 99;  i++) {
            repository.addFileResource(fileResource(IMAGE));
        }
        // verify the 100th
        String id = repository.addFileResource(fileResource(IMAGE));
        assertFileExistsInMainRepo(id);
        assertFileExistsInBackupRepo(id);
    }

    private void assertFileExistsInBackupRepo(String id) throws FileNotFoundException, InterruptedException {
        // check at 200ms intervals if the file exists on the backup repo (writes are async!)
        int count = 0;
        while(true) {
            try {
                count++;
                assertTrue("file not found in repo: " + id, new File(CONF.getBackupRepositoryDirectory() + namingScheme.getPath(id)).exists());
                break;
            } catch (AssertionError e) {
                if (count == 5) throw e;  // Retry 5 times, max 1 sec
                Thread.sleep(200);
            }
        }
    }

    private void assertFileExistsInMainRepo(String id) throws FileNotFoundException {
        assertTrue("file not found in repo: " + id,
                new File(CONF.getRepositoryDirectory() + namingScheme.getPath(id)).exists());
    }

}
