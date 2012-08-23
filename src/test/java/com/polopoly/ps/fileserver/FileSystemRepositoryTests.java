package com.polopoly.ps.fileserver;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.polopoly.ps.fileserver.guice.TestRepositoryModule;
import com.polopoly.ps.fileserver.repository.FileResource;
import com.polopoly.ps.fileserver.repository.FileSystemRepository;
import com.polopoly.ps.fileserver.repository.RepositoryStorageException;
import com.polopoly.ps.fileserver.util.FileSystemUtils;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class FileSystemRepositoryTests
{
    private static final String CLASS = FileSystemRepositoryTests.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);

    private static final String TEST_IMAGE = "test_image.jpg";
    private static final String FIXTURE_IMAGE = "src/test/fixtures/images/" + TEST_IMAGE;

    private Injector injector = Guice.createInjector(new TestRepositoryModule());
    private FileSystemRepository repository;
    private final String IMAGE = "test_image.jpg";

    @Before
    public void initRepo() throws Exception
    {
        repository = injector.getInstance(FileSystemRepository.class);
        File file = new File(repository.getPath() + IMAGE);
        file.delete();
        FileSystemUtils.deleteDirectory(new File(repository.getPath() + "0-99"));
    }

    @Test
    public void getNonExistingImage()
    {
        boolean exceptionCaught = false;
        try {
            repository.getFileResource("test_image.jpg");
        } catch (RepositoryStorageException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }



    private String addFile(String filename)
    {
        String id = null;
        try {
            id = repository.addFileResource(new FileResource(IMAGE, FileSystemUtils.getBytesFromFile(filename)));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Could not open fixture file. " + e);
            e.printStackTrace();
            assertTrue(false);
        }
        return id;
    }

    @Test
    public void addFile()
    {
        String id = addFile("src/test/fixtures/images/" + IMAGE);
        assertEquals("0", id);
    }

    @Test
    public void garbageInTheRepository()
    {
        FileSystemUtils.createDirectory(repository.getPath() + "0-99");
        FileSystemUtils.copyFile(FIXTURE_IMAGE, repository.getPath() + "0-99" + File.separator + "0.garbage");
        String id = addFile("src/test/fixtures/images/" + IMAGE);
        try {
            FileResource i = repository.getFileResource(id);
            assertNotNull(i);
        } catch (RepositoryStorageException e) {
            assertTrue(false);
        }
        assertEquals("0", id);
    }
}
