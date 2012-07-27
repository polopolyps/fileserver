package example.fileserver.guice;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import example.fileserver.counter.FileCounter;
import example.fileserver.mocks.MockFileCounter;
import example.fileserver.repository.FileSystemRepository;
import example.fileserver.repository.NamingScheme;
import example.fileserver.repository.Repository;
import example.fileserver.repository.UnbalancedTreeNamingScheme;

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