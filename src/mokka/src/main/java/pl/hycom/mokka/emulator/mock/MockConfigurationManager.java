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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.regex.Pattern;

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

	@Autowired
	private QManager qManager;

	@Autowired
	private MockSearch mockSearch;

	@Autowired
	private MockConfigurationRepository repository;

	@Autowired
	private UserRepository userRepository;

	private AtomicReference<Set<String>> pathCache = new AtomicReference<>();

	@Scheduled(fixedDelay = 5 * 60 * 1000)
	public void reportCurrentTime() {
		updatePathcache();
	}

	@Async
	private void updatePathcache() {
		pathCache.set(repository.findUniquePaths());
		log.debug("pathCache updated and contains " + pathCache.get().size() + " items");
	}

	@Transactional
	public MockConfiguration saveOrUpdateMockConfiguration(MockConfiguration mock) {
		if (mock == null) {
			return mock;
		}

		mock.setUpdated(new Date(System.currentTimeMillis()));

		MockConfiguration m = repository.save(mock);
		log.info(MOCK_ID + mock.getId() + ") added or updated");

		updatePathcache();

		return m;
	}

	@Transactional
	@Synchronized
	public boolean removeMockConfiguration(Long id) {
		try {
			repository.delete(id);
			log.info(MOCK_ID + id + ") deleted");
			return true;
		} catch (Exception e) {
			log.error(MOCK_ID + id + ") could not be deleted", e);
		}

		updatePathcache();

		return repository.findOne(id) == null;
	}

	public MockConfiguration findFirstAvailableMockConfiguration(String path, String requestBody) {
		Set<String> paths = new HashSet<>();
		paths.add(path);
		for (String p : pathCache.get()) {
			if (Pattern.matches(p, path)) {
				paths.add(p);
			}
		}

		Q q = Q.select("m").from("MockConfig m").where(Q.in("path", paths)).and(Q.eq("m.enabled", true)).orderby("m.order DESC");

		List<MockConfiguration> result = qManager.execute(q, MockConfiguration.class);

		for (MockConfiguration mc : result) {
			if (StringUtils.isBlank(mc.getPattern())) {
				return mc;
			}

			Pattern pattern = Pattern.compile(mc.getPattern(), Pattern.DOTALL);
			if (pattern.matcher(requestBody).matches()) {
				return mc;
			}
		}

		return null;
	}

	@Synchronized
	@Transactional
	public boolean setEnabled(Long id, boolean enabled) {
		if (id == null) {
			return false;
		}

		MockConfiguration mock = repository.findOne(id);
		if (mock == null) {
			return false;
		}

		mock.setEnabled(enabled);
		saveOrUpdateMockConfiguration(mock);

		return true;
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
		return repository.findOne(id);
	}

	public List<MockConfiguration> getMockConfigurations(HttpServletRequest req) {
		if (StringUtils.isNumeric(req.getParameter("show"))) {
			MockConfiguration mc = repository.findOne(Long.parseLong(req.getParameter("show")));
			return mc == null ? Collections.emptyList() : ImmutableList.of(mc);
		}

		if (StringUtils.isNumeric(req.getParameter("from"))) {
			mockSearch.setStartingIndex(Integer.parseInt(req.getParameter("from")));
		}

		// start perPage & startFrom
		if (StringUtils.isNumeric(req.getParameter("perPage"))) {
			mockSearch.setMaxResults(Integer.parseInt(req.getParameter("perPage")));
		} else {
			mockSearch.setMaxResults(numberOfResultsPerQuery);
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

		return mockSearch.find();
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

			c.setDate(r.getRevisionDate());

			c.getDiffs().putAll(getChangesForObject("", mc, r.getEntity(), MockConfiguration.class, dmp));

			if (!c.getDiffs().isEmpty()) {
				changes.add(c);
			}

			mc = r.getEntity();
		}

		Collections.reverse(changes);

		return changes;
	}

	private Map<String, Map<String, Object>> getChangesForObject(String fieldPrefix, Object o1, Object o2, Class<?> clazz, DiffMatchPatch dmp) {
		Map<String, Map<String, Object>> result = new HashMap<>();

		for (Field f : clazz.getDeclaredFields()) {
			if (f.isAnnotationPresent(TrackChanges.class)) {
				getObjectChangesForTrackChanges(fieldPrefix, o1, o2, dmp, result, f);
			}
		}

		return result;
	}

	private void getObjectChangesForTrackChanges(String fieldPrefix, Object o1, Object o2, DiffMatchPatch dmp, Map<String, Map<String, Object>> result, Field f) {
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

	private void putPrimitiveChangeToResult(String fieldPrefix, DiffMatchPatch dmp, Map<String, Map<String, Object>> result, Field f, Object o1Value, Object o2Value) {
		Map<String, Object> changeValue = new HashMap<>();
		if ((o1Value == null && o2Value != null) || (o2Value == null && o1Value != null)) {
            changeValue.put(NEW_VALUE, o2Value);
            changeValue.put(OLD_VALUE, o1Value);

        } else if (o1Value != null && !o1Value.equals(o2Value)) {
            if (f.getAnnotation(Lob.class) != null) {
                changeValue.put("diff", dmp.patchToText(dmp.patchMake((String) o1Value, (String) o2Value)));
            } else {
                changeValue.put(NEW_VALUE, o2Value);
                changeValue.put(OLD_VALUE, o1Value);
            }
        }

		if (!changeValue.isEmpty()) {
            result.put(fieldPrefix + f.getName(), changeValue);
        }
	}

	private void putObjectChangesToResult(String fieldPrefix, DiffMatchPatch dmp, Map<String, Map<String, Object>> result, Field f, Object o1Value, Object o2Value) {
		Class<?> fieldType = f.getType();
		if (o1Value != null && o2Value != null) {
            fieldType = o2Value.getClass();
            if (o1Value.getClass() != o2Value.getClass()) {
                Map<String, Object> changeValue = new HashMap<>();
                changeValue.put(NEW_VALUE, o2Value.getClass()
                        .getSimpleName());
                changeValue.put(OLD_VALUE, o1Value.getClass()
                        .getSimpleName());
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

		if (StringUtils.isBlank(mc.getPath())) {
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
}
