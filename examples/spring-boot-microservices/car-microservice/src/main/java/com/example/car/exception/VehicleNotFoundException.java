package com.example.car.exception;

public class VehicleNotFoundException extends Exception {

    public VehicleNotFoundException() { super(); }

    public VehicleNotFoundException(String message) { super(message); }

    public VehicleNotFoundException(String message, Throwable cause) { super(message, cause); }

    public VehicleNotFoundException(Throwable cause) { super(cause); }

    public VehicleNotFoundException(String message, Throwable cause, boolean enableSuppression,
                                    boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
