package com.github.ryand6.sudokuweb.exceptions.auth;

public class OAuthProviderNotLinkedException extends RuntimeException {
    public OAuthProviderNotLinkedException(String message) {
        super(message);
    }
}
