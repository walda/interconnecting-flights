package com.ryanair.controller;

import com.ryanair.exception.InvalidDateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class InterconnectionsControllerAdvice {

    @ExceptionHandler({ InvalidDateException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> invalidDate(Exception exp) {
        return new ResponseEntity<>(exp.toString(), HttpStatus.BAD_REQUEST);
    }
}
