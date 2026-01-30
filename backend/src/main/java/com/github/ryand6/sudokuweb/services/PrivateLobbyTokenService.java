package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.dto.TokenIdentifiers;
import com.github.ryand6.sudokuweb.dto.response.UserActiveTokensDto;
import com.github.ryand6.sudokuweb.exceptions.InvalidTokenException;
import com.github.ryand6.sudokuweb.exceptions.MaxActiveTokenException;
import com.github.ryand6.sudokuweb.exceptions.TokenNotFoundException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class PrivateLobbyTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final SecureInvitationsService secureInvitationsService;

    private final int MAX_TOKENS = 3;

    public PrivateLobbyTokenService(RedisTemplate<String, String> redisTemplate,
                                    SecureInvitationsService secureInvitationsService) {
        this.redisTemplate = redisTemplate;
        this.secureInvitationsService = secureInvitationsService;
    }

    // Create a new token and store in Redis DB against the user creating the token if they haven't reached their max active tokens created - track newly created token
    public String generateToken(Long lobbyId, Long userId) {
        // Create a key for tracking a users generated tokens so that the amount of active private tokens they can generate is limited
        String userKey = "user:" + userId + ":tokens";
        cleanUpUserTokens(userKey);
        Long countActiveTokens = getActiveTokensCountCreatedByUser(userKey);
        if (countActiveTokens != null && countActiveTokens >= MAX_TOKENS) {
            throw new MaxActiveTokenException(String.format("Maximum number of active private tokens reached (%d)", MAX_TOKENS));
        }
        String token = secureInvitationsService.createInvitationToken(lobbyId, userId);
        // Set the value of the key value pair to be the lobby ID so that tokens are associated with a lobby in Redis
        String value = "lobby-" + lobbyId.toString();
        // Add token to Redis DB with an expiry of 10 minutes
        redisTemplate.opsForValue().set(token, value, Duration.ofMinutes(10));
        trackNewUserCreatedToken(userKey, token);
        return token;
    }

    public Long joinPrivateLobbyWithToken(String token) {
        // Retrieve the lobby identifier associated with the token if exists then delete, ensuring tokens are one-time use
        String tokenLobby = redisTemplate.opsForValue().getAndDelete(token);
        if (tokenLobby != null) {
            // Additional validation check to ensure token is valid by checking token's signature matches HMAC hash
            TokenIdentifiers tokenIdentifiers = secureInvitationsService.validateInvitationToken(token);
            if (tokenIdentifiers == null) {
                throw new InvalidTokenException("Invalid token provided");
            }
            String userKey = "user:" + tokenIdentifiers.getUserId() + ":tokens";
            // Stop tracking the token now it's been used
            redisTemplate.opsForSet().remove(userKey, token);
            // Get the lobbyId from the token Lobby value stored
            Long tokenLobbyId = Long.valueOf(tokenLobby.substring("lobby-".length()));
            Long tokenIdentifiersLobbyId = tokenIdentifiers.getLobbyId();
            // Additional check to ensure that the lobby ID stored within the token matches the lobby ID stored in Redis associated with the token
            if (!tokenLobbyId.equals(tokenIdentifiersLobbyId)) {
                throw new InvalidTokenException("Lobby ID associated with token in Redis doesn't match lobby ID stored in the token");
            }
            return tokenIdentifiersLobbyId;
        } else {
            throw new TokenNotFoundException("Token provided does not exist, it may have expired");
        }
    }

    // Get list of all active tokens for a user
    public UserActiveTokensDto  getActiveTokensForUser(Long userId) {
        String userKey = "user:" + userId + ":tokens";
        cleanUpUserTokens(userKey); // remove expired tokens

        Set<String> tokens = redisTemplate.opsForSet().members(userKey);
        List<UserActiveTokensDto.TokenWithExpiry> activeTokens = new ArrayList<>();
        if (tokens != null) {
            Long now = System.currentTimeMillis();
            for (String token : tokens) {
                // Calculate time at which the token will expire
                Long ttlMs = redisTemplate.getExpire(token, TimeUnit.MILLISECONDS);
                if (ttlMs != null && ttlMs > 0) {
                    long expiresAt = now + ttlMs;
                    activeTokens.add(new UserActiveTokensDto.TokenWithExpiry(token, expiresAt));
                }
            }
        }
        return UserActiveTokensDto.builder()
                .activeTokens(activeTokens)
                .build();
    }

    // Adds a newly created token to the set of active tokens for that user that created
    private void trackNewUserCreatedToken(String userKey, String token) {
        redisTemplate.opsForSet().add(userKey, token);
    }

    // Ensure the number of active tokens for a user is accurate
    void cleanUpUserTokens(String userKey) {
        Set<String> tokens = redisTemplate.opsForSet().members(userKey);
        if (tokens == null) return;

        for (String token : tokens) {
            Boolean exists = redisTemplate.hasKey(token);
            if (exists == null || !exists) {
                redisTemplate.opsForSet().remove(userKey, token); // token expired
            }
        }
    }

    // Returns the number of current active tokens a user has created, used to prevent the user from abusing creating tokens
    private Long getActiveTokensCountCreatedByUser(String userKey) {
        return redisTemplate.opsForSet().size(userKey);
    }

}
