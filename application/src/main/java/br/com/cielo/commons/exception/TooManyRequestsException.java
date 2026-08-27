package br.com.cielo.commons.exception;

public class TooManyRequestsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TooManyRequestsException() {
        super("Too Many Requests");
    }

    public TooManyRequestsException(String message) {
        super(message);
    }
}
