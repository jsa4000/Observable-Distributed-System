package com.example.booking.exception;

public class HotelNotFoundException extends Exception {

    public HotelNotFoundException() { super(); }

    public HotelNotFoundException(String message) { super(message); }

    public HotelNotFoundException(String message, Throwable cause) { super(message, cause); }

    public HotelNotFoundException(Throwable cause) { super(cause); }

    public HotelNotFoundException(String message, Throwable cause, boolean enableSuppression,
                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
