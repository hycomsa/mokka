package pl.hycom.api.converter.wiremock;

import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import pl.hycom.api.model.Mapping;
import pl.hycom.api.model.request.RequestMethod;
import pl.hycom.api.model.request.RequestPattern;
import pl.hycom.api.model.response.ResponseDefinition;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Jakub Muras <jakub.muras@hycom.pl>
 */
public class WiremockMappingConverter {

    private WiremockMappingConverter(){}

    public static Mapping convertToMapping(StubMapping mapping){
        Mapping result = new Mapping();
        result.setId(mapping.getScenarioName());

        RequestPattern requestPattern = new RequestPattern();
        if(mapping.getRequest().getMethod() != null){
            requestPattern.setRequestMethod(RequestMethod.valueOf(mapping.getRequest().getMethod().toString()));
        }
        requestPattern.setUrl(mapping.getRequest().getUrl());
        requestPattern.setUrlPath(mapping.getRequest().getUrlPath());
        requestPattern.setUrlPathPattern(mapping.getRequest().getUrlPathPattern());
        requestPattern.setUrlPattern(mapping.getRequest().getUrlPattern());
        requestPattern.setBodyPatterns(ValuePatternConverter.convertToValPatternList(mapping.getRequest().getBodyPatterns()));
        requestPattern.setHeaderPatterns(ValuePatternConverter.convertToValPatternMap(mapping.getRequest().getHeaders()));
        requestPattern.setQueryParamPatterns(ValuePatternConverter.convertToValPatternMap(mapping.getRequest().getQueryParameters()));
        ResponseDefinition responseDefinition = null;
        if(mapping.getResponse().getBody() != null){
            responseDefinition = new ResponseDefinition(mapping.getResponse().getBody().getBytes(), mapping.getResponse().getStatus());
        }else if(mapping.getResponse().getByteBody() != null){
            responseDefinition = new ResponseDefinition(mapping.getResponse().getByteBody(), mapping.getResponse().getStatus());
        }

        result.setRequestPattern(requestPattern);
        result.setResponseDefinition(responseDefinition);
        return result;
    }

    public static StubMapping convertToStubMapping(Mapping mapping){
        StubMapping result = new StubMapping();
        result.setScenarioName(mapping.getId());
        result.setRequiredScenarioState(mapping.getFromId());
        result.setNewScenarioState(mapping.getTargetId());

        com.github.tomakehurst.wiremock.matching.RequestPattern requestPattern = new com.github.tomakehurst.wiremock.matching.RequestPattern();
        requestPattern.setUrl(mapping.getRequestPattern().getUrl());
        requestPattern.setUrlPath(mapping.getRequestPattern().getUrlPath());
        requestPattern.setUrlPattern(mapping.getRequestPattern().getUrlPattern());
        requestPattern.setUrlPathPattern(mapping.getRequestPattern().getUrlPathPattern());
        requestPattern.setMethod(com.github.tomakehurst.wiremock.http.RequestMethod.fromString(mapping.getRequestPattern().getRequestMethod().name()));
        requestPattern.setBodyPatterns(ValuePatternConverter.convertToValuePatternList(mapping.getRequestPattern().getBodyPatterns()));
        requestPattern.setQueryParameters(ValuePatternConverter.convertToValuePatternMap(mapping.getRequestPattern().getQueryParamPatterns()));
        requestPattern.setHeaders(ValuePatternConverter.convertToValuePatternMap(mapping.getRequestPattern().getHeaderPatterns()));

        result.setResponse(new com.github.tomakehurst.wiremock.http.ResponseDefinition(mapping.getResponseDefinition().getStatus(), mapping.getResponseDefinition().getBody()));
        result.setRequest(requestPattern);

        return result;
    }

    public static List<Mapping> convertToMapping(List<StubMapping> mappings){
        List<Mapping> result = new LinkedList<>();
        for (StubMapping val: mappings) {
            result.add(convertToMapping(val));
        }
        return result;
    }
}
