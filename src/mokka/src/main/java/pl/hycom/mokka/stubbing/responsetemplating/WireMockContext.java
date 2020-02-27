package pl.hycom.mokka.stubbing.responsetemplating;

import com.github.tomakehurst.wiremock.http.Request;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

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
        this.uri = StringUtils.removeStart((String) request.getUrl(), "/");
        this.httpMethod = request.getMethod().toString();
        this.requestBody = request.getBodyAsString();
        this.from = request.getClientIp();
    }

    @Override
    public String toString() {
        return "WireMockContext{" +
            "uri='" + uri + '\'' +
            ", httpMethod='" + httpMethod + '\'' +
            ", requestBody='" + requestBody + '\'' +
            ", from='" + from + '\'' +
            '}';
    }
}
