package com.sporty.ticketsapi.exceptions;

public class InvalidVisibilityException extends RuntimeException{
    public InvalidVisibilityException(String message) {
        super(message);
    }
}
