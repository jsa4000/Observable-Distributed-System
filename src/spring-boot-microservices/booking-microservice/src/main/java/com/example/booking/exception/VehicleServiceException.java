package com.example.booking.exception;

public class VehicleServiceException extends Exception {

    public VehicleServiceException() { super(); }

    public VehicleServiceException(String message) { super(message); }

    public VehicleServiceException(String message, Throwable cause) { super(message, cause); }

    public VehicleServiceException(Throwable cause) { super(cause); }

    public VehicleServiceException(String message, Throwable cause, boolean enableSuppression,
                                   boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
