package com.loya.devi.exception;

public class ViolationException extends RuntimeException {
    public ViolationException() {
        super();
    }

    public ViolationException(String s) {
        super(s);
    }

    public ViolationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ViolationException(Throwable throwable) {
        super(throwable);
    }
}
