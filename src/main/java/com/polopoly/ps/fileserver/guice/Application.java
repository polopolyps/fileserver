package com.polopoly.ps.fileserver.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceServletContextListener;
import com.polopoly.ps.fileserver.counter.FileCounter;
import com.polopoly.ps.fileserver.counter.FileSystemFileCounter;
import com.polopoly.ps.fileserver.mime.DataMimeDetector;
import com.polopoly.ps.fileserver.mime.MimeDetector;
import com.polopoly.ps.fileserver.repository.NamingScheme;
import com.polopoly.ps.fileserver.repository.Repository;
import com.polopoly.ps.fileserver.repository.UnbalancedTreeNamingScheme;
import com.polopoly.ps.fileserver.resources.FileResourceResource;
import com.polopoly.ps.fileserver.resources.MetadataResource;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

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
