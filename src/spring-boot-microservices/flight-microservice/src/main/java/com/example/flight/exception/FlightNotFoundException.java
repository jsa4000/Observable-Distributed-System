package com.example.flight.exception;

public class FlightNotFoundException extends Exception {

    public FlightNotFoundException() { super(); }

    public FlightNotFoundException(String message) { super(message); }

    public FlightNotFoundException(String message, Throwable cause) { super(message, cause); }

    public FlightNotFoundException(Throwable cause) { super(cause); }

    public FlightNotFoundException(String message, Throwable cause, boolean enableSuppression,
                                   boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
