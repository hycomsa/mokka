package pl.hycom.mokka.emulator.mock.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;

import pl.hycom.mokka.emulator.logs.LogStatus;
import pl.hycom.mokka.emulator.mock.MockContext;
import pl.hycom.mokka.emulator.mock.handler.MockHandler;
import pl.hycom.mokka.emulator.mock.handler.MockHandlerException;
import pl.hycom.mokka.emulator.mock.model.MockConfiguration;
import pl.hycom.mokka.emulator.mock.model.XmlConfigurationContent;
import pl.hycom.mokka.util.thymeleaf.TemplateParser;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Component
@Slf4j
public class XmlMockHandler implements MockHandler {

    @Autowired
    private TemplateParser templateParser;

    @Override
    public boolean canHandle(MockConfiguration mockConfiguration, MockContext ctx) {
        return mockConfiguration != null && mockConfiguration
                .getConfigurationContent() instanceof XmlConfigurationContent;
    }

    @Override
    public void handle(MockConfiguration mockConfiguration, MockContext ctx) throws MockHandlerException {
        try {
            XmlConfigurationContent content = (XmlConfigurationContent) mockConfiguration.getConfigurationContent();
            String response;
            if (ctx.getRequest()!=null) {

                 response = templateParser
                        .parse(content.getValue(), ImmutableMap.of("ctx", ctx, "mockConfiguration", mockConfiguration),
                               ctx.getRequest(), ctx.getResponse());
            }else{
                response=content.getValue();
            }
            if (ctx.getResponse() != null) {
                ctx.getResponse().getWriter().write(response);
            } else {
                ctx.getResponseMessage().setText(response);

            }
            ctx.getLogBuilder().response(response).status(LogStatus.OK);

        } catch (Exception e) {
            log.error("", e);
            ctx.getLogBuilder().response(e.getMessage()).status(LogStatus.ERROR);
        }

    }
}
