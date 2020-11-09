package pl.hycom.mokka.stubbing;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.hycom.mokka.emulator.mock.model.GroovyConfigurationContent;
import pl.hycom.mokka.emulator.mock.model.MockConfiguration;
import pl.hycom.mokka.stubbing.responsetemplating.GroovyResponseTransformer;

import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * @author adam.misterski@hycom.pl
 */
@Slf4j
@Component
public class WireMockStubMappingConverter implements Converter<MockConfiguration, StubMapping> {

    private static final String SLASH = "/";

    @Override
    public StubMapping convert(MockConfiguration mockConfiguration) {
        log.debug("Converting mock [id={}] to StubMapping.", mockConfiguration.getId());

        if (isBlank(mockConfiguration.getHttpMethod()) || isBlank(mockConfiguration.getPath())) {
            throw new MalformedMockConfigurationException("Path or HttpMethod is not set on MockConfiguration");
        }

        StubMapping stubMapping = new StubMapping();
        if (mockConfiguration.getId() != null) {
            stubMapping.setId(UUID.nameUUIDFromBytes(mockConfiguration.getId().toString().getBytes()));
        }

        RequestPatternBuilder requestPatternBuilder = RequestPatternBuilder
            .newRequestPattern(RequestMethod.fromString(mockConfiguration.getHttpMethod()),
                               UrlPattern.fromOneOf(null, null, null, SLASH + mockConfiguration.getPath()));

        if (StringUtils.isNotBlank(mockConfiguration.getPattern())) {
            requestPatternBuilder.withRequestBody(new RegexPattern(mockConfiguration.getPattern()));
        }

        stubMapping.setRequest(requestPatternBuilder.build());

        stubMapping.setName(defaultIfBlank(mockConfiguration.getName(), ""));
        stubMapping.setPriority(mockConfiguration.getOrder());

        ResponseDefinitionBuilder responseDefinitionBuilder = ResponseDefinitionBuilder.responseDefinition();
        responseDefinitionBuilder.withStatus(mockConfiguration.getStatus());

        if (mockConfiguration.getConfigurationContent() != null) {
            responseDefinitionBuilder.withBody(mockConfiguration.getConfigurationContent().getValue());
        }

        responseDefinitionBuilder.withFixedDelay(mockConfiguration.getTimeout());
        responseDefinitionBuilder.proxiedFrom(mockConfiguration.getProxyBaseUrl());

        if (mockConfiguration.getConfigurationContent() instanceof GroovyConfigurationContent) {
            log.debug("Mock [id={}] has groovy content. Adding [{}] transformer to StubMapping",
                      mockConfiguration.getId(), GroovyResponseTransformer.GROOVY_TRANSFORMER);
            responseDefinitionBuilder.withTransformers(GroovyResponseTransformer.GROOVY_TRANSFORMER);
        }

        stubMapping.setResponse(responseDefinitionBuilder.build());

        log.debug("Mock [id={}] converted to corresponding StubMapping [id={}]", mockConfiguration.getId(),
                  stubMapping.getId());

        return stubMapping;
    }

}
