package pl.hycom.mokka;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import pl.hycom.mokka.jms.emulator.JmsSessionAwareMessageListenerImpl;

/**
 * @author Mariusz Krysztofowoicz (mariusz.krysztofowicz@hycom.pl)
 */
@Configuration
public class JmsConfiguration {

    @Value("${activemq.broker-url}")
    private String brokerUrl;

    @Value("${activemq.default-destination-name}")
    private String destinationName;

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(brokerUrl);

        return activeMQConnectionFactory;
    }

    @Bean
    public BrokerService brokerService() throws Exception {
        BrokerService brokerService = new BrokerService();

        brokerService.addConnector(brokerUrl);
        brokerService.setPersistent(false);
        brokerService.setUseJmx(false);
        brokerService.start();
        return brokerService;
    }

    @Bean
    public DefaultMessageListenerContainer defaultMessageListenerContainer() {
        DefaultMessageListenerContainer defaultMessageListenerContainer = new DefaultMessageListenerContainer();

        defaultMessageListenerContainer.setMessageListener(jmsSessionAwareMessageListenerImpl());
        defaultMessageListenerContainer.setDestination(new ActiveMQQueue(destinationName));
        defaultMessageListenerContainer.setConnectionFactory(activeMQConnectionFactory());
        return defaultMessageListenerContainer;
    }

    @Bean
    public JmsSessionAwareMessageListenerImpl jmsSessionAwareMessageListenerImpl() {
        return new JmsSessionAwareMessageListenerImpl();
    }

}
