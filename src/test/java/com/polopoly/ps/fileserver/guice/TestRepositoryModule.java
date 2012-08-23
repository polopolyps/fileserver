package com.polopoly.ps.fileserver.guice;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.polopoly.ps.fileserver.counter.FileCounter;
import com.polopoly.ps.fileserver.guice.Configuration;
import com.polopoly.ps.fileserver.mocks.MockFileCounter;
import com.polopoly.ps.fileserver.repository.FileSystemRepository;
import com.polopoly.ps.fileserver.repository.NamingScheme;
import com.polopoly.ps.fileserver.repository.Repository;
import com.polopoly.ps.fileserver.repository.UnbalancedTreeNamingScheme;


public class TestRepositoryModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(Repository.class).to(FileSystemRepository.class);
//        bind(Repository.class).to(FileSystemWithBackupRepository.class);
        
        Configuration configuration = new Configuration("test");

        bindConstant().annotatedWith(Names.named("RepositoryDirectory")).to(configuration.getRepositoryDirectory());
        bindConstant().annotatedWith(Names.named("BackupRepositoryDirectory")).to(configuration.getBackupRepositoryDirectory());


        bind(NamingScheme.class).to(UnbalancedTreeNamingScheme.class);
        bind(FileCounter.class).to(MockFileCounter.class);

    }

}