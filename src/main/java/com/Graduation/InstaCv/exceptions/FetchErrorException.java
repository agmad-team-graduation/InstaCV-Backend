package com.Graduation.InstaCv.exceptions;

public class FetchErrorException extends RuntimeException {
    public FetchErrorException(String message) {
        super(message);
    }

    public FetchErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
