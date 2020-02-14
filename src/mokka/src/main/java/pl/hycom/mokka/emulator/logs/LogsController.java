package pl.hycom.mokka.emulator.logs;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.hycom.mokka.emulator.logs.model.Log;
import pl.hycom.mokka.web.json.View;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Slf4j
@RestController
@RequestMapping(headers = "x-requested-with=XMLHttpRequest")
public class LogsController {

	@Autowired
	private LogManager logManager;

	@Autowired
	private LogRepository logRepository;

	@PreAuthorize("hasRole('ROLE_USER')")
	@JsonView(View.General.class)
	@RequestMapping(value = "/logs", method = RequestMethod.GET)
	public List<Log> getLogs(HttpServletRequest request) {
		return logManager.getLogs(request);
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@JsonView(View.General.class)
	@RequestMapping(value = "/logs/uris", method = RequestMethod.GET)
	public Set<String> getUris() {
		return logRepository.findUniqueLogs().stream().map(Log::getUri).collect(Collectors.toSet());
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@JsonView(View.Detailed.class)
	@RequestMapping(value = "/logs/{id}", method = RequestMethod.GET)
	public Log getExchanges(@PathVariable("id") long id) {
		return logManager.getLog(id);
	}

}
