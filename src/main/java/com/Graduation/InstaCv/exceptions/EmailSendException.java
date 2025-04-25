package com.Graduation.InstaCv.exceptions;

public class EmailSendException extends RuntimeException {
    public EmailSendException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
