package com.bariscemant.verimor;

/** Raised for every non-2xx HTTP response. */
public final class VerimorApiException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final int statusCode;
    private final ErrorBodyKind bodyKind;
    private final String body;

    public VerimorApiException(int statusCode, ErrorBodyKind bodyKind, String body, String message) {
        super(message);
        this.statusCode = statusCode;
        this.bodyKind = bodyKind;
        this.body = body;
    }

    public int statusCode() {
        return statusCode;
    }

    public ErrorBodyKind bodyKind() {
        return bodyKind;
    }

    /** The response body as text; empty for binary bodies. */
    public String body() {
        return body;
    }
}
