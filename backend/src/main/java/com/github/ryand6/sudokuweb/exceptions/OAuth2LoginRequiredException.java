package com.github.ryand6.sudokuweb.exceptions;

public class OAuth2LoginRequiredException extends RuntimeException {
    public OAuth2LoginRequiredException(String message) {
        super(message);
    }
}
