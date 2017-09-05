package pl.hycom.mokka.emulator.mock;

import com.google.common.collect.ImmutableList;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.hycom.mokka.emulator.mock.model.Change;
import pl.hycom.mokka.emulator.mock.model.JmsMockConfiguration;
import pl.hycom.mokka.emulator.mock.model.MockConfiguration;
import pl.hycom.mokka.security.UserRepository;
import pl.hycom.mokka.security.model.AuditedRevisionEntity;
import pl.hycom.mokka.security.model.User;
import pl.hycom.mokka.util.query.MockSearch;
import pl.hycom.mokka.util.query.Q;
import pl.hycom.mokka.util.query.QManager;

import javax.persistence.Lob;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * @author Tomasz Wozniak (tomasz.wozniak@hycom.pl)
 */
@Slf4j
@Component
public class JmsMockConfigurationManager {

    public JmsMockConfiguration hello(String name){
        JmsMockConfiguration jmsMockConfiguration = new JmsMockConfiguration();
        jmsMockConfiguration.setDescription("Hy " + name);
        return jmsMockConfiguration;
    }

    public JmsMockConfiguration returnSampleJmsMock(){
        JmsMockConfiguration jmsMockConfiguration = new JmsMockConfiguration();
        jmsMockConfiguration.setDescription("Descrptionnnn...");
        jmsMockConfiguration.setStatus("200");
        jmsMockConfiguration.setEnabled(false);
        jmsMockConfiguration.setName("JMSmock Name");
        jmsMockConfiguration.setTimeout(0);
        jmsMockConfiguration.setId("id4");
        jmsMockConfiguration.setOrder(1);
        jmsMockConfiguration.setReplyToHeader(false);
        jmsMockConfiguration.setRequestQueueName("Sample Request Queue Name");
        jmsMockConfiguration.setResponseQueueName("Sample Response Queue Name");
        jmsMockConfiguration.setType("xml");
        jmsMockConfiguration.setConfigurationContent("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:cli=\"http://esb.polkomtel.pl/clientheader-v1\" xmlns:por=\"http://esb.polkomtel.pl/adapter/HYBRIS/client/Portability\">\n" +
                "    <soap:Header>\n" +
                "        <cli:ClientHeader>\n" +
                "\n" +
                "        </cli:ClientHeader>\n" +
                "    </soap:Header>\n" +
                "    <soap:Body>\n" +
                "        <por:CreatePortabilityCaseResponse>\n" +
                "            <ResultStatus>\n" +
                "                <Status>OK</Status>\n" +
                "                <!--Optional:-->\n" +
                "                <ErrorCode>123</ErrorCode>\n" +
                "                <!--Optional:-->\n" +
                "                <ErrorDescription>?</ErrorDescription>\n" +
                "            </ResultStatus>\n" +
                "        </por:CreatePortabilityCaseResponse>\n" +
                "    </soap:Body>\n" +
                "</soap:Envelope>");
        return jmsMockConfiguration;
    }

}
