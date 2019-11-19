package pl.hycom.mokka.emulator.mock;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import pl.hycom.mokka.emulator.logs.LogManager;
import pl.hycom.mokka.emulator.logs.LogStatus;
import pl.hycom.mokka.emulator.mock.handler.MockHandler;
import pl.hycom.mokka.emulator.mock.handler.MockHandlerException;
import pl.hycom.mokka.emulator.mock.model.MockConfiguration;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Slf4j
@Scope("request")
public class MockInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private MockFinder mockFinder;

	@Autowired
	private LogManager logManager;

	@Autowired
	private MockConfigurationManager mockConfigurationManager;

	@Autowired
	private List<MockHandler> mockHandlers;

    @Value("${mock.response.addDebugHeaders}")
    private boolean rspDebugHeadersEnabled;

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object arg2) throws Exception {

		if (!checkIfMockExists(req)) {
			return true;
		}

		MockContext ctx = new MockContext(req, res);

		res.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
		res.setContentType(req.getContentType());

		MockConfiguration mockConfiguration = mockFinder.findMock(ctx);

		if (mockConfiguration == null) {
			handleNoMockResposne(ctx);
			return false;
		}

        log.info("Found mock [id={}] for uri=[{}]", mockConfiguration.getId(), ctx.getUri());
        log.debug("Returned mock: {} ", mockConfiguration);

		handleMockResponse(ctx, mockConfiguration);

		return false;
	}

	private void handleMockResponse(MockContext ctx, MockConfiguration mockConfiguration) throws InterruptedException, MockHandlerException {

		if (mockConfiguration.getTimeout() > 0) {
			log.info("Applying mock [id={}] timeout: {}", mockConfiguration.getId(), mockConfiguration.getTimeout());
			Thread.sleep(mockConfiguration.getTimeout());
		}

		if (rspDebugHeadersEnabled) {
		    log.info("Adding debug header to mock response.");
            ctx.getResponse().addHeader("X-Mock-Id", mockConfiguration.getId().toString());
        }

		if(HttpStatus.valueOf(mockConfiguration.getStatus()).is4xxClientError() || HttpStatus.valueOf(mockConfiguration.getStatus()).is5xxServerError()){
			try {
				ctx.getResponse().sendError(HttpStatus.valueOf(mockConfiguration.getStatus()).value(), HttpStatus.valueOf(mockConfiguration.getStatus()).getReasonPhrase());
			} catch (IOException e) {
				log.warn("HttpStatus exception", e);
			}
		}else{
			ctx.getResponse().setStatus(mockConfiguration.getStatus());
		}

		boolean handled = false;
		for (MockHandler handler : mockHandlers) {
			if (handler.canHandle(mockConfiguration, ctx)) {
				handler.handle(mockConfiguration, ctx);
				handled = true;
				break;
			}
		}

		if (!handled) {
			log.warn("No handler for mock[{}]!", mockConfiguration.getId());
			ctx.getLogBuilder().status(LogStatus.NOT_FOUND).response("No handler found!");
		}

		logManager.saveLog(ctx.getLogBuilder().configurationId(mockConfiguration.getId()));
	}

	private void handleNoMockResposne(MockContext ctx) throws IOException {
		log.warn("Mock for uri [{}] not found!", ctx.getUri());

		ctx.getResponse().setStatus(HttpStatus.NOT_FOUND.value());
		ctx.getResponse().getWriter().write("Mock for uri[" + ctx.getUri() + "] not found");

		ctx.getLogBuilder().status(LogStatus.NOT_FOUND);
		logManager.saveLog(ctx.getLogBuilder());
	}

	private boolean checkIfMockExists(HttpServletRequest req) {
		String uri = StringUtils.removeStart((String) req.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI), "/");

		if (StringUtils.isBlank(uri)) {
			return false;
		}

		return mockConfigurationManager.existsPath(uri);
	}
}
