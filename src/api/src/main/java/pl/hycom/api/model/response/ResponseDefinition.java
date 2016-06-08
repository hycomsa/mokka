package pl.hycom.api.model.response;

import com.github.tomakehurst.wiremock.common.Strings;

import java.util.Map;

import static com.google.common.base.Charsets.UTF_8;

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
        this.body = Strings.bytesFromString(content);
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

        return new String(bytes, UTF_8);
    }
}
