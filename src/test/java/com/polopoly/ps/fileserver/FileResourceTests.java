package com.polopoly.ps.fileserver;

import org.junit.Before;
import org.junit.Test;

import com.polopoly.ps.fileserver.repository.FileResource;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class FileResourceTests
{
    private final int arraySize = 10;
    private FileResource file;

    @Before
    public void initDataFiles() throws Exception
    {
        byte[] data = new byte[arraySize];
        for (int i = 0; i < arraySize; i++) {
            data[i] = (byte) i;
        }
        file = new FileResource("file.jpg", data);
    }

    @Test
    public void getData()
    {
        byte[] newDataRef = file.getData();
        for (int i = 0; i < arraySize; ++i) {
            assertEquals(i, newDataRef[i]);
        }
    }

    @Test
    public void getMetadata()
    {
        assertNotNull(file);
        assertNotNull(file.getMetadata());
        assertEquals("file.jpg", file.getMetadata().getFilename());
        assertEquals("image/jpeg", file.getMetadata().getMimeType());
    }


}
