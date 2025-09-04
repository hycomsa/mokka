package pl.hycom.mokka.emulator.mock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import pl.hycom.mokka.emulator.logs.LogManager;
import pl.hycom.mokka.emulator.logs.LogStatus;
import pl.hycom.mokka.emulator.mock.handler.MockHandler;
import pl.hycom.mokka.emulator.mock.handler.MockHandlerException;
import pl.hycom.mokka.emulator.mock.model.MockConfiguration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Slf4j
public class MockInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private MockFinder mockFinder;

	@Autowired
	private LogManager logManager;

	@Autowired
	private List<MockHandler> mockHandlers;

    @Value("${mock.response.addDebugHeaders}")
    private boolean rspDebugHeadersEnabled;

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object arg2) throws Exception {

		log.debug("Mokka interceptor received request: {} {}", req.getMethod(), req.getRequestURI());
		MockContext ctx = new MockContext(req, res);

		// Log request body for diagnostics (JSON/form, etc.)
		log.debug("Mokka request body (Content-Type: {}): {}", req.getContentType(), ctx.getRequestBody());

		// Do not set response headers here; only set them when a mock is actually handled

		MockConfiguration mockConfiguration = mockFinder.findMock(ctx);

		if (mockConfiguration == null) {
			log.info("Mokka could not find a matching mock for [{} {}].", req.getMethod(), req.getRequestURI());
			return true; // Let Spring continue to controllers/static resources
		}

        log.info("Found mock [id={}] for uri=[{}]", mockConfiguration.getId(), ctx.getUri());
        log.debug("Returned mock: {} ", mockConfiguration);

		handleMockResponse(ctx, mockConfiguration);

		return false; // Request was handled by Mokka
	}

	private void handleMockResponse(MockContext ctx, MockConfiguration mockConfiguration) {
        try {
            // Set headers only when we are returning a mock response
            ctx.getResponse().setHeader("Access-Control-Allow-Origin", "*");
            ctx.getResponse().addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
            ctx.getResponse().setContentType(ctx.getRequest().getContentType());

            if (mockConfiguration.getTimeout() > 0) {
                log.info("Applying mock [id={}] timeout: {}", mockConfiguration.getId(), mockConfiguration.getTimeout());
                Thread.sleep(mockConfiguration.getTimeout());
            }

            if (rspDebugHeadersEnabled) {
                log.info("Adding debug header to mock response.");
                ctx.getResponse().addHeader("X-Mock-Id", mockConfiguration.getId().toString());
            }

            ctx.getResponse().setStatus(mockConfiguration.getStatus());

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

            logManager.saveLog(ctx.getLogBuilder().configurationId(mockConfiguration.getId()).name(mockConfiguration.getName()));

        } catch (InterruptedException | MockHandlerException e) {
            log.error("Error while processing mock for uri [{}]", ctx.getUri(), e);
        }
    }
}
