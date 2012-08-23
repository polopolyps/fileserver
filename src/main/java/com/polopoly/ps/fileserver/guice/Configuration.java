package com.polopoly.ps.fileserver.guice;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.polopoly.ps.fileserver.repository.FileSystemRepository;
import com.polopoly.ps.fileserver.repository.FileSystemWithBackupRepository;
import com.polopoly.ps.fileserver.repository.Repository;


/**
 * File Server Configuration.
 *
 * It defines:
 * <ul>
 *     <li>Repository Implementation</li>
 *     <li>Repository folder(s)</li>
 * </ul>
 *
 * The ruleset is: <em>first check environment variables, then property file. </em>
 *
 * It is possible to configure which property file should be used
 * through the system property {@see Configuration.MODE}.
 * A given mode will be resolved to a property file using the naming convention: WEB-INF/{mode}.properties
 *
 */
public class Configuration {

    public static final String MODE = "fileserver.ExecutionMode";

    private static final String REPOSITORY_PATH_PROP = "fileserver.FileRepositoryPath";
    private static final String BACKUP_REPOSITORY_PATH_PROP = "fileserver.BackUpRepositoryPath";
    private static final String BACKUP_ACTIVE_PROP = "fileserver.ActivateBackUp";

    private Properties properties = null;

    /**
     * The mode will be read from a system property.
     * If the system properties is not set this will default to 'production'.
     *
     */
    public Configuration() {
        this((System.getProperty(MODE) != null) ? System.getProperty(MODE) : "production");
    }

    public Configuration(String mode) {
        properties = new Properties();
        String propertyFile = "/" + mode + ".properties";

        InputStream inputStream = getClass().getResourceAsStream(propertyFile);
        if (inputStream == null) throw new RuntimeException("Cannot read configuration from property file: " + propertyFile +
                ". Make sure that " + MODE + " points to a valid mode.");

        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Using this constructor will bypass the logic
     * that resolves the properties file from the MODE.
     *
     * This is used mainly in integration test.
     *
     * @param properties were the configuration is stored
     */
    public Configuration(Properties properties) {
        this.properties = properties;
    }

    public String getRepositoryDirectory() {
        return getProperty(REPOSITORY_PATH_PROP);
    }

    public String getBackupRepositoryDirectory() {
        return getProperty(BACKUP_REPOSITORY_PATH_PROP);
    }
    
    public Class<? extends Repository> getRepositoryImpl() {
        if (properties.get(BACKUP_ACTIVE_PROP).toString().trim().equals("true")) {
            return FileSystemWithBackupRepository.class;
        }
        return FileSystemRepository.class;
    }

    private String getProperty(String property) {
        return (System.getProperty(property) != null) ?
                System.getProperty(property) : properties.getProperty(property);
    }

}
