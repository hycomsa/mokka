package pl.hycom.mokka.emulator.mock.handler.impl;

import com.github.tomakehurst.wiremock.extension.responsetemplating.RequestTemplateModel;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pl.hycom.mokka.emulator.logs.LogStatus;
import pl.hycom.mokka.emulator.mock.MockContext;
import pl.hycom.mokka.emulator.mock.handler.MockHandler;
import pl.hycom.mokka.emulator.mock.handler.MockHandlerException;
import pl.hycom.mokka.emulator.mock.model.GroovyConfigurationContent;
import pl.hycom.mokka.emulator.mock.model.MockConfiguration;

import javax.jms.JMSException;
import java.io.IOException;
import java.util.Map.Entry;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Component
@Slf4j
public class GroovyMockHandler implements MockHandler {

	@Override
	public boolean canHandle(MockConfiguration mockConfiguration, MockContext ctx) {
		return mockConfiguration != null && mockConfiguration.getConfigurationContent() instanceof GroovyConfigurationContent;
	}

    @Override
    public void handle(MockConfiguration mockConfiguration, MockContext ctx) throws MockHandlerException {
        try {
            GroovyConfigurationContent content = (GroovyConfigurationContent) mockConfiguration.getConfigurationContent();
            Binding binding = new Binding();

            binding.setVariable("ctx", ctx);
            binding.setVariable("request", RequestTemplateModel.from(new MockHttpServletRequestAdapter(ctx)));

            Object value = resolveValue(content.getScript(), binding);
            setReponseBody(ctx, value);

            StringBuilder responseLog = createResponseLog(content, binding, value);

            ctx.getLogBuilder().response(responseLog.toString()).status(LogStatus.OK);

        } catch (Exception e) {
            log.error("", e);
            ctx.getLogBuilder().response(e.getMessage()).status(LogStatus.ERROR);
        }

    }

    private Object resolveValue(String script, Binding binding) {
        try {
            return new GroovyShell(binding).evaluate(script);
        } catch (Exception e) {
            return script;
        }
    }

    private void setReponseBody(MockContext ctx, Object value) throws IOException, JMSException {
        if (ctx.getResponse() != null) {
            ctx.getResponse().getWriter().write(value != null ? value.toString() : StringUtils.EMPTY);
        } else {
            ctx.getResponseMessage().setText(value != null ? value.toString() : StringUtils.EMPTY);
        }
    }

    private StringBuilder createResponseLog(GroovyConfigurationContent content, Binding binding, Object value) {
        StringBuilder responseLog = new StringBuilder();
        responseLog.append("Script:\n\n").append(content.getScript()).append("\n\n-------------------------\n\n");
        responseLog.append("Response:\n\n").append(value != null ? value.toString() : StringUtils.EMPTY).append("\n\n-------------------------\n\n");
        responseLog.append("Variables:\n\n");
        for (Object o : binding.getVariables().entrySet()) {
            if (o instanceof Entry) {
                Entry e = (Entry) o;
                if (ArrayUtils.contains(new String[]{"ctx", "request"}, e.getKey())) {
                    continue;
                }

                responseLog.append("\t").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
            }
        }
        return responseLog;
    }
}
