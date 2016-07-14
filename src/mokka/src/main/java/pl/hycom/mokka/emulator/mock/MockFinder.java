package pl.hycom.mokka.emulator.mock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.hycom.mokka.emulator.mock.model.MockConfiguration;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Slf4j
@Component
public class MockFinder {

	@Autowired
	private MockConfigurationManager mockConfigurationManager;

	public MockConfiguration findMock(MockContext ctx) {
		return mockConfigurationManager.findFirstAvailableMockConfiguration(ctx.getUri(), ctx.getRequestBody());
	}

}
