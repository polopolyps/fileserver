package com.polopoly.ps.fileserver;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.polopoly.ps.fileserver.guice.TestRepositoryModule;
import com.polopoly.ps.fileserver.repository.NamingScheme;
import com.polopoly.ps.fileserver.repository.UnbalancedTreeNamingScheme;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static junit.framework.Assert.assertEquals;

public class UnbalancedTreeNamingSchemeTests
{
    private static final String CLASS = UnbalancedTreeNamingSchemeTests.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);
    
    private Injector injector = Guice.createInjector(new TestRepositoryModule());
    private NamingScheme scheme;

    @Before
    public void init() throws Exception
    {
        scheme = injector.getInstance(UnbalancedTreeNamingScheme.class);
    }

    @Test
    public void newFile()
    {
        assertEquals("0", scheme.newFile());
        assertEquals("1", scheme.newFile());
    }

    @Test
    public void createAHundredFiles()
    {
        try {
            for (int i = 0; i < 99; ++i) {
                scheme.newFile();
            }
            assertEquals("0-99" + File.separator, scheme.getPath(scheme.newFile()));
            String x = scheme.getPath(scheme.newFile());
            assertEquals("100-9999" + File.separator + "100-199" + File.separator, x);
        } catch (FileNotFoundException e) {
            LOG.log(Level.SEVERE, "Unexpected error.",e);
        }
    }

    @Test
    public void createTwoHundredFiles()
    {
        try {
            for (int i = 0; i < 200; ++i) {
                scheme.newFile();
            }
            assertEquals("100-9999" + File.separator + "200-299" + File.separator, scheme.getPath(scheme.newFile()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createTenThousandFiles()
    {
        try {
            for (int i = 0; i < 10000; ++i) {
                scheme.newFile();
            }
            assertEquals("10000-999999" + File.separator + "10000-19999" + File.separator + "10000-10099" + File.separator, scheme.getPath(scheme.newFile()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
