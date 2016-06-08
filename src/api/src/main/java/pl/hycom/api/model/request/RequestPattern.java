package pl.hycom.api.model.request;

import java.util.List;
import java.util.Map;

/**
 * Model for request pattern
 * @author Michal Adamczyk, HYCOM S.A.
 */
public class RequestPattern {

    /**
     * request method for this request pattern
     */
    private RequestMethod requestMethod;
    private String urlPattern;
    private String url;
    private String urlPath;
    private String urlPathPattern;
    private Map<String, ValPattern> headerPatterns;
    private Map<String, ValPattern> queryParamPatterns;
    private List<ValPattern> bodyPatterns;
    private List<HeaderValPattern> headers;
    private List<HeaderValPattern> parameters;

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public String getUrlPathPattern() {
        return urlPathPattern;
    }

    public void setUrlPathPattern(String urlPathPattern) {
        this.urlPathPattern = urlPathPattern;
    }

    public Map<String, ValPattern> getHeaderPatterns() {
        return headerPatterns;
    }

    public void setHeaderPatterns(Map<String, ValPattern> headerPatterns) {
        this.headerPatterns = headerPatterns;
    }

    public Map<String, ValPattern> getQueryParamPatterns() {
        return queryParamPatterns;
    }

    public void setQueryParamPatterns(Map<String, ValPattern> queryParamPatterns) {
        this.queryParamPatterns = queryParamPatterns;
    }

    public List<ValPattern> getBodyPatterns() {
        return bodyPatterns;
    }

    public void setBodyPatterns(List<ValPattern> bodyPatterns) {
        this.bodyPatterns = bodyPatterns;
    }

    public List<HeaderValPattern> getHeaders() {
        return headers;
    }

    public void setHeaders(List<HeaderValPattern> headers) {
        this.headers = headers;
    }

    public List<HeaderValPattern> getParameters() {
        return parameters;
    }

    public void setParameters(List<HeaderValPattern> parameters) {
        this.parameters = parameters;
    }
}
