package pl.hycom.mokka.emulator.logs;

import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.hycom.mokka.web.json.View;
import wiremock.com.fasterxml.jackson.annotation.JsonView;
import wiremock.com.fasterxml.jackson.core.JsonProcessingException;
import wiremock.com.fasterxml.jackson.databind.ObjectMapper;


/**
 * @author Dominik Gorkiewicz (dominik.gorkiewicz@hycom.pl)
 */
@RequiredArgsConstructor
@RestController
@Slf4j
public class JournalController {

    private final WireMockServer wireMockServer;

    @JsonView(View.General.class)
    @GetMapping(value = "/journal")
    public String getJournal() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(wireMockServer.getAllServeEvents());
    }
}
