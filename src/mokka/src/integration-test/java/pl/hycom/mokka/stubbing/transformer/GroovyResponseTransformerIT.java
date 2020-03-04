package pl.hycom.mokka.stubbing.transformer;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.standalone.MappingsSource;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import pl.hycom.mokka.stubbing.WireMockServerConfiguration;
import pl.hycom.mokka.stubbing.responsetemplating.GroovyResponseTransformer;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.restassured.RestAssured.expect;

/**
 * @author Bartosz Kuron (bartosz.kuron@hycom.pl)
 */
@ActiveProfiles("test")
@SpringBootTest(classes = WireMockServerConfiguration.class)
public class GroovyResponseTransformerIT {

    private static final String API_URL = "test-body";
    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "text/plain";

    @Autowired
    private WireMockServer wm;

    @MockBean
    private MappingsSource wireMockMappingSource;

    @BeforeEach
    public void setUp() {
        wm.start();
        RestAssured.port = wm.port();
    }

    @Test
    public void transform_test() {
        createStub("/"+API_URL);

        Response response = RestAssured.get("/"+API_URL);
        expect().statusCode(200).header(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE).response();

        String expectedBody = "<response>" + API_URL + "|" + "GET</response>";
        Assertions.assertEquals(expectedBody, response.getBody().asString());
    }


    private void createStub(String url) {
        String body = "result='<response>'; result+=ctx.uri; result+='|'; result+=request.requestLine.method; result+='</response>'; return result;";

        wm.stubFor(get(urlEqualTo(url)).willReturn(aResponse()
            .withHeader(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE)
            .withStatus(200)
            .withBody(body)
            .withTransformers(GroovyResponseTransformer.GROOVY_TRANSFORMER)));
    }

    @AfterEach
    public void cleanup() {
        if (wm != null) {
            wm.stop();
        }
    }

}
