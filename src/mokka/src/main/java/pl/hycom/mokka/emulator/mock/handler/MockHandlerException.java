package pl.hycom.mokka.emulator.mock.handler;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
public class MockHandlerException extends Exception {

	private static final long serialVersionUID = 8677968963852075467L;

	public MockHandlerException() {
		super();
	}

	public MockHandlerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MockHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

	public MockHandlerException(String message) {
		super(message);
	}

	public MockHandlerException(Throwable cause) {
		super(cause);
	}

}
