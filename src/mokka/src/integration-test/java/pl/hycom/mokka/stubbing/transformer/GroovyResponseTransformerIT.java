package pl.hycom.mokka.stubbing.transformer;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.expect;

/**
 * @author Bartosz Kuron (bartosz.kuron@hycom.pl)
 */
@DirtiesContext
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GroovyResponseTransformerIT {


    @Value("${wiremock.test.httpPort}")
    private int wiremockHttpPort = 8083;

    WireMockServer wm;

    @BeforeEach
    public void setUp() {
        RestAssured.port = wiremockHttpPort;
    }

    @Test
    public void transform_test() {
        startWithExtensions("pl.hycom.mokka.stubbing.responsetemplating.GroovyResponseTransformer");
        createStub("/test-body");

        expect().statusCode(404).when().get("/test-body");
    }


    private void createStub(String url) {
        wm.stubFor(get(urlEqualTo(url)).willReturn(aResponse()
            .withHeader("Content-Type", "text/plain")
            .withStatus(200)
            .withBody("result='''<responseBody>'''\\r\\nresult+=ctx.uri\\r\\nresult+='''<\\/responseBody>'''\\r\\n\\r\\nreturn result")
            .withTransformers("groovy-transformer")));
    }


    private void startWithExtensions(String... extensions) {
        wm = new WireMockServer(wireMockConfig()
            .port(wiremockHttpPort)
            .extensions(extensions));
        wm.start();
    }

    @AfterEach
    public void cleanup() {
        if (wm != null) {
            wm.stop();
        }
    }

}
