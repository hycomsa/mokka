package pl.hycom.mokka.event;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import pl.hycom.mokka.emulator.mock.MockConfigurationRepository;
import pl.hycom.mokka.emulator.mock.model.MockConfiguration;

import java.util.Optional;

/**
 * {@link StubMappingEvent}'s listener.
 *
 * @author adam.misterski@hycom.pl
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StubMappingListener {

    private final WireMockServer wireMockServer;

    private final ConversionService conversionService;

    private final MockConfigurationRepository mockConfigurationRepository;

    @EventListener({StubMappingRemovedEvent.class, StubMappingDisabledEvent.class})
    public void removeStubMapping(StubMappingEvent event){
        StubMapping stubMapping = new StubMapping();
        stubMapping.setId(event.getUuid());
        wireMockServer.removeStubMapping(stubMapping);
        log.info("StubMapping (id: {}) deleted", event.getUuid());
    }

    @EventListener({StubMappingCreatedEvent.class, StubMappingEnabledEvent.class})
    public void addStubMapping(StubMappingEvent event){
        Optional<MockConfiguration> mockConfiguration = mockConfigurationRepository.findById(event.getId());
        if(mockConfiguration.isPresent()){
            wireMockServer.addStubMapping(conversionService.convert(mockConfiguration.get(), StubMapping.class));
            log.info("StubMapping (id: {}) added", event.getUuid());
        } else{
            log.warn("MockConfiguration (id: {}) not found in repository. StubMapping has not been added", event.getId());
        }
    }

    @EventListener(StubMappingUpdatedEvent.class)
    public void updateStubMapping(StubMappingEvent event){
        Optional<MockConfiguration> mockConfiguration = mockConfigurationRepository.findById(event.getId());
        if(mockConfiguration.isPresent()){
            StubMapping stubMapping = new StubMapping();
            stubMapping.setId(event.getUuid());
            wireMockServer.removeStubMapping(stubMapping);

            if(mockConfiguration.get().isEnabled()){
                wireMockServer.addStubMapping(conversionService.convert(mockConfiguration.get(), StubMapping.class));
                log.info("StubMapping (id: {}) updated", event.getUuid());
            } else {
                log.info("MockConfiguration (id: {}) is disabled, StubMapping has not been added", event.getId());
            }

        } else{
            log.warn("MockConfiguration (id: {}) not found in repository. StubMapping has not been added", event.getId());
        }
    }

}
