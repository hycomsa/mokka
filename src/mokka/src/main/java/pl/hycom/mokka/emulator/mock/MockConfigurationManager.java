package pl.hycom.mokka.emulator.mock;

import com.google.common.collect.ImmutableList;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.hycom.mokka.emulator.mock.model.Change;
import pl.hycom.mokka.emulator.mock.model.ConfigurationContent;
import pl.hycom.mokka.emulator.mock.model.GroovyConfigurationContent;
import pl.hycom.mokka.emulator.mock.model.MockConfiguration;
import pl.hycom.mokka.emulator.mock.model.StringConfigurationContent;
import pl.hycom.mokka.emulator.mock.model.WrappedMockConfiguration;
import pl.hycom.mokka.emulator.mock.model.XmlConfigurationContent;
import pl.hycom.mokka.event.StubMappingCreatedEvent;
import pl.hycom.mokka.event.StubMappingDisabledEvent;
import pl.hycom.mokka.event.StubMappingEnabledEvent;
import pl.hycom.mokka.event.StubMappingRemovedEvent;
import pl.hycom.mokka.event.StubMappingUpdatedEvent;
import pl.hycom.mokka.security.UserRepository;
import pl.hycom.mokka.security.model.AuditedRevisionEntity;
import pl.hycom.mokka.security.model.User;
import pl.hycom.mokka.util.ArithmeticUtils;
import pl.hycom.mokka.util.query.MockSearch;
import pl.hycom.mokka.util.query.Q;
import pl.hycom.mokka.util.query.QManager;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Slf4j
@Component
public class MockConfigurationManager {

    public static final String MOCK_ID = "Mock (id: ";

    public static final String PATTERN = "pattern";

    public static final String NEW_VALUE = "newValue";

    public static final String OLD_VALUE = "oldValue";

    public static final String NOT_VALID = "not-valid";

    private static int numberOfResultsPerQuery = 10;

    private static final Map<String, Class> map;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private QManager qManager;

    @Autowired
    private MockSearch mockSearch;

    @Autowired
    private MockConfigurationRepository repository;

    @Autowired
    private UserRepository userRepository;

    private AtomicReference<Set<String>> pathCache = new AtomicReference<>();

    @Value("${mocksPerPage}")
    private Integer mocksPerPage;

    WrappedMockConfiguration wrappedMockConfiguration = new WrappedMockConfiguration();

    public MockConfigurationManager(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void reportCurrentTime() {
        updatePathcache();
    }

    static {
        map = new HashMap<>();

        map.put("StringConfigurationContent", StringConfigurationContent.class);
        map.put("XmlConfigurationContent", XmlConfigurationContent.class);
        map.put("GroovyConfigurationContent", GroovyConfigurationContent.class);
    }

    @Async
    private void updatePathcache() {
        pathCache.set(repository.findUniquePaths());
        log.debug("pathCache updated and contains " + pathCache.get().size() + " items");
    }

    @Transactional
    public MockConfiguration createMockConfiguration(MockConfiguration mock){
        if (mock == null) {
            return mock;
        }

        mock.setUpdated(new Date(System.currentTimeMillis()));

        MockConfiguration m = repository.save(mock);

        StubMappingCreatedEvent stubMappingCreatedEvent = new StubMappingCreatedEvent(this, mock.getId());
        applicationEventPublisher.publishEvent(stubMappingCreatedEvent);

        log.info(MOCK_ID + "{}) added", mock.getId());

        updatePathcache();

        return m;
    }

    @Transactional
    public MockConfiguration updateMockConfiguration(MockConfiguration mock){
        if (mock == null) {
            return mock;
        }

        mock.setUpdated(new Date(System.currentTimeMillis()));

        MockConfiguration m = repository.save(mock);

        StubMappingUpdatedEvent stubMappingUpdatedEvent = new StubMappingUpdatedEvent(this, mock.getId());
        applicationEventPublisher.publishEvent(stubMappingUpdatedEvent);

        log.info(MOCK_ID + "{}) updated", mock.getId());

        updatePathcache();

        return m;
    }

    @Transactional
    @Synchronized
    public boolean removeMockConfiguration(Long id) {
        try {
            repository.deleteById(id);

            StubMappingRemovedEvent stubMappingRemovedEvent = new StubMappingRemovedEvent(this, id);
            applicationEventPublisher.publishEvent(stubMappingRemovedEvent);

            log.info(MOCK_ID + "{}) deleted", id);
            return true;
        } catch (Exception e) {
            log.error(MOCK_ID + "{}) could not be deleted", id);
        }

        updatePathcache();

        return !repository.findById(id).isPresent();
    }

    public MockConfiguration findFirstAvailableMockConfiguration(
        String path, String requestBody, String httpMethod) {
        Set<String> paths = new HashSet<>();
        paths.add(path);
        for (String p : pathCache.get()) {
            if (Pattern.matches(p, path)) {
                paths.add(p);
            }
        }

        Q q = Q.select("m").from("MockConfig m").where(Q.in("path", paths)).and(Q.eq("m.enabled", true))
                .orderby("m.order DESC");

        List<MockConfiguration> result = qManager.execute(q, MockConfiguration.class);

        for (MockConfiguration mc : result) {
            if (isNotEmpty(mc.getHttpMethod()) && !equalsIgnoreCase(mc.getHttpMethod(), httpMethod)) {
                log.debug("Mock [id={}] HTTP method [{}] does not match request HTTP method [{}].", mc.getId(), mc.getHttpMethod(), httpMethod);
                continue;
            }

            if (isBlank(mc.getPattern())) {
                return mc;
            }

            Pattern pattern = Pattern.compile(mc.getPattern(), Pattern.DOTALL);
            if (pattern.matcher(requestBody).matches()) {
                return mc;
            }
        }

        return null;
    }

    @Transactional
    public boolean disable(Long id) {
        if (id == null) {
            return false;
        }

        Optional<MockConfiguration> mock = repository.findById(id);
        mock.ifPresent(m -> m.setEnabled(false));

        StubMappingDisabledEvent stubMappingDisabledEvent = new StubMappingDisabledEvent(this, id);
        applicationEventPublisher.publishEvent(stubMappingDisabledEvent);


        return mock.isPresent();
    }

    @Transactional
    public boolean enable(Long id) {
        if (id == null) {
            return false;
        }

        Optional<MockConfiguration> mock = repository.findById(id);
        mock.ifPresent(m -> m.setEnabled(true));

        StubMappingEnabledEvent stubMappingEnabledEvent = new StubMappingEnabledEvent(this, id);
        applicationEventPublisher.publishEvent(stubMappingEnabledEvent);


        return mock.isPresent();
    }

    public boolean existsPath(String path) {
        if (pathCache.get().contains(path)) {
            return true;
        }

        for (String p : pathCache.get()) {
            if (Pattern.matches(p, path)) {
                return true;
            }
        }

        return false;
    }

    public MockConfiguration getMockConfiguration(long id) {
        return repository.findById(id).orElse(null);
    }

    public WrappedMockConfiguration getMockConfigurations(HttpServletRequest req) {
        if (StringUtils.isNumeric(req.getParameter("show"))) {
            Optional<MockConfiguration> mc = repository.findById(Long.parseLong(req.getParameter("show")));

            wrappedMockConfiguration.mocks = mc.<List<MockConfiguration>>map(ImmutableList::of)
                .orElse(Collections.emptyList());
        }

        if (StringUtils.isNumeric(req.getParameter("from"))) {
            mockSearch.setStartingIndex(Integer.parseInt(req.getParameter("from")) * mocksPerPage);
        }

        // start perPage & startFrom
        if (StringUtils.isNumeric(req.getParameter("perPage"))) {
            mockSearch.setMaxResults(Integer.parseInt(req.getParameter("perPage")) * mocksPerPage);
        } else {
            mockSearch.setMaxResults(numberOfResultsPerQuery);
        }

        // id
        if (StringUtils.isNotBlank(req.getParameter(MockSearch.ID))) {
            mockSearch.add(MockSearch.ID, req.getParameter(MockSearch.ID));
        }

        // name
        if (StringUtils.isNotBlank(req.getParameter(MockSearch.NAME))) {
            mockSearch.add(MockSearch.NAME, req.getParameter(MockSearch.NAME));
        }

        // path
        if (StringUtils.isNotBlank(req.getParameter(MockSearch.PATH))) {
            mockSearch.add(MockSearch.PATH, req.getParameter(MockSearch.PATH));
        }

        // description
        if (StringUtils.isNotBlank(req.getParameter(MockSearch.DESCRIPTION))) {
            mockSearch.add(MockSearch.DESCRIPTION, req.getParameter(MockSearch.DESCRIPTION));
        }

        // path
        if (StringUtils.isNotBlank(req.getParameter(MockSearch.PATTERN))) {
            mockSearch.add(MockSearch.PATTERN, req.getParameter(MockSearch.PATTERN));
        }

        //enabled
        if (StringUtils.isNotBlank(req.getParameter(MockSearch.ENABLED))) {
            mockSearch.add(MockSearch.ENABLED, req.getParameter(MockSearch.ENABLED));
        }

        wrappedMockConfiguration.mocks = mockSearch.find();
        Long allMockCount = mockSearch.countAllPossibleResults();
        wrappedMockConfiguration.pageCount = ArithmeticUtils.divideAndCeil(allMockCount, mocksPerPage);
        mockSearch.clearParameterMap();

        return wrappedMockConfiguration;
    }

    public List<Change> getChanges(Long id) {
        if (id == null) {
            return Collections.emptyList();
        }

        Revisions<Integer, MockConfiguration> revisions = repository.findRevisions(id);

        List<Change> changes = new ArrayList<>(revisions.getContent().size());
        DiffMatchPatch dmp = new DiffMatchPatch();
        MockConfiguration mc = null;
        for (Revision<Integer, MockConfiguration> r : revisions.getContent()) {
            Change c = new Change();
            c.setAuthor(((AuditedRevisionEntity) r.getMetadata().getDelegate()).getAuthor());
            userRepository.findOneByUserName(c.getAuthor()).ifPresent(new Consumer<User>() {
                @Override
                public void accept(User u) {
                    c.setAuthor(u.getFirstName() + " " + u.getLastName());
                }
            });

            r.getRevisionInstant().ifPresent(i -> c.setDate(LocalDateTime.ofInstant(i, ZoneOffset.UTC)));
            c.getDiffs().putAll(getChangesForObject("", mc, r.getEntity(), MockConfiguration.class, dmp));

            if (!c.getDiffs().isEmpty()) {
                r.getRevisionNumber().ifPresent(c::setId);
                changes.add(c);
            }

            mc = r.getEntity();
        }

        Collections.reverse(changes);

        return changes;
    }

    private Map<String, Map<String, Object>> getChangesForObject(
            String fieldPrefix, Object o1, Object o2, Class<?> clazz, DiffMatchPatch dmp) {
        Map<String, Map<String, Object>> result = new HashMap<>();

        for (Field f : clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(TrackChanges.class)) {
                getObjectChangesForTrackChanges(fieldPrefix, o1, o2, dmp, result, f);
            }
        }

        return result;
    }

    private void getObjectChangesForTrackChanges(
            String fieldPrefix, Object o1, Object o2, DiffMatchPatch dmp, Map<String, Map<String, Object>> result,
            Field f) {
        try {
            f.setAccessible(true);

            Object o1Value = o1 == null ? null : f.get(o1);
            Object o2Value = o2 == null ? null : f.get(o2);

            if (!ClassUtils.isPrimitiveOrWrapper(f.getType()) && f.getType() != String.class) {
                o1Value = initializeAndUnproxy(o1Value);
                o2Value = initializeAndUnproxy(o2Value);
                putObjectChangesToResult(fieldPrefix, dmp, result, f, o1Value, o2Value);
            } else {
                if (o1Value == null && o2Value == null) {
                    return;
                }
                putPrimitiveChangeToResult(fieldPrefix, dmp, result, f, o1Value, o2Value);
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.error("", e);
        }
    }

    private void putPrimitiveChangeToResult(
            String fieldPrefix, DiffMatchPatch dmp, Map<String, Map<String, Object>> result, Field f, Object o1Value,
            Object o2Value) {
        Map<String, Object> changeValue = new HashMap<>();
        if ((o1Value == null && o2Value != null) || (o2Value == null && o1Value != null)) {
            changeValue.put(NEW_VALUE, o2Value);
            changeValue.put(OLD_VALUE, o1Value);

        } else if (o1Value != null && !o1Value.equals(o2Value)) {

            changeValue.put(NEW_VALUE, o2Value);
            changeValue.put(OLD_VALUE, o1Value);

        }

        if (!changeValue.isEmpty()) {
            result.put(fieldPrefix + f.getName(), changeValue);
        }
    }

    private void putObjectChangesToResult(
            String fieldPrefix, DiffMatchPatch dmp, Map<String, Map<String, Object>> result, Field f, Object o1Value,
            Object o2Value) {
        Class<?> fieldType = f.getType();
        if (o1Value != null && o2Value != null) {
            fieldType = o2Value.getClass();
            if (o1Value.getClass() != o2Value.getClass()) {
                Map<String, Object> changeValue = new HashMap<>();
                changeValue.put(NEW_VALUE, o2Value.getClass().getSimpleName());
                changeValue.put(OLD_VALUE, o1Value.getClass().getSimpleName());
                result.put(fieldPrefix + f.getName(), changeValue);
                o1Value = null;
            }
        }

        if (o1Value == null && o2Value != null) {
            fieldType = o2Value.getClass();
        }

        if (o1Value != null && o2Value == null) {
            fieldType = o1Value.getClass();
        }

        result.putAll(getChangesForObject(fieldPrefix + f.getName() + ".", o1Value, o2Value, fieldType, dmp));
    }

    private <T> T initializeAndUnproxy(T entity) {
        if (entity == null) {
            return entity;
        }

        Hibernate.initialize(entity);
        if (entity instanceof HibernateProxy) {
            return (T) ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
        }
        return entity;
    }

    public Map<String, String> validate(MockConfiguration mc) {
        Map<String, String> out = new HashMap<>();

        if (isBlank(mc.getPath())) {
            out.put("path", NOT_VALID);
        }

        if (StringUtils.isNotBlank(mc.getPattern())) {
            try {
                Pattern.compile(mc.getPattern());
            } catch (Exception e) {
                log.debug("Exception: ", e);
                out.put(PATTERN, NOT_VALID);
            }
        }

        if (mc.getTimeout() < 0) {
            out.put("timeout", NOT_VALID);
        }

        return out;
    }

    @Transactional
    public Change restoreChange(long configId, long changeId) {
        String oldValue = "";
        Integer intOldValue;
        MockConfiguration config = getMockConfiguration(configId);
        List<Change> changes = getChanges(configId);
        Optional<Change> optionalChange = changes.stream().filter(c -> c.getId().longValue() == changeId).findFirst();

        if (!optionalChange.isPresent()) {
            throw new IllegalArgumentException("No change found for config id " + configId + " with change id " + changeId);
        }

        Change change = optionalChange.get();

        if (!change.getDiffs().containsKey("configurationContent") && (change.getDiffs()
                .containsKey("configurationContent.script") || change.getDiffs()
                .containsKey("configurationContent.value"))) {

            if (change.getDiffs().containsKey("configurationContent.script")) {
                oldValue = (String) change.getDiffs().get("configurationContent.script").get("oldValue");
            }

            if (change.getDiffs().containsKey("configurationContent.value")) {
                oldValue = (String) change.getDiffs().get("configurationContent.value").get("oldValue");
            }

            if (config.getConfigurationContent() instanceof StringConfigurationContent) {
                StringConfigurationContent content = (StringConfigurationContent) config.getConfigurationContent();
                content.setValue(oldValue);

            } else if (config.getConfigurationContent() instanceof XmlConfigurationContent) {
                XmlConfigurationContent content = (XmlConfigurationContent) config.getConfigurationContent();
                content.setValue(oldValue);

            } else if (config.getConfigurationContent() instanceof GroovyConfigurationContent) {
                GroovyConfigurationContent content = (GroovyConfigurationContent) config.getConfigurationContent();
                content.setScript(oldValue);
            }
        } else if (change.getDiffs().containsKey("configurationContent")) {
            Change change1 = changes.stream().filter(c -> c.getId().longValue() < changeId)
                    .filter(c -> change.getDiffs().containsKey("configurationContent.script") || c.getDiffs()
                            .containsKey("configurationContent.value")).findFirst().orElse(null);
            Class<?> configurationContentClass = map.get(change.getDiffs().get("configurationContent").get("oldValue"));
            Object configurationContentNewInstance = null;
            Object newValue = change1.getDiffs().get("configurationContent.value").get("newValue");
            try {
                configurationContentNewInstance = configurationContentClass.getConstructor().newInstance();
                Method method = configurationContentNewInstance.getClass().getMethod("setValue", String.class);
                setValueMethodInvoke(method, configurationContentNewInstance, newValue);
                config.setConfigurationContent((ConfigurationContent) configurationContentNewInstance);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                log.warn("Exception: ", e);
            } catch (NoSuchMethodException e) {
                try {
                    Method method = configurationContentNewInstance.getClass().getMethod("setScript", String.class);
                    setValueMethodInvoke(method, configurationContentNewInstance, newValue);
                    config.setConfigurationContent((ConfigurationContent) configurationContentNewInstance);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                    log.warn("Exception: ", ex);
                }
            }
        }

        if (change.getDiffs().containsKey("path")) {
            oldValue = (String) change.getDiffs().get("path").get("oldValue");
            oldValue = ObjectUtils.defaultIfNull(oldValue, "");
            config.setPath(oldValue);
        }

        if (change.getDiffs().containsKey("pattern")) {
            oldValue = (String) change.getDiffs().get("pattern").get("oldValue");
            oldValue = ObjectUtils.defaultIfNull(oldValue, "");
            config.setPattern(oldValue);
        }

        if (change.getDiffs().containsKey("name")) {
            oldValue = (String) change.getDiffs().get("name").get("oldValue");
            oldValue = ObjectUtils.defaultIfNull(oldValue, "");
            config.setName(oldValue);
        }

        if (change.getDiffs().containsKey("description")) {
            oldValue = (String) change.getDiffs().get("description").get("oldValue");
            oldValue = ObjectUtils.defaultIfNull(oldValue, "");
            config.setDescription(oldValue);
        }

        if (change.getDiffs().containsKey("timeout")) {
            intOldValue = (Integer) change.getDiffs().get("timeout").get("oldValue");
            intOldValue = ObjectUtils.defaultIfNull(intOldValue, 0);
            config.setTimeout(intOldValue);
        }

        if (change.getDiffs().containsKey("order")) {
            intOldValue = (Integer) change.getDiffs().get("order").get("oldValue");
            intOldValue = ObjectUtils.defaultIfNull(intOldValue, 0);
            config.setOrder(intOldValue);

        }

        if (change.getDiffs().containsKey("status")) {
            intOldValue = (Integer) change.getDiffs().get("status").get("oldValue");
            intOldValue = ObjectUtils.defaultIfNull(intOldValue, 0);
            config.setStatus(intOldValue);
        }

        updateMockConfiguration(config);
        return change;


    }

    private void setValueMethodInvoke(Method method, Object newInstance, Object newValue) throws
                                                                                          InvocationTargetException,
                                                                                          IllegalAccessException {
        method.invoke(newInstance, newValue);
    }
}
