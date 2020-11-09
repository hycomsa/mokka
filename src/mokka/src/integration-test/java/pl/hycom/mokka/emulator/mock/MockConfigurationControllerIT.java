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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import pl.hycom.mokka.Application;
import wiremock.com.jayway.jsonpath.JsonPath;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * @author adam.misterski@hycom.pl
 */
@DirtiesContext
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@SpringBootTest(classes = Application.class, properties = {"wiremock.enabled = true"})
public class MockConfigurationControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WireMockServer wm;

    private static final String name = "test";
    private static final String httpMethod = "GET";
    private static final int status = 303;
    private static final String path = "path";
    private static final String slash = "/";

    @BeforeEach
    public void setUp() {
        RestAssured.port = wm.port();
    }

    @Test
    @WithMockUser(roles="ADMIN")
    public void create_mock() throws Exception {
        String id = createMock(true);

        Response response = RestAssured.get("/__admin/mappings/" + convertIDtoUUID(id));
        assertEquals(name, response.jsonPath().get("name"));
        assertEquals(slash + path, response.jsonPath().get("request.urlPathPattern"));
        assertEquals(httpMethod, response.jsonPath().get("request.method"));
    }

    @Test
    @WithMockUser(roles="ADMIN")
    public void update_mock() throws Exception {
        String id = createMock(true);
        String testChange = "test_change";

        String requestBody = "{\"status\":" + status + ",\"httpMethod\":\"" + httpMethod + "\",\"path\":\""+path+"\",\"id\":"+id+",\"description\":\"test\",\"pattern\":null,\"name\":\"" + testChange + "\",\"enabled\":true,\"proxyBaseUrl\":null,\"timeout\":0,\"order\":0,\"configurationContent\":{\"string\":{\"value\":\"d\"}},\"updated\":\"2020-03-18T01:44:39.089+0000\",\"errors\":null,\"changes\":null,\"showChanges\":false,\"showConfiguration\":true,\"type\":\"string\"}";

        mvc.perform(asyncRequest(put("/configuration")).contentType(MediaType.APPLICATION_JSON).with(csrf())
            .content(requestBody))
            .andExpect(status().isOk());

        Response response = RestAssured.get("/__admin/mappings/" + convertIDtoUUID(id));
        assertEquals(testChange, response.jsonPath().get("name"));
        assertEquals(slash + path, response.jsonPath().get("request.urlPathPattern"));
        assertEquals(httpMethod, response.jsonPath().get("request.method"));
    }

    @Test
    @WithMockUser(roles="ADMIN")
    public void disable_mock() throws Exception {
        String id = createMock(true);

        mvc.perform(asyncRequest(post("/configuration/{id}/disable", id)).contentType(MediaType.APPLICATION_JSON).with(csrf()))
            .andExpect(status().isOk());

        Response response = RestAssured.get("/__admin/mappings/" + convertIDtoUUID(id));
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    @WithMockUser(roles="ADMIN")
    public void enable_mock() throws Exception {
        String id = createMock(false);

        mvc.perform(asyncRequest(post("/configuration/{id}/enable", id)).contentType(MediaType.APPLICATION_JSON).with(csrf()))
            .andExpect(status().isOk());

        Response response = RestAssured.get("/__admin/mappings/" + convertIDtoUUID(id));
        assertEquals(name, response.jsonPath().get("name"));
        assertEquals(slash + path, response.jsonPath().get("request.urlPathPattern"));
        assertEquals(httpMethod, response.jsonPath().get("request.method"));
    }

    @Test
    public void remove_mock() throws Exception {
        String id = createMock(true);

        mvc.perform(asyncRequest(delete("/configuration/"+ id)).contentType(MediaType.APPLICATION_JSON).with(csrf()))
            .andExpect(status().isOk());

        Response response = RestAssured.get("/__admin/mappings" + convertIDtoUUID(id));
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND.value());
    }

    private MockHttpServletRequestBuilder asyncRequest(MockHttpServletRequestBuilder builder) {
        return builder.header("x-requested-with", "XMLHttpRequest");
    }

    private String createMock(boolean enable) throws Exception {
        String requestBody = "{\"status\":\"" + status + "\",\"httpMethod\":\"" + httpMethod + "\",\"path\":\"" + path + "\",\"name\":\"" + name + "\",\"enabled\":\"" + enable + "\",\"type\":\"string\",\"configurationContent\":{\"string\":{\"value\":\"test\"}}}\n";

        String createMock = mvc.perform(asyncRequest(put("/configuration")).contentType(MediaType.APPLICATION_JSON).with(csrf())
            .content(requestBody))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        return JsonPath.parse(createMock).read("$['id']").toString();
    }

    private String convertIDtoUUID(String id){
        return UUID.nameUUIDFromBytes(id.getBytes()).toString();
    }
}
