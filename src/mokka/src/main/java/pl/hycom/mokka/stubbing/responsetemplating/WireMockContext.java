package pl.hycom.mokka.stubbing.responsetemplating;

import com.github.tomakehurst.wiremock.http.Request;
import lombok.Getter;

/**
 * Keeps information about WireMock context for groovy binding.
 *
 * @author Bartosz Kuron (bartosz.kuron@hycom.pl)
 */
@Getter
public class WireMockContext {

    private String uri;

    private String httpMethod;

    private String requestBody;

    private String from;

    public WireMockContext(Request request) {
        this.uri = request.getUrl();
        this.httpMethod = request.getMethod().toString();
        this.requestBody = request.getBodyAsString();
        this.from = request.getClientIp();
    }
}
