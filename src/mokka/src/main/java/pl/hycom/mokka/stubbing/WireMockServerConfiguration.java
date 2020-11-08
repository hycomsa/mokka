package pl.hycom.mokka.stubbing;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.standalone.MappingsSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.hycom.mokka.stubbing.responsetemplating.GroovyResponseTransformer;

/**
 * Configuration bean related to {@link com.github.tomakehurst.wiremock.WireMockServer}.
 *
 * @author Piotr Kulasek-Szwed <piotr.kulasek-szwed@hycom.pl>
 */
@Configuration
@EnableConfigurationProperties(WireMockProperties.class)
public class WireMockServerConfiguration {

    /**
     * Provides configured {@link com.github.tomakehurst.wiremock.WireMockServer}.
     *
     * @return
     */
    @Bean
    public WireMockServer wireMockServer(WireMockProperties wireMockProperties, MappingsSource wireMockMappingSource) {
        WireMockConfiguration o = new WireMockConfiguration();
        o.port(wireMockProperties.getHttpPort());
        o.mappingSource(wireMockMappingSource);
        o.extensions(GroovyResponseTransformer.class);
        o.enableBrowserProxying(wireMockProperties.isBrowserProxying());
        o.notifier(new Slf4jNotifier(wireMockProperties.isVerbose()));
        o.gzipDisabled(wireMockProperties.isGzip());
        return new WireMockServer(o);
    }
}
