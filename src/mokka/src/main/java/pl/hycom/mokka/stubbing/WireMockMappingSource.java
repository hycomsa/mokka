package pl.hycom.mokka.stubbing;

import com.github.tomakehurst.wiremock.standalone.MappingsSource;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.github.tomakehurst.wiremock.stubbing.StubMappings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import pl.hycom.mokka.emulator.mock.MockConfigurationRepository;
import pl.hycom.mokka.emulator.mock.model.MockConfiguration;

import java.util.List;

/**
 * Implementements WireMock's {@link MappingsSource}
 *
 * @author adam.misterski@hycom.pl
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WireMockMappingSource implements MappingsSource {

    private final ConversionService conversionService;

    private final MockConfigurationRepository mockConfigurationRepository;

    @Override
    public void save(List<StubMapping> list) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void save(StubMapping stubMapping) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void remove(StubMapping stubMapping) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void removeAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void loadMappingsInto(StubMappings stubMappings) {
        Iterable<MockConfiguration> mockConfigurations = mockConfigurationRepository.findAll();

        for(MockConfiguration mockConfiguration: mockConfigurations){
            try{
                stubMappings.addMapping(conversionService.convert(mockConfiguration, StubMapping.class));
            }
            catch (ConversionException ex){
                log.error("MockConfiguration (id: {}) could not be converted", mockConfiguration.getId(), ex);
            }

        }
    }
}
