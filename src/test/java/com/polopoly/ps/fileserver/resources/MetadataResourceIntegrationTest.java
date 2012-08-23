package com.polopoly.ps.fileserver.resources;

import java.io.IOException;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebResponse;
import com.polopoly.ps.fileserver.resources.MetadataResource;

import org.junit.Test;
import org.xml.sax.SAXException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;


public class MetadataResourceIntegrationTest extends AbstractResourceIntegrationTest {
    private static final String URL = WEBAPP + MetadataResource.RESOURCE + "/";

    @Test
    public void getMetadata() throws IOException, SAXException {
        copyTestImageToRepository();
        GetMethodWebRequest req = new GetMethodWebRequest(URL + "0");
        WebResponse resp = wc.getResponse(req);

        assertNotNull("Response was null", resp);
        assertEquals(200, resp.getResponseCode());
        assertEquals("text/plain", resp.getContentType());
        int contentLength = resp.getContentLength();
        byte[] data = new byte[contentLength];
        resp.getInputStream().read(data);
        String str = new String(data);
        assertEquals("filename=" + TEST_IMAGE + "\nmimeType=image/jpeg\npath=" + repository.getPath() + "0-99/0\n",
                str);

    }




}