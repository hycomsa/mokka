package pl.hycom.mokka.stubbing;

/**
 * @author adam.misterski@hycom.pl
 */
public class MalformedMockConfigurationException extends RuntimeException {

    public MalformedMockConfigurationException() {
        super();
    }

    public MalformedMockConfigurationException(String message) {
        super(message);
    }

    public MalformedMockConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MalformedMockConfigurationException(Throwable cause) {
        super(cause);
    }

    protected MalformedMockConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
