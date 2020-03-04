package pl.hycom.mokka.stubbing.responsetemplating;

import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Bartosz Kuron (bartosz.kuron@hycom.pl)
 */

public class GroovyResponseTransformerTest {

    private static final String URL = "body";
    private static final RequestMethod REQUEST_METHOD = RequestMethod.GET;
    private static final String FROM = "000.00.0.000";
    private static final String REQUEST_BODY_CONTENT = "request body content";

    private ResponseDefinition responseDefinition;

    private Request request;

    private GroovyResponseTransformer groovyResponseTransformer = new GroovyResponseTransformer();

    @BeforeEach
    public void initialize(){
        request = mock(com.github.tomakehurst.wiremock.http.Request.class);
        when(request.getUrl()).thenReturn("/"+URL);
        when(request.getMethod()).thenReturn(RequestMethod.GET);
        when(request.getClientIp()).thenReturn(FROM);
        when(request.getBodyAsString()).thenReturn(REQUEST_BODY_CONTENT);


        responseDefinition = mock(ResponseDefinition.class);
        when(responseDefinition.getHeaders()).thenReturn(new HttpHeaders());
        when(responseDefinition.getStatus()).thenReturn(205);
    }

    @Test
    public void transform_test() {
        String body = "result=ctx.uri\n" +
            "result+='|'\n"  +
            "result+=ctx.httpMethod\n"  +
            "result+='|'\n"  +
            "result+=ctx.requestBody\n" +
            "result+='|'\n"  +
            "result+=ctx.from\n" +
            "result+='|'\n"  +
            "result+=request.requestLine.method\n" +
            "return result";

        when(responseDefinition.getBody()).thenReturn(body);
        ResponseDefinition result = groovyResponseTransformer.transform(request, responseDefinition, null, null);

        assertNotNull(result);
        assertEquals(205, result.getStatus());

        String expectedResponse = URL + "|" + REQUEST_METHOD.toString() + "|" + REQUEST_BODY_CONTENT + "|" + FROM + "|" + REQUEST_METHOD;
        assertEquals(expectedResponse, result.getBody());
    }

    @Test
    public void transform_no_groovy_input_test() {
        String body = "Random non groovy test";
        when(responseDefinition.getBody()).thenReturn(body);

        ResponseDefinition result = groovyResponseTransformer.transform(request, responseDefinition, null, null);

        assertNotNull(result);
        assertEquals(205, result.getStatus());

        assertEquals(body, result.getBody());
    }

    @Test
    public void transform_empty_body_test() {
        when(responseDefinition.getBody()).thenReturn(null);

        ResponseDefinition result = groovyResponseTransformer.transform(request, responseDefinition, null, null);

        assertNotNull(result);
        assertEquals(205, result.getStatus());
        assertEquals(StringUtils.EMPTY, result.getBody());
    }

}
