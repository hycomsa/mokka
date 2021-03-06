package pl.hycom.mokka.emulator.logs;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.hycom.mokka.emulator.logs.model.Log;
import pl.hycom.mokka.emulator.logs.model.Log.LogBuilder;
import pl.hycom.mokka.util.query.Q;
import pl.hycom.mokka.util.query.QManager;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Slf4j
@Component
public class LogManager {

	@Autowired
	private LogRepository repository;

	@Autowired
	private QManager qManager;

	private static final int NUMBER_OF_RESULTS_PER_QUERY = 5;

	private static final String[] dateFormats = new String[] { "yyyy-MM-dd'T'HH:mm:ss.S'Z'" , "yyyy-MM-dd HH:mm"};

	private AtomicReference<Set<Log>> logCache = new AtomicReference<>();

	@Value("${create.logs.enabled}")
	private boolean createLogsEnabled;

	@Scheduled(fixedDelay = 5 * 60 * 1000)
	public void reportCurrentTime() {
		updateLogCache();
	}

	@Async
	protected void updateLogCache() {
		logCache.set(repository.findUniqueLogs());
		log.debug("logCache updated and contains {} items.", logCache.get().size());
	}

	@Transactional
	public void saveLog(LogBuilder logBuilder) {
		log.debug("CreateLogsEnabled flag has been set to[{}]",createLogsEnabled);
		if(createLogsEnabled) {
			repository.save(logBuilder.build());
		}
	}

	public List<Log> getLogs(HttpServletRequest req) {

		boolean descSort = StringUtils.equalsIgnoreCase(req.getParameter("fetchMethod"), "old");

		Q q = Q.select("l").from("LogsMocks l").orderby("l.date " + (descSort ? "DESC" : "ASC")).maxResults(NUMBER_OF_RESULTS_PER_QUERY);

		Long lastId = StringUtils.isNumeric(req.getParameter("lastId")) ? Long.parseLong(req.getParameter("lastId")) : null;
		createLog(req, descSort, q, lastId);

		return qManager.execute(q, Log.class);
	}

	private void createLog(HttpServletRequest req, boolean descSort, Q q, Long lastId) {
		if (lastId != null) {
			if (descSort) {
				q.and(Q.lt("l.id", lastId));
			} else {
				q.and(Q.gt("l.id", lastId));
			}
		}

		// path
		if (StringUtils.isNotBlank(req.getParameter("path"))) {
			q.and(Q.like("l.uri", req.getParameter("path")));
		}

		// name
		if (StringUtils.isNotBlank(req.getParameter("name"))) {
			q.and(Q.like("l.name", "%" + req.getParameter("name") + "%"));
		}

		// from
		if (StringUtils.isNotBlank(req.getParameter("from"))) {
			q.and(Q.like("l.from", req.getParameter("from")));
		}

		// text
		if (StringUtils.isNotBlank(req.getParameter("text"))) {
			q.and(Q.ors(Q.like("l.request", req.getParameter("text")), Q.like("l.response", req.getParameter("text"))));
		}

		// dateFrom
		if (StringUtils.isNotBlank(req.getParameter("dateFrom"))) {
			try {
				q.and(Q.after("l.date", DateUtils.parseDate(req.getParameter("dateFrom"), dateFormats)));
			} catch (Exception e) {
				log.warn("Error parsing date", e);
			}
		}

		// dateTo
		if (StringUtils.isNotBlank(req.getParameter("dateTo"))) {
			try {
				q.and(Q.before("l.date", DateUtils.parseDate(req.getParameter("dateTo"), dateFormats)));
			} catch (Exception e) {
				log.warn("Error parsing date", e);
			}
		}
	}

	public Log getLog(long id) {
		return repository.findById(id).orElse(null);
	}

	public boolean removeLog(Long id) {
		try {
			repository.deleteById(id);
			log.info("Log (id: " + id + ") deleted");
			return true;
		} catch (Exception e) {
			log.error("Log (id: " + id + ") could not be deleted", e);
		}

		return false;
	}

	public void deleteAllLogs() {
		repository.deleteAll();
		log.info("All logs removed");
	}

}
