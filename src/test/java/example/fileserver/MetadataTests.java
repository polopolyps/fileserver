package example.fileserver;

import example.fileserver.mime.Metadata;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class MetadataTests {
    private String FILENAME = "foobar.jpg";
    private String MIMETYPE = "image/jpeg";
    private Metadata metadata = new Metadata(FILENAME, MIMETYPE);;
    

    @Test
    public void filename() {
        assertEquals(FILENAME, metadata.getFilename());
    }

    @Test
    public void mimeType() {
        assertEquals(MIMETYPE, metadata.getMimeType());
    }

    @Test
    public void load() {
        Metadata m = new Metadata("", "");
        m.load("filename=" + FILENAME + "\nmimeType=" + MIMETYPE + "\n");
        assertEquals(FILENAME, m.getFilename());
        assertEquals(MIMETYPE, m.getMimeType());
        
    }  
}
