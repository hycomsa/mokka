package pl.hycom.mokka.service.openapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.hycom.mokka.service.openapi.pojo.StubMappingListWrapper;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Kamil Adamiec (kamil.adamiec@hycom.pl)
 */
public class OpenAPIToStubMappingListWrapperConverterTest {

    private static final String TEST_PATH = "/path";

    private static final String HTTP_STATUS_OK_CODE = "200";

    private static final String HTTP_STATUS_NOT_FOUND_CODE = "404";

    private static final String CONTENT_TYPE_HEADER_KEY = "Content-Type";

    private static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";

    private static final String APPLICATION_XML_CONTENT_TYPE = "application/xml";

    private static final String RESPONSE_AS_XML = "<version>3.0.0</version>";

    private static final String RESPONSE_AS_JSON = "\"{\\\"version\\\":\\\"3.0.0\\\"}\"";

    @Mock
    private OpenApiSchemaParsingStrategy openApiSchemaParsingStrategy;

    @InjectMocks
    private OpenAPIToStubMappingListWrapperConverter testedConverter;

    @BeforeEach
    public void setUp() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testConvertOpenAPIWithExampleJSONResponse() {

        MediaType mediaType = new MediaType();
        JsonNode responseNode = createExampleResponse();
        mediaType.setExample(responseNode);
        Content content = new Content();
        content.put(APPLICATION_JSON_CONTENT_TYPE, mediaType);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setContent(content);
        ApiResponses apiResponses = new ApiResponses();
        apiResponses.put(HTTP_STATUS_OK_CODE, apiResponse);
        Operation operation = new Operation();
        operation.setResponses(apiResponses);
        PathItem pathItem = new PathItem();
        pathItem.setGet(operation);
        Paths paths = new Paths();
        paths.put(TEST_PATH, pathItem);
        OpenAPI openAPI = new OpenAPI();
        openAPI.setPaths(paths);

        StubMappingListWrapper stubMappingsWrapper = testedConverter.convert(openAPI);
        assertNotNull(stubMappingsWrapper);
        List<StubMapping> stubMappings = stubMappingsWrapper.getStubMappings();
        assertNotNull(stubMappings);
        assertEquals(1, stubMappings.size());

        StubMapping stubMapping = stubMappings.get(0);
        assertNotNull(stubMapping.getRequest());
        assertEquals(RequestMethod.GET, stubMapping.getRequest().getMethod());
        assertEquals(TEST_PATH, stubMapping.getRequest().getUrl());
        assertNotNull(stubMapping.getResponse());
        assertEquals(200, stubMapping.getResponse().getStatus());
        assertEquals(RESPONSE_AS_JSON, stubMapping.getResponse().getBody());
        assertEquals(1, stubMapping.getResponse().getHeaders().size());
        assertEquals(APPLICATION_JSON_CONTENT_TYPE, stubMapping.getResponse().getHeaders().getHeader(CONTENT_TYPE_HEADER_KEY).firstValue());
    }

    @Test
    public void testConvertOpenAPIWithExampleXMLResponse() {

        MediaType mediaType = new MediaType();
        JsonNode responseNode = createExampleResponse();
        mediaType.setExample(responseNode);
        Content content = new Content();
        content.put(APPLICATION_XML_CONTENT_TYPE, mediaType);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setContent(content);
        ApiResponses apiResponses = new ApiResponses();
        apiResponses.put(HTTP_STATUS_OK_CODE, apiResponse);
        Operation operation = new Operation();
        operation.setResponses(apiResponses);
        PathItem pathItem = new PathItem();
        pathItem.setGet(operation);
        Paths paths = new Paths();
        paths.put(TEST_PATH, pathItem);
        OpenAPI openAPI = new OpenAPI();
        openAPI.setPaths(paths);

        StubMappingListWrapper stubMappingsWrapper = testedConverter.convert(openAPI);
        assertNotNull(stubMappingsWrapper);
        List<StubMapping> stubMappings = stubMappingsWrapper.getStubMappings();
        assertNotNull(stubMappings);
        assertEquals(1, stubMappings.size());

        StubMapping stubMapping = stubMappings.get(0);
        assertNotNull(stubMapping.getRequest());
        assertEquals(RequestMethod.GET, stubMapping.getRequest().getMethod());
        assertEquals(TEST_PATH, stubMapping.getRequest().getUrl());
        assertNotNull(stubMapping.getResponse());
        assertEquals(200, stubMapping.getResponse().getStatus());
        assertEquals(RESPONSE_AS_XML, stubMapping.getResponse().getBody());
        assertEquals(1, stubMapping.getResponse().getHeaders().size());
        assertEquals(APPLICATION_XML_CONTENT_TYPE, stubMapping.getResponse().getHeaders().getHeader(CONTENT_TYPE_HEADER_KEY).firstValue());
    }

    @Test
    public void testConsiderHttpStatusOKAsDefault() {

        MediaType mediaType = new MediaType();
        JsonNode responseNode = createExampleResponse();
        mediaType.setExample(responseNode);
        Content content = new Content();
        content.put(APPLICATION_JSON_CONTENT_TYPE, mediaType);
        ApiResponse apiResponseOk = new ApiResponse();
        apiResponseOk.setContent(content);

        MediaType mediaType404 = new MediaType();
        mediaType404.setExample("Not found!");
        Content content404 = new Content();
        content404.put(APPLICATION_JSON_CONTENT_TYPE, mediaType404);
        ApiResponse apiResponseNotFound = new ApiResponse();
        apiResponseNotFound.setContent(content404);

        ApiResponses apiResponses = new ApiResponses();
        apiResponses.put(HTTP_STATUS_OK_CODE, apiResponseOk);
        apiResponses.put(HTTP_STATUS_NOT_FOUND_CODE, apiResponseNotFound);

        Operation operation = new Operation();
        operation.setResponses(apiResponses);
        PathItem pathItem = new PathItem();
        pathItem.setGet(operation);
        Paths paths = new Paths();
        paths.put(TEST_PATH, pathItem);
        OpenAPI openAPI = new OpenAPI();
        openAPI.setPaths(paths);

        StubMappingListWrapper stubMappingsWrapper = testedConverter.convert(openAPI);
        assertNotNull(stubMappingsWrapper);
        List<StubMapping> stubMappings = stubMappingsWrapper.getStubMappings();
        assertNotNull(stubMappings);
        assertEquals(1, stubMappings.size());

        StubMapping stubMapping = stubMappings.get(0);
        assertNotNull(stubMapping.getRequest());
        assertEquals(RequestMethod.GET, stubMapping.getRequest().getMethod());
        assertEquals(TEST_PATH, stubMapping.getRequest().getUrl());
        assertNotNull(stubMapping.getResponse());
        assertEquals(200, stubMapping.getResponse().getStatus());
        assertEquals(RESPONSE_AS_JSON, stubMapping.getResponse().getBody());
        assertEquals(1, stubMapping.getResponse().getHeaders().size());
        assertEquals(APPLICATION_JSON_CONTENT_TYPE, stubMapping.getResponse().getHeaders().getHeader(CONTENT_TYPE_HEADER_KEY).firstValue());
    }

    @Test
    public void testResponseDefinedBySchema() {

        Schema schema = new Schema();
        schema.set$ref("/components/schema/Version");
        MediaType mediaType = new MediaType();
        mediaType.setSchema(schema);
        Content content = new Content();
        content.put(APPLICATION_JSON_CONTENT_TYPE, mediaType);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setContent(content);
        ApiResponses apiResponses = new ApiResponses();
        apiResponses.put(HTTP_STATUS_OK_CODE, apiResponse);
        Operation operation = new Operation();
        operation.setResponses(apiResponses);
        PathItem pathItem = new PathItem();
        pathItem.setGet(operation);
        Paths paths = new Paths();
        paths.put(TEST_PATH, pathItem);

        Components components = new Components();

        OpenAPI openAPI = new OpenAPI();
        openAPI.setPaths(paths);
        openAPI.setComponents(components);

        when(openApiSchemaParsingStrategy.createExampleFromSchema(any(), any())).thenReturn(createExampleResponse());

        StubMappingListWrapper stubMappingsWrapper = testedConverter.convert(openAPI);
        assertNotNull(stubMappingsWrapper);
        List<StubMapping> stubMappings = stubMappingsWrapper.getStubMappings();
        assertNotNull(stubMappings);
        assertEquals(1, stubMappings.size());

        StubMapping stubMapping = stubMappings.get(0);
        assertNotNull(stubMapping.getRequest());
        assertEquals(RequestMethod.GET, stubMapping.getRequest().getMethod());
        assertEquals(TEST_PATH, stubMapping.getRequest().getUrl());
        assertNotNull(stubMapping.getResponse());
        assertEquals(200, stubMapping.getResponse().getStatus());
        assertEquals(RESPONSE_AS_JSON, stubMapping.getResponse().getBody());
        assertEquals(1, stubMapping.getResponse().getHeaders().size());
        assertEquals(APPLICATION_JSON_CONTENT_TYPE, stubMapping.getResponse().getHeaders().getHeader(CONTENT_TYPE_HEADER_KEY).firstValue());
    }

    private ObjectNode createExampleResponse() {
        return new ObjectNode(new JsonNodeFactory(false),
            Collections.singletonMap("version", new TextNode("3.0.0")));
    }
}
