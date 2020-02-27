package pl.hycom.mokka.handler.impl;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.DelegatingServletInputStream;
import pl.hycom.mokka.emulator.mock.MockContext;
import pl.hycom.mokka.emulator.mock.handler.MockHandlerException;
import pl.hycom.mokka.emulator.mock.handler.impl.GroovyMockHandler;
import pl.hycom.mokka.emulator.mock.model.GroovyConfigurationContent;
import pl.hycom.mokka.emulator.mock.model.MockConfiguration;
import wiremock.org.apache.commons.io.IOUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Bartosz Kuron (bartosz.kuron@hycom.pl)
 */
public class GroovyMockHandlerTest {

    private static final String FROM = "000.000.0.000";
    private static final String URL_METHOD = "test-body";
    private static final String HTTP_METHOD = "GET";
    private static final String REQUEST_BODY = "requestBody";
    private static final String SEPARATOR = "result+='|'\n";

    private GroovyMockHandler groovyMockHandler = new GroovyMockHandler();
    private HttpServletRequest request;


    @BeforeEach
    public void initialize() throws IOException {
        request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn(HTTP_METHOD);
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(new ArrayList<String>(Collections.singletonList("HeaderName"))));
        when(request.getHeaders(any())).thenReturn(Collections.enumeration(new ArrayList<String>(Collections.singletonList("Header"))));
        when(request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI)).thenReturn("/" + URL_METHOD);
        when(request.getRemoteHost()).thenReturn(FROM);
        when(request.getInputStream()).thenReturn(new DelegatingServletInputStream(IOUtils.toInputStream(REQUEST_BODY)));
    }

    @Test
    public void handle_test() throws IOException, MockHandlerException {
        //given
        String initialBody = prepareInitialGroovyBody();
        StringWriter stringWriter = new StringWriter();
        MockContext mockContext = prepareMockContext(stringWriter);

        //when
        groovyMockHandler.handle(prepareMockConfiguration(initialBody), mockContext);

        //then
        Assertions.assertEquals(URL_METHOD, mockContext.getUri());
        Assertions.assertEquals(FROM, mockContext.getFrom());

        String expectedBody = URL_METHOD + "|" + HTTP_METHOD + "|" + REQUEST_BODY + "|" + FROM + "|" + HTTP_METHOD;
        Assertions.assertEquals(expectedBody, stringWriter.toString());
    }

    @Test
    public void handle_no_groovy_input_test() throws IOException, MockHandlerException {
        //given
        String initialBody = "Random not groovy text";
        StringWriter stringWriter = new StringWriter();
        MockContext mockContext = prepareMockContext(stringWriter);

        //when
        groovyMockHandler.handle(prepareMockConfiguration(initialBody), mockContext);

        //then
        Assertions.assertEquals(URL_METHOD, mockContext.getUri());
        Assertions.assertEquals(initialBody, stringWriter.toString());
    }

    @Test
    public void handle_null_input_test() throws IOException, MockHandlerException {
        //given
        String initialBody = null;
        StringWriter stringWriter = new StringWriter();
        MockContext mockContext = prepareMockContext(stringWriter);

        //when
        groovyMockHandler.handle(prepareMockConfiguration(initialBody), mockContext);

        //then
        Assertions.assertEquals(URL_METHOD, mockContext.getUri());
        Assertions.assertEquals(StringUtils.EMPTY, stringWriter.toString());
    }

    private MockConfiguration prepareMockConfiguration(String initialBody) {
        GroovyConfigurationContent groovyConfigurationContent = new GroovyConfigurationContent();
        groovyConfigurationContent.setScript(initialBody);

        MockConfiguration mockConfiguration = mock(MockConfiguration.class);
        when(mockConfiguration.getConfigurationContent()).thenReturn(groovyConfigurationContent);
        return mockConfiguration;
    }

    private MockContext prepareMockContext(StringWriter stringWriter) throws IOException {

        HttpServletResponse response = mock(HttpServletResponse.class);

        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        return new MockContext(request, response);
    }

    private String prepareInitialGroovyBody() {
        return "result=ctx.uri\n" +
            SEPARATOR +
            "result+=ctx.httpMethod\n" +
            SEPARATOR +
            "result+=ctx.requestBody\n" +
            SEPARATOR +
            "result+=ctx.from\n" +
            SEPARATOR +
            "result+=request.requestLine.method\n" +
            "return result";
    }
}
