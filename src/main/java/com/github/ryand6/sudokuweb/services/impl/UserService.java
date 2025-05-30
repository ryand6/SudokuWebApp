package com.github.ryand6.sudokuweb.services.impl;

import com.github.ryand6.sudokuweb.domain.ScoreEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.exceptions.UsernameTakenException;
import com.github.ryand6.sudokuweb.repositories.UserRepository;
import com.github.ryand6.sudokuweb.util.OAuthUtil;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Try retrieve User's OAuth provider name and ID
    public Optional<UserEntity> tryGetCurrentUser(OAuth2User principal, OAuth2AuthenticationToken authToken) {
        String provider = OAuthUtil.retrieveOAuthProviderName(authToken);
        String providerId = OAuthUtil.retrieveOAuthProviderId(provider, principal);
        return userRepository.findByProviderAndProviderId(provider, providerId);
    }

    // Create user when they log into site for the first time
    public UserEntity createNewUser(String username, String provider, String providerId) {
        //Check if username already exists in DB
        if (userRepository.existsByUsername(username)) {
            throw new UsernameTakenException("Username provided is taken, please choose another");
        }

        // Create score entity first so that it can be persisted when User entity is persisted
        ScoreEntity score = new ScoreEntity();
        score.setTotalScore(0);
        score.setGamesPlayed(0);
        // Persist the user entity to DB
        UserEntity newUser = new UserEntity();
        newUser.setUsername(username);
        newUser.setProvider(provider);
        newUser.setProviderId(providerId);
        newUser.setIsOnline(true);
        newUser.setScoreEntity(score);
        return userRepository.save(newUser);
    }

}
