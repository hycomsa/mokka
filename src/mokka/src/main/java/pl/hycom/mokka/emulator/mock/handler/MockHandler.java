package pl.hycom.mokka.emulator.mock.handler;

import pl.hycom.mokka.emulator.mock.MockContext;
import pl.hycom.mokka.emulator.mock.model.MockConfiguration;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
public interface MockHandler {

	boolean canHandle(MockConfiguration mc, MockContext ctx);

	void handle(MockConfiguration mockConfiguration, MockContext ctx) throws pl.hycom.mokka.emulator.mock.handler.MockHandlerException;
}
