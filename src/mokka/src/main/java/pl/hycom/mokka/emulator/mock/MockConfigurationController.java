package pl.hycom.mokka.emulator.mock;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.hycom.mokka.emulator.mock.model.Change;
import pl.hycom.mokka.emulator.mock.model.MockConfiguration;
import pl.hycom.mokka.web.json.View;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Slf4j
@RestController
@RequestMapping(headers = "x-requested-with=XMLHttpRequest")
public class MockConfigurationController {

	@Autowired
	private MockConfigurationManager mockConfigurationManager;

	@PreAuthorize("hasRole('ROLE_USER')")
	@JsonView(View.General.class)
	@RequestMapping(value = "/configuration", method = RequestMethod.GET)
	public List<MockConfiguration> getMocks(HttpServletRequest request) {
		return mockConfigurationManager.getMockConfigurations(request);
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@JsonView(View.Detailed.class)
	@RequestMapping(value = "/configuration/{id}", method = RequestMethod.GET)
	public MockConfiguration getMock(@PathVariable("id") long id) {
		return mockConfigurationManager.getMockConfiguration(id);
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
	@RequestMapping(value = "/configuration", method = RequestMethod.PUT)
	public Object saveMock(@RequestBody MockConfiguration mock, HttpServletRequest req) {

		Map<String, String> errors = mockConfigurationManager.validate(mock);
		if (!errors.isEmpty()) {
			return ImmutableMap.of("errors", errors);
		}

		return mockConfigurationManager.saveOrUpdateMockConfiguration(mock);
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
	@RequestMapping(value = "/configuration/{id}", method = RequestMethod.DELETE)
	public boolean removeMock(@PathVariable("id") long id) {
		return mockConfigurationManager.removeMockConfiguration(id);
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
	@RequestMapping(value = "/configuration/{id}/{enable}", method = RequestMethod.POST)
	public boolean setEnabled(@PathVariable("id") long id, @PathVariable("enable") String enable) {
		return mockConfigurationManager.setEnabled(id, StringUtils.equalsIgnoreCase(enable, "enable"));
	}

	@PreAuthorize("hasAnyRole('ROLE_USER')")
	@RequestMapping(value = "/configuration/{id}/changes", method = RequestMethod.GET)
	public List<Change> getChanges(@PathVariable("id") long id) {
		return mockConfigurationManager.getChanges(id);
	}

	@PreAuthorize("hasAnyRole('ROLE_USER')")
	@RequestMapping(value = "/configuration/statuses", method = RequestMethod.POST)
	public List<Integer> getStatuses() {
		return Arrays.stream(HttpStatus.values()).map(v -> v.value()).distinct().collect(Collectors.toList());
	}

}
