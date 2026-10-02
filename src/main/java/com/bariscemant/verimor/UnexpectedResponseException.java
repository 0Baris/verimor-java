package com.bariscemant.verimor;

/** Raised when a 2xx response does not have the expected shape. */
public final class UnexpectedResponseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnexpectedResponseException(String message) {
        super(message);
    }

    public UnexpectedResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
