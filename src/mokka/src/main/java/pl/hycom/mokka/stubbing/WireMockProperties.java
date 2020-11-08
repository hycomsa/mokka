package pl.hycom.mokka.stubbing;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for WireMock engine.
 *
 * @author Piotr Kulasek-Szwed <piotr.kulasek-szwed@hycom.pl>
 */
@ConfigurationProperties(prefix = "wiremock")
@Data
public class WireMockProperties {

    private boolean enabled = false;

    private int httpPort = 8082;

    private boolean verbose = false;

    private boolean browserProxying = false;

    private boolean gzip = true;
}
