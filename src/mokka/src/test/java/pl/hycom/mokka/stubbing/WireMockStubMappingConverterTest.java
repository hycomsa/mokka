package pl.hycom.mokka.stubbing;

import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.junit.jupiter.api.Test;
import pl.hycom.mokka.emulator.mock.model.ConfigurationContent;
import pl.hycom.mokka.emulator.mock.model.GroovyConfigurationContent;
import pl.hycom.mokka.emulator.mock.model.MockConfiguration;
import pl.hycom.mokka.emulator.mock.model.StringConfigurationContent;
import pl.hycom.mokka.emulator.mock.model.XmlConfigurationContent;
import pl.hycom.mokka.stubbing.responsetemplating.GroovyResponseTransformer;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author adam.misterski@hycom.pl
 */
public class WireMockStubMappingConverterTest {

    private static final Long id = 1L;
    private static final String name = "test";
    private static final int order = 0;
    private static final String httpMethod = "GET";
    private static final int timeout = 1;
    private static final int status = 200;
    private static final String path = "path";
    private static final String proxy = "http://proxyserver.com";
    private static final String slash = "/";
    private static final ConfigurationContent stringConfigurationContent = new StringConfigurationContent();
    private static final ConfigurationContent xmlConfigurationContent = new XmlConfigurationContent();
    private static final ConfigurationContent groovyConfigurationContent = new GroovyConfigurationContent();

    WireMockStubMappingConverter wireMockStubMappingConverter = new WireMockStubMappingConverter();

    @Test
    public void convertTo_Should_Convert_When_ConfiurationContentIsStringConfigurationContent() {
        //given
        MockConfiguration mockConfiguration = new MockConfiguration();
        mockConfiguration.setId(id);
        mockConfiguration.setName(name);
        mockConfiguration.setOrder(order);
        mockConfiguration.setHttpMethod(httpMethod);
        mockConfiguration.setTimeout(timeout);
        mockConfiguration.setStatus(status);
        mockConfiguration.setPath(path);
        mockConfiguration.setConfigurationContent(stringConfigurationContent);
        mockConfiguration.setProxyBaseUrl(proxy);

        //when
        StubMapping stubMapping = wireMockStubMappingConverter.convert(mockConfiguration);

        //then
        assertEquals(stubMapping.getId(), UUID.nameUUIDFromBytes(mockConfiguration.getId().toString().getBytes()));
        assertEquals(stubMapping.getName(), mockConfiguration.getName());
        assertEquals(stubMapping.getPriority(), mockConfiguration.getOrder());
        assertEquals(stubMapping.getRequest().getUrl(),slash + mockConfiguration.getPath());
        assertEquals(stubMapping.getRequest().getMethod().toString(), mockConfiguration.getHttpMethod());
        assertEquals(stubMapping.getResponse().getFixedDelayMilliseconds(), mockConfiguration.getTimeout());
        assertEquals(stubMapping.getResponse().getStatus(), mockConfiguration.getStatus());
        assertEquals(stubMapping.getResponse().getBody(), mockConfiguration.getConfigurationContent().getValue());
        assertEquals(stubMapping.getResponse().getProxyBaseUrl(), mockConfiguration.getProxyBaseUrl());
    }

    @Test
    public void convertTo_ShouldConvertRegexpAsBodyPattern() {
        //given
        MockConfiguration mockConfiguration = new MockConfiguration();
        mockConfiguration.setId(id);
        mockConfiguration.setHttpMethod(httpMethod);
        mockConfiguration.setPath(path);
        mockConfiguration.setPattern("([a-z]*)");

        //when
        StubMapping stubMapping = wireMockStubMappingConverter.convert(mockConfiguration);

        //then
        assertEquals(1, stubMapping.getRequest().getBodyPatterns().size());
        assertTrue(stubMapping.getRequest().getBodyPatterns().get(0) instanceof RegexPattern);
        assertEquals(mockConfiguration.getPattern(), stubMapping.getRequest().getBodyPatterns().get(0).getValue());
    }

    @Test
    public void convertTo_Should_Convert_When_ConfiurationContentIsXMLConfigurationContent() {
        //given
        MockConfiguration mockConfiguration = new MockConfiguration();
        mockConfiguration.setId(id);
        mockConfiguration.setName(name);
        mockConfiguration.setOrder(order);
        mockConfiguration.setHttpMethod(httpMethod);
        mockConfiguration.setTimeout(timeout);
        mockConfiguration.setStatus(status);
        mockConfiguration.setPath(path);
        mockConfiguration.setConfigurationContent(xmlConfigurationContent);
        mockConfiguration.setProxyBaseUrl(proxy);

        //when
        StubMapping stubMapping = wireMockStubMappingConverter.convert(mockConfiguration);

        //then
        assertEquals(stubMapping.getId(), UUID.nameUUIDFromBytes(mockConfiguration.getId().toString().getBytes()));
        assertEquals(stubMapping.getName(), mockConfiguration.getName());
        assertEquals(stubMapping.getPriority(), mockConfiguration.getOrder());
        assertEquals(stubMapping.getRequest().getUrl(),slash + mockConfiguration.getPath());
        assertEquals(stubMapping.getRequest().getMethod().toString(), mockConfiguration.getHttpMethod());
        assertEquals(stubMapping.getResponse().getFixedDelayMilliseconds(), mockConfiguration.getTimeout());
        assertEquals(stubMapping.getResponse().getStatus(), mockConfiguration.getStatus());
        assertEquals(stubMapping.getResponse().getBody(), mockConfiguration.getConfigurationContent().getValue());
        assertEquals(stubMapping.getResponse().getProxyBaseUrl(), mockConfiguration.getProxyBaseUrl());
    }

    @Test
    public void convertTo_Should_Convert_When_ConfiurationContentIsGroovyConfigurationContent() {
        //given
        MockConfiguration mockConfiguration = new MockConfiguration();
        mockConfiguration.setId(id);
        mockConfiguration.setName(name);
        mockConfiguration.setOrder(order);
        mockConfiguration.setHttpMethod(httpMethod);
        mockConfiguration.setTimeout(timeout);
        mockConfiguration.setStatus(status);
        mockConfiguration.setPath(path);
        mockConfiguration.setConfigurationContent(groovyConfigurationContent);
        mockConfiguration.setProxyBaseUrl(proxy);

        //when
        StubMapping stubMapping = wireMockStubMappingConverter.convert(mockConfiguration);

        //then
        assertEquals(stubMapping.getId(), UUID.nameUUIDFromBytes(mockConfiguration.getId().toString().getBytes()));
        assertEquals(stubMapping.getName(), mockConfiguration.getName());
        assertEquals(stubMapping.getPriority(), mockConfiguration.getOrder());
        assertEquals(stubMapping.getRequest().getUrl(),slash + mockConfiguration.getPath());
        assertEquals(stubMapping.getRequest().getMethod().toString(), mockConfiguration.getHttpMethod());
        assertEquals(stubMapping.getResponse().getFixedDelayMilliseconds(), mockConfiguration.getTimeout());
        assertEquals(stubMapping.getResponse().getStatus(), mockConfiguration.getStatus());
        assertEquals(stubMapping.getResponse().getBody(), mockConfiguration.getConfigurationContent().getValue());
        assertEquals(stubMapping.getResponse().getProxyBaseUrl(), mockConfiguration.getProxyBaseUrl());
    }

    @Test
    public void convertTo_Shouldnt_Convert_When_HttpMethodIsNull(){
        //given
        MockConfiguration mockConfiguration = new MockConfiguration();
        mockConfiguration.setPath(path);
        assertThrows(MalformedMockConfigurationException.class, () -> {
            wireMockStubMappingConverter.convert(mockConfiguration);
        });
    }

    @Test
    public void convertTo_Shouldnt_Convert_When_PathIsNull(){
        //given
        MockConfiguration mockConfiguration = new MockConfiguration();
        mockConfiguration.setHttpMethod(httpMethod);
        assertThrows(MalformedMockConfigurationException.class, () -> {
            wireMockStubMappingConverter.convert(mockConfiguration);
        });
    }

    @Test
    public void groovyTransformer_Should_Be_Added_When_ConfiurationContentIsGroovyConfigurationContent(){
        //given
        MockConfiguration mockConfiguration = new MockConfiguration();
        mockConfiguration.setConfigurationContent(groovyConfigurationContent);
        mockConfiguration.setHttpMethod(httpMethod);
        mockConfiguration.setPath(path);

        //when
        StubMapping stubMapping = wireMockStubMappingConverter.convert(mockConfiguration);

        //then
        assertTrue(stubMapping.getResponse().getTransformers().contains(GroovyResponseTransformer.GROOVY_TRANSFORMER));
    }

    @Test
    public void groovyTransformer_Shouldnt_Be_Added_When_ConfiurationContentIsStringConfigurationContent(){
        //given
        MockConfiguration mockConfiguration = new MockConfiguration();
        mockConfiguration.setConfigurationContent(stringConfigurationContent);
        mockConfiguration.setHttpMethod(httpMethod);
        mockConfiguration.setPath(path);

        //when
        StubMapping stubMapping = wireMockStubMappingConverter.convert(mockConfiguration);

        //then
        assertNull(stubMapping.getResponse().getTransformers());
    }

    @Test
    public void groovyTransformer_Shouldnt_Be_Added_When_ConfiurationContentIsXMLConfigurationContent(){
        //given
        MockConfiguration mockConfiguration = new MockConfiguration();
        mockConfiguration.setConfigurationContent(xmlConfigurationContent);
        mockConfiguration.setHttpMethod(httpMethod);
        mockConfiguration.setPath(path);

        //when
        StubMapping stubMapping = wireMockStubMappingConverter.convert(mockConfiguration);

        //then
        assertNull(stubMapping.getResponse().getTransformers());
    }

}
