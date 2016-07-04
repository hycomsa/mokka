package pl.hycom.api.model.response;


import java.nio.charset.Charset;
import java.util.Map;

/**
 * Model for response definition
 * @author Michal Adamczyk, HYCOM S.A.
 */
public class ResponseDefinition {

    private int status;
    private byte[] body;
    private Map<String, String> headers;

    public ResponseDefinition() {
    }

    public ResponseDefinition(String content, int status) {
        this.body = content.getBytes(Charset.forName("UTF-8"));
        this.status = status;
    }

    public ResponseDefinition(byte[] body, int status) {
        this.body = body;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBodyAsString() {
        return body != null ? stringFromBytes(body) : null;
    }

    public String stringFromBytes(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        return new String(bytes, Charset.forName("UTF-8"));
    }
}
