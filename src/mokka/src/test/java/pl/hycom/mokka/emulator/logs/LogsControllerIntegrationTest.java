package pl.hycom.mokka.emulator.logs;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.hycom.mokka.AbstractTest;
import pl.hycom.mokka.emulator.logs.model.Log;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Kamil Adamiec (kamil.adamiec@hycom.pl)
 */
@DirtiesContext
@AutoConfigureMockMvc
public class LogsControllerIntegrationTest extends AbstractTest {

    private static final String GET_LOGS_URI = "/logs";

    private static final String GET_URIS_URI = "/logs/uris";

    private static final String GET_LOG_URI = "/logs/{id}";

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    public void setUp() {

        Log logModel = new Log();
        logModel.setUri("testService");
        logModel.setHttpMethod("GET");
        logModel.setRequest("");
        logModel.setResponse("");
        logModel.setDate(Timestamp.valueOf(LocalDateTime.now()));
        logModel.setStatus(LogStatus.OK);
        logModel.setFrom("127.0.0.1");
        logModel.setConfigurationId(1L);
        logRepository.save(logModel);
    }

    @AfterEach
    public void tearDown() {
        logRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles="USER")
    public void testGetLogs() throws Exception {

        mvc.perform(asyncRequest(MockMvcRequestBuilders.get(GET_LOGS_URI)))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$", Matchers.hasSize(1)));

    }

    @Test
    @WithMockUser(roles="USER")
    public void testGetSetOfUris() throws Exception {

        mvc.perform(asyncRequest(MockMvcRequestBuilders.get(GET_URIS_URI)))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$", Matchers.contains("testService")));
    }

    @Test
    @WithMockUser(roles="USER")
    public void testGetExchanges() throws Exception {

        mvc.perform(asyncRequest(MockMvcRequestBuilders.get(GET_LOG_URI, "1")))
            .andExpect(status().is(200));
    }

    private MockHttpServletRequestBuilder asyncRequest(MockHttpServletRequestBuilder builder) {
        return builder.header("x-requested-with", "XMLHttpRequest");
    }
}
