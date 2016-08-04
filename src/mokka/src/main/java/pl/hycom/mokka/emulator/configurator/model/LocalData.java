package pl.hycom.mokka.emulator.configurator.model;

import org.springframework.stereotype.Component;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Component
public class LocalData {

	private int timeout;

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}
