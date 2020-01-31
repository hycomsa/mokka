package pl.hycom.mokka.jms.emulator;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.SessionAwareMessageListener;
import pl.hycom.mokka.emulator.logs.LogManager;
import pl.hycom.mokka.emulator.logs.LogStatus;
import pl.hycom.mokka.emulator.mock.MockContext;
import pl.hycom.mokka.emulator.mock.MockFinder;
import pl.hycom.mokka.emulator.mock.handler.MockHandler;
import pl.hycom.mokka.emulator.mock.handler.MockHandlerException;
import pl.hycom.mokka.emulator.mock.model.MockConfiguration;


import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.List;

/**
 * @author Mariusz Krysztofowoicz (mariusz.krysztofowicz@hycom.pl)
 */
@Slf4j
public class JmsSessionAwareMessageListenerImpl implements SessionAwareMessageListener {


    @Autowired
    private MockFinder mockFinder;

    @Autowired
    private LogManager logManager;

    @Autowired
    private List<MockHandler> mockHandlers;
    @Override
    public void onMessage(Message message, Session session) {
        try {
            TextMessage response = session.createTextMessage();
            if (message instanceof ActiveMQTextMessage) {
                MockContext mockContext=new MockContext((ActiveMQTextMessage)message,response);
                MockConfiguration mockConfiguration =    mockFinder.findMock(mockContext);
                handleMockResponse(mockContext,mockConfiguration);
            }

            response.setJMSCorrelationID(message.getJMSMessageID());
            try (MessageProducer replyProducer = session.createProducer(null)) {
                replyProducer.send(message.getJMSReplyTo(), response);
            }
        }catch (Exception e) {
            log.error("Unexpected error occurred during message handling {}",e);
        }
    }

    private void handleMockResponse(MockContext ctx, MockConfiguration mockConfiguration) throws InterruptedException,
                                                                                                 MockHandlerException {

        if (mockConfiguration.getTimeout() > 0) {
            log.info("Waiting for timeout: " + mockConfiguration.getTimeout());
            Thread.sleep(mockConfiguration.getTimeout());
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
            log.warn("No handler for mock[" + mockConfiguration.getId() + "]!");
            ctx.getLogBuilder().status(LogStatus.NOT_FOUND).response("No handler found!");
        }

        logManager.saveLog(ctx.getLogBuilder().configurationId(mockConfiguration.getId()));
    }

}
