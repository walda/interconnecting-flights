package com.ryanair.exception;

public class InvalidDateException extends RuntimeException {

    private String message;

    public InvalidDateException(String msg) {
        message = msg;
    }

    @Override
    public String toString() {
        return message;
    }
}
