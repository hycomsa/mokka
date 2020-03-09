package pl.hycom.mokka.service.openapi;

import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;
import pl.hycom.mokka.service.openapi.pojo.StubMappingListWrapper;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class OpenApiParsingServiceTest {

    private static final String EXAMPLE_JSON_SCHEMA = "{\n" +
        "  \"openapi\": \"3.0.2\",\n" +
        "  \"paths\": {\n" +
        "    \"/pet\": {\n" +
        "      \"put\": {\n" +
        "        \"tags\": [\"pet\"],\n" +
        "        \"summary\": \"Update an existing pet\",\n" +
        "        \"description\": \"Update an existing pet by Id\",\n" +
        "        \"operationId\": \"updatePet\",\n" +
        "        \"responses\": {\n" +
        "          \"200\": {\n" +
        "            \"description\": \"Successful operation\",\n" +
        "            \"content\": {\n" +
        "              \"application/xml\": {\n" +
        "                \"example\": {\n" +
        "                  \"versions\": [{\n" +
        "                      \"status\": \"CURRENT\",\n" +
        "                      \"updated\": \"2011-01-21T11:33:21Z\",\n" +
        "                      \"id\": \"v2.0\",\n" +
        "                      \"links\": [{\n" +
        "                        \"href\": \"http://127.0.0.1:8774/v2/\",\n" +
        "                        \"rel\": \"self\"\n" +
        "                      }]\n" +
        "                    },\n" +
        "                    {\n" +
        "                      \"status\": \"EXPERIMENTAL\",\n" +
        "                      \"updated\": \"2013-07-23T11:33:21Z\",\n" +
        "                      \"id\": \"v3.0\",\n" +
        "                      \"links\": [{\n" +
        "                        \"href\": \"http://127.0.0.1:8774/v3/\",\n" +
        "                        \"rel\": \"self\"\n" +
        "                      }]\n" +
        "                    }\n" +
        "                  ]\n" +
        "                }\n" +
        "              }\n" +
        "            }\n" +
        "          }\n" +
        "        }\n" +
        "      }\n" +
        "    }\n" +
        "  }\n" +
        "}";

    @Spy
    private ConversionService conversionService;

    @InjectMocks
    private OpenApiParsingService testedService;

    @Test
    public void testCreateStubMappingsNonValidSchema() {

        String exampleSchema = "not valid schema";
        List<StubMapping> result = testedService.createStubMappings(exampleSchema);

        Assertions.assertEquals(0, result.size());
        Mockito.verify(conversionService, Mockito.never()).convert(Mockito.any(), Mockito.eq(StubMappingListWrapper.class));
    }

    @Test
    public void testCreateStubMappingValidSchema() {

        StubMappingListWrapper wrapper = new StubMappingListWrapper();
        wrapper.setStubMappings(Collections.singletonList(new StubMapping()));
        Mockito.when(conversionService.convert(Mockito.any(), Mockito.eq(StubMappingListWrapper.class))).thenReturn(wrapper);
        Mockito.when(conversionService.canConvert(OpenAPI.class, StubMappingListWrapper.class)).thenReturn(true);
        List<StubMapping> result = testedService.createStubMappings(EXAMPLE_JSON_SCHEMA);
        Assertions.assertEquals(1, result.size());
        Mockito.verify(conversionService, Mockito.times(1))
            .convert(Mockito.any(), Mockito.eq(StubMappingListWrapper.class));
    }
}
