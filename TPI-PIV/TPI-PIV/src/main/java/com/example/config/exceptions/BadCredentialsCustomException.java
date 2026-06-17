package com.example.config.exceptions;

public class BadCredentialsCustomException extends RuntimeException {
    public BadCredentialsCustomException(String message) {
        super(message);
   }

}
