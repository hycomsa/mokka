package pl.hycom.mokka.service.openapi;

import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import pl.hycom.mokka.service.openapi.pojo.StubMappingListWrapper;

import java.util.Collections;
import java.util.List;

/**
 * @author Kamil Adamiec (kamil.adamiec@hycom.pl)
 */
@Slf4j
@RequiredArgsConstructor
public class OpenApiParsingService {

    private final ConversionService conversionService;

    @SuppressWarnings("unchecked")
    public List<StubMapping> createStubMappings(String schema) {

        SwaggerParseResult parseResult = new OpenAPIV3Parser().readContents(schema);
        if(parseResult.getOpenAPI() == null) {

            parseResult.getMessages().forEach(log::error);
            return Collections.emptyList();
        }
        OpenAPI openAPI = parseResult.getOpenAPI();

        if(conversionService.canConvert(OpenAPI.class, StubMappingListWrapper.class)) {
            StubMappingListWrapper stubMappingListWrapper = conversionService.convert(openAPI, StubMappingListWrapper.class);
            return stubMappingListWrapper != null && stubMappingListWrapper.getStubMappings() != null ?
                stubMappingListWrapper.getStubMappings() : Collections.emptyList();
        }
        return Collections.emptyList();
    }
}
