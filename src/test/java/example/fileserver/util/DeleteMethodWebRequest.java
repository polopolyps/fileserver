package example.fileserver.util;

import com.meterware.httpunit.HeaderOnlyWebRequest;

public class DeleteMethodWebRequest extends HeaderOnlyWebRequest {

    public DeleteMethodWebRequest(String urlString) {
        super(urlString);
        method = "DELETE";
    }

}
