package pl.hycom.mokka.stubbing;

import com.github.tomakehurst.wiremock.client.BasicCredentials;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.CustomMatcherDefinition;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.hycom.mokka.emulator.mock.model.GroovyConfigurationContent;
import pl.hycom.mokka.emulator.mock.model.MockConfiguration;
import pl.hycom.mokka.stubbing.responsetemplating.GroovyResponseTransformer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author adam.misterski@hycom.pl
 */
@Component
public class WireMockStubMappingConverter implements Converter<MockConfiguration, StubMapping> {

    private static final String SLASH = "/";

    @Override
    public StubMapping convert(MockConfiguration mockConfiguration) {

        StubMapping stubMapping = new StubMapping();

        if(mockConfiguration.getPath() != null || mockConfiguration.getHttpMethod() != null) {
            RequestPattern requestPattern = new RequestPattern(SLASH + mockConfiguration.getPath(), (String) null, (String) null, (String) null, RequestMethod.fromString(mockConfiguration.getHttpMethod()), (Map) null, (Map) null, (Map) null, (BasicCredentials) null, (List) null, (CustomMatcherDefinition) null, (List) null);

            if(mockConfiguration.getId() != null){
                stubMapping.setId(UUID.nameUUIDFromBytes(mockConfiguration.getId().toString().getBytes()));
            }
            if(mockConfiguration.getName() != null){
                stubMapping.setName(mockConfiguration.getName());
            }
            stubMapping.setPriority(mockConfiguration.getOrder());

            ResponseDefinitionBuilder responseDefinitionBuilder = ResponseDefinitionBuilder.responseDefinition();
            responseDefinitionBuilder.withStatus(mockConfiguration.getStatus());

            if(mockConfiguration.getConfigurationContent() != null){
                responseDefinitionBuilder.withBody(mockConfiguration.getConfigurationContent().getValue());
            }

            responseDefinitionBuilder.withFixedDelay(mockConfiguration.getTimeout());
            responseDefinitionBuilder.proxiedFrom(mockConfiguration.getProxyBaseUrl());

            if(mockConfiguration.getConfigurationContent() instanceof GroovyConfigurationContent) {
                responseDefinitionBuilder.withTransformers(GroovyResponseTransformer.GROOVY_TRANSFORMER);
            }


            stubMapping.setRequest(requestPattern);
            stubMapping.setResponse(responseDefinitionBuilder.build());
        } else{
            throw new MalformedMockConfigurationException("Path or HttpMethod is not set on MockConfiguration");
        }

        return stubMapping;
    }

}
