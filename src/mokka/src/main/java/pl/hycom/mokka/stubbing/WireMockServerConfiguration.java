package pl.hycom.mokka.stubbing;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.hycom.mokka.stubbing.responsetemplating.GroovyResponseTransformer;

/**
 * Configuration bean related to {@link com.github.tomakehurst.wiremock.WireMockServer}.
 *
 * @author Piotr Kulasek-Szwed <piotr.kulasek-szwed@hycom.pl>
 */
@Configuration
public class WireMockServerConfiguration {

    /**
     * WireMock HTTP port to be exposed.
     */
    @Value("${wiremock.httpPort}")
    private int wiremockHttpPort;

    /**
     * Provides configured {@link com.github.tomakehurst.wiremock.WireMockServer}.
     *
     * @return
     */
    @Bean
    public WireMockServer wireMockServer() {
        WireMockConfiguration o = new WireMockConfiguration();
        o.port(wiremockHttpPort);
        o.extensions(GroovyResponseTransformer.class);

        return new WireMockServer(o);
    }
}
