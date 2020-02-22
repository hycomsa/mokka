package pl.hycom.mokka.service.openapi;

import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.hycom.mokka.ConversionConfiguration;

import java.io.*;
import java.util.List;

@ActiveProfiles("test")
@SpringBootTest(classes = {ConversionConfiguration.class, OpenApiParserConfiguration.class})
public class OpenApiParsingServiceIT {

    private static final String JSON_SCHEMA_WITH_EXAMPLE = "exampleResponse.json";

    private static final String YAML_SCHEMA_WITH_EXAMPLE = "exampleResponse.yaml";

    @Autowired
    private OpenApiParsingService openApiParsingService;

    @Test
    public void testCreateStubMappingsNonValidSchema() {

        String nonValidSchema = "non valid schema";
        List<StubMapping> result = openApiParsingService.createStubMappings(nonValidSchema);

        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void testCreateStubMappingsValidJsonSchema() throws IOException {

        List<StubMapping> result = openApiParsingService.createStubMappings(readTestDataContent(JSON_SCHEMA_WITH_EXAMPLE));

        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void testCreateStubMappingsValidYamlSchema() throws IOException {

        List<StubMapping> result = openApiParsingService.createStubMappings(readTestDataContent(YAML_SCHEMA_WITH_EXAMPLE));

        Assertions.assertEquals(1, result.size());
    }

    private String readTestDataContent(String filename) throws IOException {

        InputStream fileStream = this.getClass().getResourceAsStream("/files/openapi/" + filename);
        StringBuilder stringBuilder = new StringBuilder();
        try(BufferedReader br = new BufferedReader(new InputStreamReader(fileStream))) {
            for(String line = br.readLine(); line != null; line = br.readLine()) {
                stringBuilder.append(line).append('\n');
            }
        }
        return stringBuilder.toString();
    }
}
