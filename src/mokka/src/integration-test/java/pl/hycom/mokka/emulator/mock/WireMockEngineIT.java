package pl.hycom.mokka.emulator.mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import pl.hycom.mokka.Application;
import wiremock.com.jayway.jsonpath.JsonPath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * @author Piotr Kulasek-Szwed <piotr.kulasek-szwed@hycom.pl>
 */
@DirtiesContext
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@SpringBootTest(classes = Application.class, properties = {"wiremock.enabled = true"})
public class WireMockEngineIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WireMockServer wm;

    private static final String name = "test";
    private static final String httpGetMethod = "GET";
    private static final String httpPostMethod = "POST";
    private static final int status = 200;
    private static final String path = "path";
    private static final String slash = "/";

    @BeforeEach

    public void setUp() {
        RestAssured.port = wm.port();
    }

    @Test
    @WithMockUser(roles="ADMIN")
    public void shouldMatchMock() throws Exception {
        createMock(true, path, "", httpGetMethod);
        Response response = RestAssured.get(slash + path);
        assertEquals("test", response.asString());
    }

    @Test
    @WithMockUser(roles="ADMIN")
    public void shouldMatchMockByPathPattern() throws Exception {
        createMock(true, "test/([a-z]*)", "", httpGetMethod);
        Response response = RestAssured.get("/test/a");
        assertEquals("test", response.asString());
    }

    @Test
    @WithMockUser(roles="ADMIN")
    public void shouldMatchMockByBodyPattern() throws Exception {
        createMock(true, path, "([a-z]*)", httpPostMethod);
        Response response = RestAssured.with().body("abcd").post(slash + path);
        assertEquals("test", response.asString());
    }

    private MockHttpServletRequestBuilder asyncRequest(MockHttpServletRequestBuilder builder) {
        return builder.header("x-requested-with", "XMLHttpRequest");
    }

    private String createMock(boolean enable, String path, String pattern, String httpMethod) throws Exception {
        String requestBody = "{\"status\":\"" + status + "\",\"httpMethod\":\"" + httpMethod + "\",\"path\":\"" + path + "\",\"name\":\"" + name + "\",\"pattern\":\"" + pattern + "\",\"enabled\":\"" + enable + "\",\"type\":\"string\",\"configurationContent\":{\"string\":{\"value\":\"test\"}}}\n";

        String createMock = mvc.perform(asyncRequest(put("/configuration")).contentType(MediaType.APPLICATION_JSON).with(csrf())
            .content(requestBody))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        return JsonPath.parse(createMock).read("$['id']").toString();
    }

}
