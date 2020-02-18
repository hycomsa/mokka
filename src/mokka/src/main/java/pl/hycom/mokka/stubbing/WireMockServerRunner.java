package pl.hycom.mokka.stubbing;

import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

/**
 * Starts embedded WireMock server provided by {@link WireMockServerConfiguration} configuration bean.
 * <p>
 * May be disabled with `wiremock.enabled` property.
 *
 * @author Piotr Kulasek-Szwed <piotr.kulasek-szwed@hycom.pl>
 * @see: ADR-0004 Should Wiremock be used as stubs engine
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WireMockServerRunner implements CommandLineRunner {

    private final WireMockServer server;

    @Setter
    @Value("${wiremock.enabled}")
    private boolean wiremockEnabled;

    @Override
    public void run(String... args) throws Exception {
        if (!wiremockEnabled) {
            log.debug("Embedded Wiremock disabled by configuration.");
            return;
        }

        log.info("Starting embedded Wiremock server.");

        server.start();

        log.info("Embedded Wiremock is running [{}] on port {}.", server.isRunning(), server.port());
    }
}
