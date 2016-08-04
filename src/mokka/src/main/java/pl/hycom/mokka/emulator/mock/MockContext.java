package pl.hycom.mokka.emulator.mock;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import pl.hycom.mokka.emulator.logs.model.Log;
import pl.hycom.mokka.emulator.logs.model.Log.LogBuilder;

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
	private String requestBody;
	private String from;

	private LogBuilder logBuilder;

	private HttpServletRequest request;
	private HttpServletResponse response;

	public MockContext(HttpServletRequest request, HttpServletResponse response) throws DocumentException {
		this.request = request;
		this.response = response;
		uri = StringUtils.removeStart((String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI), "/");
		requestBody = getRequestBody(request);
		from = getSenderAddress(request);

		logBuilder = Log.builder().uri(uri).request(requestBody).from(from).date(new Timestamp(System.currentTimeMillis()));
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

	private static String getRequestBody(HttpServletRequest req) throws DocumentException {
		try {
			String result = IOUtils.toString(new InputStreamReader(req.getInputStream()));
			StringWriter sw = new StringWriter();
			new XMLWriter(sw, OutputFormat.createPrettyPrint()).write(DocumentHelper.parseText(result));
			result = sw.toString();

			return result;
		} catch (IOException e) {
			log.error("", e);
		}

		return null;
	}
}
