package com.example.booking.exception;

public class HotelServiceException extends Exception {

    public HotelServiceException() { super(); }

    public HotelServiceException(String message) { super(message); }

    public HotelServiceException(String message, Throwable cause) { super(message, cause); }

    public HotelServiceException(Throwable cause) { super(cause); }

    public HotelServiceException(String message, Throwable cause, boolean enableSuppression,
                                 boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
