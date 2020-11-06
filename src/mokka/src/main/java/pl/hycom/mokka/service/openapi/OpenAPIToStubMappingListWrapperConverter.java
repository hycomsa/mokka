package pl.hycom.mokka.service.openapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.CollectionUtils;
import pl.hycom.mokka.service.openapi.pojo.StubMappingListWrapper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Kamil Adamiec (kamil.adamiec@hycom.pl)
 */
@RequiredArgsConstructor
public class OpenAPIToStubMappingListWrapperConverter implements Converter<OpenAPI, StubMappingListWrapper> {

    private static final String HTTP_STATUS_OK_CODE = "200";

    private static final String CONTENT_TYPE_KEY = "Content-Type";

    private static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";

    private static final String APPLICATION_XML_CONTENT_TYPE = "application/xml";

    private final OpenApiSchemaParsingStrategy openApiSchemaParsingStrategy;

    @Override
    public StubMappingListWrapper convert(OpenAPI openAPI) {

        List<StubMapping> stubMappingList = new LinkedList<>();
        processPaths(stubMappingList, openAPI.getPaths(), openAPI.getComponents());

        StubMappingListWrapper stubMappingListWrapper = new StubMappingListWrapper();
        stubMappingListWrapper.setStubMappings(stubMappingList);
        return stubMappingListWrapper;
    }

    private void processPaths(List<StubMapping> stubMappingList, Paths paths, Components components) {

        for (Map.Entry<String, PathItem> pathEntry : paths.entrySet()) {
            processPathItem(stubMappingList, pathEntry.getKey(), pathEntry.getValue(), components);
        }
    }

    private void processPathItem(List<StubMapping> stubMappingList, String path, PathItem pathItem, Components components) {

        if(haveResponsesForGivenMethod(pathItem.getGet())) {
            Operation op = pathItem.getGet();
            stubMappingList.addAll(processApiResponses(RequestMethod.GET, path, op.getOperationId(), op.getResponses(), components));
        }
        if(haveResponsesForGivenMethod(pathItem.getPost())) {
            Operation op = pathItem.getPost();
            stubMappingList.addAll(processApiResponses(RequestMethod.POST, path, op.getOperationId(), op.getResponses(), components));
        }
        if(haveResponsesForGivenMethod(pathItem.getPut())) {
            Operation op = pathItem.getPut();
            stubMappingList.addAll(processApiResponses(RequestMethod.PUT, path, op.getOperationId(), op.getResponses(), components));
        }
        if(haveResponsesForGivenMethod(pathItem.getDelete())) {
            Operation op = pathItem.getDelete();
            stubMappingList.addAll(processApiResponses(RequestMethod.DELETE, path, op.getOperationId(), op.getResponses(), components));
        }
        if(haveResponsesForGivenMethod(pathItem.getHead())) {
            Operation op = pathItem.getHead();
            stubMappingList.addAll(processApiResponses(RequestMethod.HEAD, path, op.getOperationId(), op.getResponses(), components));
        }
        if(haveResponsesForGivenMethod(pathItem.getOptions())) {
            Operation op = pathItem.getOptions();
            stubMappingList.addAll(processApiResponses(RequestMethod.OPTIONS, path, op.getOperationId(), op.getResponses(), components));
        }
        if(haveResponsesForGivenMethod(pathItem.getPatch())) {
            Operation op = pathItem.getPatch();
            stubMappingList.addAll(processApiResponses(RequestMethod.PATCH, path, op.getOperationId(), op.getResponses(), components));
        }
        if(haveResponsesForGivenMethod(pathItem.getTrace())) {
            Operation op = pathItem.getTrace();
            stubMappingList.addAll(processApiResponses(RequestMethod.TRACE, path, op.getOperationId(), op.getResponses(), components));
        }
    }

    private boolean haveResponsesForGivenMethod(Operation operation) {
        return operation != null && !CollectionUtils.isEmpty(operation.getResponses());
    }

    private List<StubMapping> processApiResponses(RequestMethod requestMethod, String path, String operationId,
                                                  ApiResponses apiResponses, Components components) {

        ApiResponse apiResponse = apiResponses.values().iterator().next();
        int status = Integer.parseInt(apiResponses.keySet().iterator().next());
        if(apiResponses.size() > 1) {
            ApiResponse responseWithStatusOK = apiResponses.entrySet().stream()
                .filter(entry -> HTTP_STATUS_OK_CODE.equals(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst().orElse(null);
            if(responseWithStatusOK != null) {
                apiResponse = responseWithStatusOK;
                status = 200;
            }
        }
        return processApiResponse(requestMethod, path, operationId, status, apiResponse, components);
    }

    private List<StubMapping> processApiResponse(RequestMethod requestMethod, String path, String operationId, int status,
                                                 ApiResponse apiResponse, Components components) {

        RequestPattern requestPattern = createWireMockRequestPattern(requestMethod, path);
        List<StubMapping> stubMappings = new ArrayList<>();

        for(Map.Entry<String, MediaType> mediaTypeEntry : apiResponse.getContent().entrySet()) {

            if(!APPLICATION_JSON_CONTENT_TYPE.equals(mediaTypeEntry.getKey())
                && !APPLICATION_XML_CONTENT_TYPE.equals(mediaTypeEntry.getKey())) {

                continue;
            }

            ResponseDefinition responseDefinition = processMediaType(
                mediaTypeEntry.getValue(), mediaTypeEntry.getKey(), status, components);
            StubMapping stubMapping = new StubMapping();
            stubMapping.setRequest(requestPattern);
            stubMapping.setResponse(responseDefinition);
            stubMapping.setName(operationId + ": " + apiResponse.getDescription());
            stubMappings.add(stubMapping);
        }

        return stubMappings;
    }

    private ResponseDefinition processMediaType(MediaType mediaType, String contentType, int status, Components components) {

        Object exampleResponse;
        if (mediaType.getExample() != null) {
            exampleResponse = mediaType.getExample();
        } else if (mediaType.getExamples() != null && mediaType.getExamples().size() > 0) {
            exampleResponse = mediaType.getExamples().values().iterator().next().getValue();
        } else if (mediaType.getSchema() != null) {
            exampleResponse = openApiSchemaParsingStrategy.createExampleFromSchema(mediaType.getSchema(), components);
        } else {
            return WireMock.aResponse().build();
        }

        if(APPLICATION_JSON_CONTENT_TYPE.equals(contentType)) {
            return createWireMockJSONResponseDefinition(status, (JsonNode) exampleResponse);
        } else {
            return createWireMockXMLResponseDefinition(status, (JsonNode) exampleResponse);
        }
    }

    private RequestPattern createWireMockRequestPattern(RequestMethod requestMethod, String path) {
        return RequestPatternBuilder.newRequestPattern(requestMethod, WireMock.urlEqualTo(path)).build();
    }

    private ResponseDefinition createWireMockJSONResponseDefinition(int status, JsonNode rootNode) {

        return ResponseDefinitionBuilder.jsonResponse(rootNode.toString(), status);
    }

    private ResponseDefinition createWireMockXMLResponseDefinition(int status, JsonNode node) {

        return WireMock.aResponse()
            .withStatus(status)
            .withHeader(CONTENT_TYPE_KEY, APPLICATION_XML_CONTENT_TYPE)
            .withBody(nodeToXML(node))
            .build();
    }

    private String nodeToXML(JsonNode node) {

        JSONObject json = new JSONObject(node.toString());
        return XML.toString(json);
    }
}
