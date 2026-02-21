package com.github.ryand6.sudokuweb.exceptions.auth;

public class OAuth2LoginRequiredException extends RuntimeException {
    public OAuth2LoginRequiredException(String message) {
        super(message);
    }
}
