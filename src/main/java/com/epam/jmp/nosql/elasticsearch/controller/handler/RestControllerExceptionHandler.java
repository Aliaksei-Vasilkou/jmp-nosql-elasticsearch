package com.epam.jmp.nosql.elasticsearch.controller.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.epam.jmp.nosql.elasticsearch.exception.EmployeeNotFoundException;
import com.epam.jmp.nosql.elasticsearch.exception.NotExpectedResultException;
import com.epam.jmp.nosql.elasticsearch.model.ErrorResponse;

@ControllerAdvice
public class RestControllerExceptionHandler {

    @ExceptionHandler(value = EmployeeNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleEmployeeNotFoundException(EmployeeNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse("NOT_FOUND", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = NotExpectedResultException.class)
    protected ResponseEntity<ErrorResponse> handleNotExpectedResultException(NotExpectedResultException ex) {
        return new ResponseEntity<>(new ErrorResponse("NOT_EXPECTED_RESULT", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

     @ExceptionHandler(value = IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(new ErrorResponse("BAD_REQUEST", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

     @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception ex) {
        return new ResponseEntity<>(new ErrorResponse("UNKNOWN_ERROR", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
