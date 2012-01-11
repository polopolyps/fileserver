package example.fileserver.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import example.fileserver.counter.FileCounter;
import example.fileserver.counter.FileSystemFileCounter;
import example.fileserver.mime.DataMimeDetector;
import example.fileserver.mime.MimeDetector;
import example.fileserver.repository.NamingScheme;
import example.fileserver.repository.Repository;
import example.fileserver.repository.UnbalancedTreeNamingScheme;
import example.fileserver.resources.FileResourceResource;
import example.fileserver.resources.MetadataResource;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Application extends GuiceServletContextListener
{
    private static final Logger LOG = Logger.getLogger(Application.class.getName());

    @Override
    protected Injector getInjector()
    {
        LOG.log(Level.INFO, "Invoking Application.");
        return Guice.createInjector(new JerseyServletModule() {

            @Override
            protected void configureServlets()
            {

                // resources
                bind(FileResourceResource.class);
                bind(MetadataResource.class);


                // IOC
                bind(NamingScheme.class).to(UnbalancedTreeNamingScheme.class);
                bind(FileCounter.class).to(FileSystemFileCounter.class);
                bind(MimeDetector.class).to(DataMimeDetector.class);

                Configuration config = new Configuration();
                bindConstant().annotatedWith(Names.named("RepositoryDirectory")).to(config.getRepositoryDirectory());
                bindConstant().annotatedWith(Names.named("BackupRepositoryDirectory")).to(config.getBackupRepositoryDirectory());
                bind(Repository.class).to(config.getRepositoryImpl());

                
                // Servlet mapping
                serve("/*").with(GuiceContainer.class);

            }
        });
    }


}
