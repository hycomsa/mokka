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
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Slf4j
@Component
public class JmsMockConfigurationManager {

    public String hello(){
        return "Cześć ";
    }

}
