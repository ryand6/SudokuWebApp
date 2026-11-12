package com.github.ryand6.sudokuweb.exceptions.auth;

public class TooManyOtpRequestsException extends RuntimeException {
    public TooManyOtpRequestsException(String message) {
        super(message);
    }
}
