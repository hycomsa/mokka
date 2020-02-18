package pl.hycom.mokka.stubbing;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Piotr Kulasek-Szwed <piotr.kulasek-szwed@hycom.pl>
 */
public class WireMockServerRunnerTest {

    @Test
    public void shouldNotStartWireMock() {
        // given
        WireMockServer server = new WireMockServer();
        WireMockServerRunner runner = new WireMockServerRunner(server);
        runner.setWiremockEnabled(false);

        // when
        try {
            runner.run("");
        } catch (Exception e) {
            fail();
        }

        // then
        assertFalse(server.isRunning());
    }

    @Test
    public void shouldStartWireMock() {
        // given
        WireMockServer server = new WireMockServer();
        WireMockServerRunner runner = new WireMockServerRunner(server);
        runner.setWiremockEnabled(true);

        // when
        try {
            runner.run("");
        } catch (Exception e) {
            fail();
        }

        // then
        assertTrue(server.isRunning());
    }

}
