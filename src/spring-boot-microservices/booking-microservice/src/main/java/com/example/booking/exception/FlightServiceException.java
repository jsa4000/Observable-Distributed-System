package com.example.booking.exception;

public class FlightServiceException extends Exception {

    public FlightServiceException() { super(); }

    public FlightServiceException(String message) { super(message); }

    public FlightServiceException(String message, Throwable cause) { super(message, cause); }

    public FlightServiceException(Throwable cause) { super(cause); }

    public FlightServiceException(String message, Throwable cause, boolean enableSuppression,
                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
