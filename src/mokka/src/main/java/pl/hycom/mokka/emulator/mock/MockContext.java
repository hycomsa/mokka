package pl.hycom.mokka.emulator.mock;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import pl.hycom.mokka.emulator.logs.model.Log;
import pl.hycom.mokka.emulator.logs.model.Log.LogBuilder;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.sql.Timestamp;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Getter
@Slf4j
public class MockContext {

    private String uri;

    private String httpMethod;

    private String requestBody;

    private String from;

    private LogBuilder logBuilder;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private ActiveMQTextMessage requestMessage;

    private TextMessage responseMessage;


    public MockContext(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        uri = StringUtils.removeStart((String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI), "/");
        requestBody = getRequestBody(request);
        from = getSenderAddress(request);
        httpMethod = getHttpMethod(request);

        logBuilder = Log.builder().uri(uri).httpMethod(httpMethod).request(requestBody).from(from)
                .date(new Timestamp(System.currentTimeMillis()));
    }

    public MockContext(ActiveMQTextMessage requestMessage, TextMessage responseMessage) throws JMSException {
        this.requestMessage = requestMessage;
        this.responseMessage = responseMessage;
        uri = requestMessage.getDestination().getPhysicalName();
        requestBody = requestMessage.getText();

        logBuilder = Log.builder().uri(uri).request(requestBody).from(from)
                .date(new Timestamp(System.currentTimeMillis()));
    }

    private static String getSenderAddress(HttpServletRequest request) {
        if (StringUtils.isNotBlank(request.getHeader("X-Forwarded-For"))) {
            return request.getHeader("X-Forwarded-For");
        }

        if (StringUtils.isNotBlank(request.getRemoteHost())) {
            return request.getRemoteHost();
        }

        return request.getRemoteAddr();
    }

    private static String getHttpMethod(HttpServletRequest request) {
        return request.getMethod();
    }

    private static String getRequestBody(HttpServletRequest req) {

        String requestBody = StringUtils.EMPTY;
        try {
            requestBody = IOUtils.toString(new InputStreamReader(req.getInputStream()));
        } catch (IOException e) {
            log.error("Error occured when reading body of the request", e);
            return requestBody;
        }

        if (StringUtils.isBlank(requestBody)) {
            log.info("Request body is empty.");
            return requestBody;
        }

        try {
            StringWriter sw = new StringWriter();
            new XMLWriter(sw, OutputFormat.createCompactFormat()).write(DocumentHelper.parseText(requestBody));
            return sw.toString();
        } catch (IOException | DocumentException e) {
            log.warn("Error occured when XML parsing body of the request: {}", e.getMessage());
            log.info("Raw string will be used.");
            return requestBody;
        }
    }
}
