package pl.hycom.api.exception;

import pl.hycom.api.MockManager;

/**
 * Base exception fir mock managing. May be thrown when errors occur on {@link MockManager} calls
 *
 * @author Michal Adamczyk, HYCOM S.A.
 */
public class MockManagingException extends RuntimeException{

    /**
     * default constructor
     */
    public MockManagingException() {
    }

    /**
     * constructor with message, matching super
     * @param message message
     */
    public MockManagingException(String message) {
        super(message);
    }

    /**
     * constructor with message and cause, matching super
     *
     * @param message message
     * @param cause   cause
     */
    public MockManagingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * constructor with cause, matching super
     *
     * @param cause cause
     */
    public MockManagingException(Throwable cause) {
        super(cause);
    }

    /**
     * constructor with all available fields, matching super
     *
     * @param message            message
     * @param cause              cause
     * @param enableSuppression  enable suppression flag
     * @param writableStackTrace writable stack trace flag
     */
    public MockManagingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
