package example.fileserver;

import com.google.inject.Guice;
import com.google.inject.Injector;
import example.fileserver.counter.FileSystemFileCounter;
import example.fileserver.guice.TestRepositoryModule;
import example.fileserver.repository.FileSystemRepository;
import example.fileserver.util.FileSystemUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class FileSystemFileCounterTests
{
    private static final String CLASS = FileSystemFileCounterTests.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);

    private static final String TEST_IMAGE = "test_image.jpg";
    private static final String FIXTURE_IMAGE = "src/test/fixtures/images/" + TEST_IMAGE;

    private Injector injector = Guice.createInjector(new TestRepositoryModule());
    private FileSystemRepository repository;
    private FileSystemFileCounter counter;

    @Before
    public void initRepo()
    {
        repository = injector.getInstance(FileSystemRepository.class);
        FileSystemUtils.deleteDirectory(new File(repository.getPath()));
        FileSystemUtils.createDirectory(repository.getPath());
    }

    @Test
    public void getNext()
    {
        counter = new FileSystemFileCounter(repository.getPath());
        long first = counter.getNextFileCounter();
        assertEquals(1, first);
        File dir = new File(repository.getPath() + "0-99");
        dir.mkdirs();
        File f = new File(repository.getPath() + "0-99" + File.separator + "0");
        try {
            f.createNewFile();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to create file " + f.getAbsoluteFile());
            e.printStackTrace();
            assertTrue(false);
        }
        assertTrue(f.exists());
        long duo = counter.getNextFileCounter();
        assertEquals(2, duo);
    }

    @Test
    public void counterInitializationFromFileSystem()
    {
        FileSystemUtils.createDirectory(repository.getPath() + "0-99");
        FileSystemUtils.copyFile(FIXTURE_IMAGE, repository.getPath() + "0-99" + File.separator + "1");
        FileSystemUtils.copyFile(FIXTURE_IMAGE, repository.getPath() + "0-99" + File.separator + "2");
        counter = new FileSystemFileCounter(repository.getPath());
        assertEquals(3, counter.getNextFileCounter());
    }
    
    @Test
    public void counterInitializationFromFileSystemGap()
    {
        FileSystemUtils.createDirectory(repository.getPath() + "0-99");
        FileSystemUtils.copyFile(FIXTURE_IMAGE, repository.getPath() + "0-99" + File.separator + "1");
        FileSystemUtils.copyFile(FIXTURE_IMAGE, repository.getPath() + "0-99" + File.separator + "28");
        counter = new FileSystemFileCounter(repository.getPath());
        assertEquals(29, counter.getNextFileCounter());
    }
    
    @Test
    public void counterInitFromFileSystemTree() {
        FileSystemUtils.createDirectory(repository.getPath() + "0-999");
        FileSystemUtils.createDirectory(repository.getPath() + "0-999" + File.separator + "0-99");
        FileSystemUtils.createDirectory(repository.getPath() + "0-999" + File.separator + "100-199");
        for(int i = 0; i < 100; i++) {
            FileSystemUtils.copyFile(FIXTURE_IMAGE, repository.getPath() + "0-999" + File.separator + "0-99" + File.separator + String.valueOf(i));
        }
        FileSystemUtils.copyFile(FIXTURE_IMAGE, repository.getPath() + "0-999" + File.separator + "100-199" + File.separator + "101");
        FileSystemUtils.copyFile(FIXTURE_IMAGE, repository.getPath() + "0-999" + File.separator + "100-199" + File.separator + "102");
       
        counter = new FileSystemFileCounter(repository.getPath());
        assertEquals(103, counter.getNextFileCounter());
    }

    @Test
    public void garbageInTheRepository()
    {
        FileSystemUtils.createDirectory(repository.getPath() + "0-99");
        FileSystemUtils.copyFile(FIXTURE_IMAGE, repository.getPath() + "0-99" + File.separator + "1.garbage");
        counter = new FileSystemFileCounter(repository.getPath());
        assertEquals(1, counter.getNextFileCounter());
    }

}
