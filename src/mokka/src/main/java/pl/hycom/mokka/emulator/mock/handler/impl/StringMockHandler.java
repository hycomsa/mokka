package pl.hycom.mokka.emulator.mock.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import pl.hycom.mokka.emulator.logs.LogStatus;
import pl.hycom.mokka.emulator.mock.MockContext;
import pl.hycom.mokka.emulator.mock.handler.MockHandler;
import pl.hycom.mokka.emulator.mock.handler.MockHandlerException;
import pl.hycom.mokka.emulator.mock.model.MockConfiguration;
import pl.hycom.mokka.emulator.mock.model.StringConfigurationContent;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Component
@Slf4j
public class StringMockHandler implements MockHandler {

    @Override
    public boolean canHandle(MockConfiguration mockConfiguration, MockContext ctx) {
        return mockConfiguration != null && mockConfiguration
                .getConfigurationContent() instanceof StringConfigurationContent;
    }

    @Override
    public void handle(MockConfiguration mockConfiguration, MockContext ctx) throws MockHandlerException {
        try {
            StringConfigurationContent content = (StringConfigurationContent) mockConfiguration
                    .getConfigurationContent();
            if (ctx.getResponse() != null) {
                ctx.getResponse().getWriter().write(content.getValue());
            } else {
                ctx.getResponseMessage().setText(content.getValue());
            }
            ctx.getLogBuilder().response(content.getValue()).status(LogStatus.OK);

        } catch (Exception e) {
            log.error("", e);
            ctx.getLogBuilder().response(e.getMessage()).status(LogStatus.ERROR);
        }

    }
}
