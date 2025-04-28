package com.Graduation.InstaCv.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidRegistrationDataException extends BaseException {
    public InvalidRegistrationDataException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}