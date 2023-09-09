package com.epam.jmp.nosql.elasticsearch.exception;

public class NotExpectedResultException extends RuntimeException {

    public NotExpectedResultException(String message) {
        super(message);
    }

    public NotExpectedResultException(String message, Throwable cause) {
        super(message, cause);
    }
}
