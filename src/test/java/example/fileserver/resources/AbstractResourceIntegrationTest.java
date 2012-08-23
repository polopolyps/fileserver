package example.fileserver.resources;

import java.io.File;

import org.junit.Before;

import com.google.inject.Guice;
import com.meterware.httpunit.WebConversation;

import example.fileserver.guice.TestRepositoryModule;
import example.fileserver.repository.FileSystemRepository;
import example.fileserver.util.FileSystemUtils;


public abstract class AbstractResourceIntegrationTest {

    protected static final String HOST = "http://0.0.0.0:8082";
    protected static final String WEBAPP = HOST + "/fileserver/";

    protected static final String TEST_IMAGE = "test_image.jpg";
    protected static final String BIG_TEST_IMAGE = "big_image.jpg";
    protected static final String WRONG_EXTENSION_TEST_IMAGE = "jpg_image.gif";
    protected static final String FIXTURE_IMAGE = "src/test/fixtures/images/" + TEST_IMAGE;
    protected static final String BIG_FIXTURE_IMAGE = "src/test/fixtures/images/" + BIG_TEST_IMAGE;
    protected static final String WRONG_EXTENSION_FIXTURE_IMAGE = "src/test/fixtures/images/"
                                                                  + WRONG_EXTENSION_TEST_IMAGE;
    protected static final String FIXTURE_METADATA = "src/test/fixtures/metadata/" + TEST_IMAGE + ".metadata";

    protected WebConversation wc;
    protected FileSystemRepository repository;

    @Before
    public void setUp() {
        repository =  Guice.createInjector(new TestRepositoryModule()).getInstance(FileSystemRepository.class);
        wc = new WebConversation();
        wc.setExceptionsThrownOnErrorStatus(false);

        FileSystemUtils.deleteDirectory(new File(repository.getPath()));
        FileSystemUtils.createDirectory(repository.getPath());
    }

    protected void copyTestImageToRepository() {
        FileSystemUtils.createDirectory(repository.getPath() + "0-99");
        FileSystemUtils.copyFile(FIXTURE_IMAGE, repository.getPath() + "0-99/0");
        FileSystemUtils.copyFile(FIXTURE_METADATA, repository.getPath() + "0-99/0.metadata");
    }

}
