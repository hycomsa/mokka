package pl.hycom.adapters.wiremock;


import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy;
import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.ValuePattern;
import com.github.tomakehurst.wiremock.stubbing.ListStubMappingsResult;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

import java.util.List;

/**
 * @author Jakub Muras <jakub.muras@hycom.pl>
 */
public class WireMockService {

    private final String host;
    private final int port;

    public WireMockService(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public List<StubMapping> listAll(){
        WireMock.configureFor(getHost(), getPort());
        ListStubMappingsResult stubs = WireMock.listAllStubMappings();
        return stubs.getMappings();
    }

    public StubMapping addMapping(StubMapping mapping){
        UrlMatchingStrategy urlMatchingStrategy = new UrlMatchingStrategy();
        urlMatchingStrategy.setUrl(mapping.getRequest().getUrl());
        urlMatchingStrategy.setUrlPath(mapping.getRequest().getUrlPath());
        urlMatchingStrategy.setUrlPathPattern(mapping.getRequest().getUrlPathPattern());
        urlMatchingStrategy.setUrlPattern(mapping.getRequest().getUrlPattern());
        MappingBuilder builder = new MappingBuilder(mapping.getRequest().getMethod(), urlMatchingStrategy);
        builder.willReturn(ResponseDefinitionBuilder.like(mapping.getResponse())).
                inScenario(mapping.getScenarioName()).
                whenScenarioStateIs(mapping.getRequiredScenarioState()).
                willSetStateTo(mapping.getNewScenarioState());
        for (ValuePattern valuePattern:mapping.getRequest().getBodyPatterns()) {
            builder.withRequestBody(convertValuePattern(valuePattern));
        }
        if(mapping.getRequest().getHeaders() != null){
            for (String key:mapping.getRequest().getHeaders().keySet()) {
                builder.withHeader(key, convertValuePattern(mapping.getRequest().getHeaders().get(key)));
            }
        }
        if(mapping.getRequest().getQueryParameters() != null){
            for (String key:mapping.getRequest().getQueryParameters().keySet()) {
                builder.withQueryParam(key, convertValuePattern(mapping.getRequest().getHeaders().get(key)));
            }
        }
        WireMock.givenThat(builder);
        WireMock.saveAllMappings();
        return builder.build();
    }

    private ValueMatchingStrategy convertValuePattern(ValuePattern valuePattern){
        if(valuePattern == null){
            return null;
        }
        ValueMatchingStrategy valueMatchingStrategy = new ValueMatchingStrategy();
        valueMatchingStrategy.setContains(valuePattern.getContains());
        valueMatchingStrategy.setMatches(valuePattern.getMatches());
        valueMatchingStrategy.setMatchingXPath(valuePattern.getMatchesXPath());
        valueMatchingStrategy.setDoesNotMatch(valuePattern.getDoesNotMatch());
        valueMatchingStrategy.setEqualTo(valuePattern.getEqualTo());
        valueMatchingStrategy.setEqualToJson(valuePattern.getEqualToJson());
        valueMatchingStrategy.setEqualToXml(valuePattern.getEqualToXml());
        valueMatchingStrategy.setJsonCompareMode(valuePattern.getJsonCompareMode());
        valueMatchingStrategy.setJsonMatchesPath(valuePattern.getMatchesJsonPath());
        valueMatchingStrategy.setXPathNamespaces(valuePattern.getWithXPathNamespaces());
        return valueMatchingStrategy;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
