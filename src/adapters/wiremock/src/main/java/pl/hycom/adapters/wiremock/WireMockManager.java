package pl.hycom.adapters.wiremock;

import pl.hycom.api.MockManager;
import pl.hycom.api.model.Mapping;

import java.util.List;

/**
 * @author Jakub Muras <jakub.muras@hycom.pl>
 */
public class WireMockManager implements MockManager {

    private final String host;
    private final int port;
    private WireMockService wireMockService;

    public WireMockManager(String host, int port) {
        this.host = host;
        this.port = port;
        wireMockService = new WireMockService(getHost(), getPort());
    }

    @Override
    public void create(Mapping mapping) {
        wireMockService.addMapping(WiremockMappingConverter.convertToStubMapping(mapping));
    }

    @Override
    public void remove(Mapping mapping) {

    }

    @Override
    public void update(Mapping mapping) {

    }

    @Override
    public List<Mapping> getAllMappings() {
        return WiremockMappingConverter.convertToMapping(wireMockService.listAll());
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
