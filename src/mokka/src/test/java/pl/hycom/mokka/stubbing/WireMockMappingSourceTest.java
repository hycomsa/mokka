package pl.hycom.mokka.stubbing;

import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.github.tomakehurst.wiremock.stubbing.StubMappings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import pl.hycom.mokka.emulator.mock.MockConfigurationRepository;
import pl.hycom.mokka.emulator.mock.model.MockConfiguration;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author adam.misterski@hycom.pl
 */
@ExtendWith(MockitoExtension.class)
public class WireMockMappingSourceTest {

    @InjectMocks
    private WireMockMappingSource wireMockMappingSource;

    @Mock
    private StubMappings stubMapping;

    @Mock
    private ConversionService conversionService;

    @Mock
    private MockConfigurationRepository mockConfigurationRepository;

    @Test
    public void whenMockConfigurationListIsEmpty() {
        //when
        wireMockMappingSource.loadMappingsInto(stubMapping);

        //then
        verify(stubMapping, never()).addMapping(any());
    }

    @Test
    public void whenMockConfigurationListWithMalformedUntilConvertion() {
        //given
        MockConfiguration malformedMock = new MockConfiguration();
        List<MockConfiguration> mockConfigurationList = Collections.singletonList(malformedMock);
        when(mockConfigurationRepository.findAll()).thenReturn(mockConfigurationList);
        doThrow(ConversionFailedException.class).when(conversionService).convert(malformedMock, StubMapping.class);

        //when
        wireMockMappingSource.loadMappingsInto(stubMapping);

        //then
        verify(stubMapping, never()).addMapping(any());
    }

    @Test
    public void whenMockConfigurationListIsFull() {
        //given
        List<MockConfiguration> mockConfigurationList = Collections.singletonList(new MockConfiguration());
        when(mockConfigurationRepository.findAll()).thenReturn(mockConfigurationList);

        //when
        wireMockMappingSource.loadMappingsInto(stubMapping);

        //then
        verify(stubMapping, times(1)).addMapping(any());
    }

}
