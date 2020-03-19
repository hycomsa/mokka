package pl.hycom.mokka.emulator.mock;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.hycom.mokka.emulator.mock.model.Change;
import pl.hycom.mokka.emulator.mock.model.MockConfiguration;
import pl.hycom.mokka.emulator.mock.model.WrappedMockConfiguration;
import pl.hycom.mokka.web.json.View;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

	@Autowired
	private MockConfigurationRepository mockConfigurationRepository;

	@PreAuthorize("hasRole('ROLE_USER')")
	@JsonView(View.General.class)
	@GetMapping(value = "/configuration")
	public WrappedMockConfiguration getMocks(HttpServletRequest request) {
		return mockConfigurationManager.getMockConfigurations(request);
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@JsonView(View.Detailed.class)
	@GetMapping(value = "/configuration/{id}")
	public MockConfiguration getMock(@PathVariable("id") long id) {
		return mockConfigurationManager.getMockConfiguration(id);
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
	@PutMapping(value = "/configuration")
	public Object saveMock(@RequestBody MockConfiguration mock, HttpServletRequest req) {
		Map<String, String> errors = mockConfigurationManager.validate(mock);
		if (!errors.isEmpty()) {
			return ImmutableMap.of("errors", errors);
		}

		return mock.getId() == null ? mockConfigurationManager.createMockConfiguration(mock) : mockConfigurationManager.updateMockConfiguration(mock);
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
	@DeleteMapping(value = "/configuration/{id}")
	public boolean removeMock(@PathVariable("id") long id) {
		return mockConfigurationManager.removeMockConfiguration(id);
	}

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    @PostMapping(value = "/configuration/{id}/enable")
    public boolean setEnabled(@PathVariable("id") long id) {
        return mockConfigurationManager.enable(id);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    @PostMapping(value = "/configuration/{id}/disable")
    public boolean setDisabled(@PathVariable("id") long id) {
        return mockConfigurationManager.disable(id);
    }

	@PreAuthorize("hasAnyRole('ROLE_USER')")
	@GetMapping(value = "/configuration/{id}/changes")
	public List<Change> getChanges(@PathVariable("id") long id) {
		return mockConfigurationManager.getChanges(id);
	}

	@PreAuthorize("hasAnyRole('ROLE_USER')")
	@GetMapping(value = "/configuration/{configId}/restore/{changeId}")
	public Change restoreChange(@PathVariable("configId") long configId, @PathVariable("changeId") long changeId) {
		return mockConfigurationManager.restoreChange(configId, changeId);
	}

	@PreAuthorize("hasAnyRole('ROLE_USER')")
	@PostMapping(value = "/configuration/statuses")
	public Map<Integer, String> getStatuses() {
		return Arrays.stream(HttpStatus.values()).distinct().collect(Collectors.toMap(HttpStatus::value, HttpStatus::getReasonPhrase, (v1, v2) -> v1));
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@JsonView(View.Detailed.class)
	@GetMapping(value = "/configuration/paths")
	public Set<String> getPaths() {
		return mockConfigurationRepository.findUniquePaths();
	}
}
