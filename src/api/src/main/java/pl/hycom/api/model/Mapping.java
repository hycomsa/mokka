package pl.hycom.api.model;

import pl.hycom.api.model.request.RequestPattern;
import pl.hycom.api.model.response.ResponseDefinition;

import java.util.Objects;

/**
 * model for mock mapping
 *
 * @author Michal Adamczyk, HYCOM S.A.
 */
public class Mapping {

    /**
     * mapping identifier
     */
    private String id;

    private String fromId;

    private String targetId;

    /**
     * request pattern
     */
    private RequestPattern requestPattern;

    /**
     * response definition
     */
    private ResponseDefinition responseDefinition;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public RequestPattern getRequestPattern() {
        return requestPattern;
    }

    public void setRequestPattern(RequestPattern requestPattern) {
        this.requestPattern = requestPattern;
    }

    public ResponseDefinition getResponseDefinition() {
        return responseDefinition;
    }

    public void setResponseDefinition(ResponseDefinition responseDefinition) {
        this.responseDefinition = responseDefinition;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Mapping)) {
            return false;
        }
        Mapping mapping = (Mapping) o;
        return Objects.equals(id, mapping.id) &&
                Objects.equals(requestPattern, mapping.requestPattern) &&
                Objects.equals(responseDefinition, mapping.responseDefinition);
    }

    @Override public int hashCode() {
        return Objects.hash(id, requestPattern, responseDefinition);
    }
}
