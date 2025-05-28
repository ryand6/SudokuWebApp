package com.github.ryand6.sudokuweb.services.impl;

import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.repositories.UserRepository;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticateUserService {

    private final UserRepository userRepository;

    public AuthenticateUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserEntity> tryGetCurrentUser(OAuth2User principal, OAuth2AuthenticationToken authToken) {
        String provider = authToken.getAuthorizedClientRegistrationId();
        String providerId = switch (provider) {
            case "google" -> principal.getName();
            case "github", "facebook" -> principal.getAttribute("id").toString();
            default -> throw new IllegalStateException("Unknown provider: " + provider);
        };
        return userRepository.findByProviderAndProviderId(provider, providerId);
    }

}
