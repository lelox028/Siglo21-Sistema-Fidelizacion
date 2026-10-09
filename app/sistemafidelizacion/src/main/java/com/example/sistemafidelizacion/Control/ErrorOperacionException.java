package com.example.sistemafidelizacion.Control;

public class ErrorOperacionException extends Exception {
    public ErrorOperacionException(String message) {
        super(message);
    }

    public ErrorOperacionException(String message, Throwable cause) {
        super(message, cause);
    }
}
