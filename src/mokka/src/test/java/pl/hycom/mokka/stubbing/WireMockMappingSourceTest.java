package pl.hycom.mokka.stubbing;

import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.github.tomakehurst.wiremock.stubbing.StubMappings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import pl.hycom.mokka.emulator.mock.MockConfigurationRepository;
import pl.hycom.mokka.emulator.mock.model.ConfigurationContent;
import pl.hycom.mokka.emulator.mock.model.MockConfiguration;
import pl.hycom.mokka.emulator.mock.model.StringConfigurationContent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author adam.misterski@hycom.pl
 */
@ExtendWith(MockitoExtension.class)
public class WireMockMappingSourceTest {

    private static final Long id = 1L;
    private static final String name = "test";
    private static final int order = 0;
    private static final String httpMethod = "GET";
    private static final int timeout = 1;
    private static final int status = 200;
    private static final String path = "path";
    private static final ConfigurationContent stringConfigurationContent = new StringConfigurationContent();

    @InjectMocks
    private WireMockMappingSource wireMockMappingSource;

    @Mock
    private StubMappings stubMapping;

    @Mock
    private ConversionService conversionService;

    @Mock
    private MockConfigurationRepository mockConfigurationRepository;

    @Test
    public void whenMockConfigurationListIsEmpty(){
        wireMockMappingSource.loadMappingsInto(stubMapping);
        Mockito.verify(stubMapping, Mockito.times(0)).addMapping(Mockito.any());
    }

    @Test
    public void whenMockConfigurationListWithMalformedUntilConvertion(){
        List<MockConfiguration> mockConfigurationList = new ArrayList<>();
        MockConfiguration mockConfiguration = new MockConfiguration();
        mockConfiguration.setId(id);
        mockConfiguration.setName(name);
        mockConfiguration.setOrder(order);
        mockConfiguration.setTimeout(timeout);
        mockConfiguration.setStatus(status);
        mockConfiguration.setConfigurationContent(stringConfigurationContent);

        MockConfiguration mockConfiguration2 = new MockConfiguration();
        mockConfiguration2.setId(id);
        mockConfiguration2.setName(name);
        mockConfiguration2.setOrder(order);
        mockConfiguration2.setHttpMethod(httpMethod);
        mockConfiguration2.setTimeout(timeout);
        mockConfiguration2.setStatus(status);
        mockConfiguration2.setPath(path);
        mockConfiguration2.setConfigurationContent(stringConfigurationContent);

        mockConfigurationList.add(mockConfiguration);
        mockConfigurationList.add(mockConfiguration2);

        Mockito.when(mockConfigurationRepository.findAll()).thenReturn(mockConfigurationList);
        Mockito.doThrow(ConversionFailedException.class).when(conversionService).convert(mockConfiguration, StubMapping.class);

        wireMockMappingSource.loadMappingsInto(stubMapping);

        Mockito.verify(stubMapping, Mockito.times(1)).addMapping(Mockito.any());
    }

    @Test
    public void whenMockConfigurationListIsFull(){
        List<MockConfiguration> mockConfigurationList = new ArrayList<>();
        MockConfiguration mockConfiguration = new MockConfiguration();
        mockConfiguration.setId(id);
        mockConfiguration.setName(name);
        mockConfiguration.setOrder(order);
        mockConfiguration.setHttpMethod(httpMethod);
        mockConfiguration.setTimeout(timeout);
        mockConfiguration.setStatus(status);
        mockConfiguration.setPath(path);
        mockConfiguration.setConfigurationContent(stringConfigurationContent);

        MockConfiguration mockConfiguration2 = new MockConfiguration();
        mockConfiguration2.setId(id);
        mockConfiguration2.setName(name);
        mockConfiguration2.setOrder(order);
        mockConfiguration2.setHttpMethod(httpMethod);
        mockConfiguration2.setTimeout(timeout);
        mockConfiguration2.setStatus(status);
        mockConfiguration2.setPath(path);
        mockConfiguration2.setConfigurationContent(stringConfigurationContent);

        mockConfigurationList.add(mockConfiguration);
        mockConfigurationList.add(mockConfiguration2);

        Mockito.when(mockConfigurationRepository.findAll()).thenReturn(mockConfigurationList);

        wireMockMappingSource.loadMappingsInto(stubMapping);

        Mockito.verify(stubMapping, Mockito.times(2)).addMapping(Mockito.any());
    }


}
