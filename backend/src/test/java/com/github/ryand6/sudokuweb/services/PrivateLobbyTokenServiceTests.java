package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.dto.TokenIdentifiers;
import com.github.ryand6.sudokuweb.dto.response.UserActiveTokensDto;
import com.github.ryand6.sudokuweb.exceptions.lobby.token.InvalidTokenException;
import com.github.ryand6.sudokuweb.exceptions.lobby.token.MaxActiveTokenException;
import com.github.ryand6.sudokuweb.exceptions.lobby.token.TokenNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PrivateLobbyTokenServiceTests {

    @Mock
    private RedisTemplate redisTemplate;

    @Mock
    private SetOperations<String, String> setOperations;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private SecureInvitationsService secureInvitationsService;

    @Spy
    @InjectMocks
    private PrivateLobbyTokenService privateLobbyTokenService;

    private static final int MAX_TOKENS = 3;

    @Test
    void cleanUpUserTokens_callsRemoveWhenTokenNoLongerExists() {
        String token1 = "test1";
        String token2 = "test2";

        Set<String> tokens = Set.of(token1, token2);

        when(redisTemplate.opsForSet()).thenReturn(setOperations);

        when(setOperations.members(any(String.class))).thenReturn(tokens);

        when(redisTemplate.hasKey(token1)).thenReturn(false);

        when(redisTemplate.hasKey(token2)).thenReturn(true);

        when(setOperations.remove(any(String.class), any(String.class))).thenReturn(null);

        privateLobbyTokenService.cleanUpUserTokens("key");

        verify(setOperations, times(1)).remove("key", token1);
    }

    @Test
    void getActiveTokensForUser_includesOnlyActiveTokens() {
        Long userId = 42L;
        String userKey = "user:" + userId + ":tokens";

        String token1 = "token1";
        String token2 = "token2"; // expired
        Set<String> tokens = Set.of(token1, token2);

        when(redisTemplate.opsForSet()).thenReturn(setOperations);

        when(setOperations.members(userKey)).thenReturn(tokens);
        when(redisTemplate.getExpire(token1, TimeUnit.MILLISECONDS)).thenReturn(1000L); // active
        when(redisTemplate.getExpire(token2, TimeUnit.MILLISECONDS)).thenReturn(-1L);   // expired

        doNothing().when(privateLobbyTokenService).cleanUpUserTokens(userKey);

        UserActiveTokensDto result = privateLobbyTokenService.getActiveTokensForUser(userId);

        List<UserActiveTokensDto.TokenWithExpiry> activeTokens = result.getActiveTokens();
        assertThat(activeTokens).hasSize(1);

        UserActiveTokensDto.TokenWithExpiry tokenWithExpiry = activeTokens.get(0);
        assertThat(tokenWithExpiry.getToken()).isEqualTo(token1);

        long now = System.currentTimeMillis();
        assertThat(tokenWithExpiry.getExpiresAt()).isGreaterThanOrEqualTo(now);
        assertThat(tokenWithExpiry.getExpiresAt()).isLessThanOrEqualTo(now + 2000);

        // Verify cleanup and Redis interactions
        verify(privateLobbyTokenService).cleanUpUserTokens(userKey);
        verify(setOperations).members(userKey);
        verify(redisTemplate).getExpire(token1, TimeUnit.MILLISECONDS);
        verify(redisTemplate).getExpire(token2, TimeUnit.MILLISECONDS);
    }

    @Test
    void getActiveTokensForUser_emptyTokenSet_returnsEmptyList() {
        Long userId = 99L;
        String userKey = "user:" + userId + ":tokens";

        when(redisTemplate.opsForSet()).thenReturn(setOperations);

        when(setOperations.members(userKey)).thenReturn(Set.of());
        doNothing().when(privateLobbyTokenService).cleanUpUserTokens(userKey);

        UserActiveTokensDto result = privateLobbyTokenService.getActiveTokensForUser(userId);

        assertThat(result.getActiveTokens()).isEmpty();

        verify(privateLobbyTokenService).cleanUpUserTokens(userKey);
        verify(setOperations).members(userKey);
        verify(redisTemplate, never()).getExpire(any(String.class), any());
    }

    @Test
    void getActiveTokensForUser_allTokensExpired_returnsEmptyList() {
        Long userId = 50L;
        String userKey = "user:" + userId + ":tokens";

        String token1 = "t1";
        String token2 = "t2";
        Set<String> tokens = Set.of(token1, token2);

        when(redisTemplate.opsForSet()).thenReturn(setOperations);

        when(setOperations.members(userKey)).thenReturn(tokens);
        when(redisTemplate.getExpire(token1, TimeUnit.MILLISECONDS)).thenReturn(0L);
        when(redisTemplate.getExpire(token2, TimeUnit.MILLISECONDS)).thenReturn(-100L);

        doNothing().when(privateLobbyTokenService).cleanUpUserTokens(userKey);

        UserActiveTokensDto result = privateLobbyTokenService.getActiveTokensForUser(userId);

        assertThat(result.getActiveTokens()).isEmpty();

        verify(privateLobbyTokenService).cleanUpUserTokens(userKey);
        verify(redisTemplate).getExpire(token1, TimeUnit.MILLISECONDS);
        verify(redisTemplate).getExpire(token2, TimeUnit.MILLISECONDS);
    }

    @Test
    void getActiveTokensForUser_allTokensActive_returnsAllTokens() {
        // Arrange
        Long userId = 7L;
        String userKey = "user:" + userId + ":tokens";

        String token1 = "t1";
        String token2 = "t2";
        Set<String> tokens = Set.of(token1, token2);

        when(redisTemplate.opsForSet()).thenReturn(setOperations);

        when(setOperations.members(userKey)).thenReturn(tokens);
        when(redisTemplate.getExpire(token1, TimeUnit.MILLISECONDS)).thenReturn(5000L);
        when(redisTemplate.getExpire(token2, TimeUnit.MILLISECONDS)).thenReturn(10000L);

        doNothing().when(privateLobbyTokenService).cleanUpUserTokens(userKey);

        UserActiveTokensDto result = privateLobbyTokenService.getActiveTokensForUser(userId);

        List<UserActiveTokensDto.TokenWithExpiry> activeTokens = result.getActiveTokens();
        assertThat(activeTokens).hasSize(2);
        assertThat(activeTokens).extracting("token")
                .containsExactlyInAnyOrder(token1, token2);

        verify(privateLobbyTokenService).cleanUpUserTokens(userKey);
        verify(redisTemplate).getExpire(token1, TimeUnit.MILLISECONDS);
        verify(redisTemplate).getExpire(token2, TimeUnit.MILLISECONDS);
    }

    @Test
    void generateToken_success_createsAndStoresToken() {
        Long userId = 1L;
        Long lobbyId = 100L;
        String expectedToken = "token123";
        String userKey = "user:" + userId + ":tokens";

        when(redisTemplate.opsForSet()).thenReturn(setOperations);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        doNothing().when(privateLobbyTokenService).cleanUpUserTokens(userKey);

        when(setOperations.add(anyString(), anyString())).thenReturn(1L);
        when(setOperations.size(anyString())).thenReturn(0L);

        when(secureInvitationsService.createInvitationToken(lobbyId, userId)).thenReturn(expectedToken);

        String token = privateLobbyTokenService.generateToken(lobbyId, userId);

        assertThat(token).isEqualTo(expectedToken);
        verify(privateLobbyTokenService).cleanUpUserTokens(userKey);
        verify(secureInvitationsService).createInvitationToken(any(Long.class), any(Long.class));
        verify(valueOperations).set(expectedToken, "lobby-" + lobbyId, Duration.ofMinutes(10));

    }

    @Test
    void generateToken_throwsMaxActiveTokenException_whenTooManyActiveTokens() {
        Long userId = 1L;
        Long lobbyId = 100L;
        String userKey = "user:" + userId + ":tokens";

        when(redisTemplate.opsForSet()).thenReturn(setOperations);

        doNothing().when(privateLobbyTokenService).cleanUpUserTokens(userKey);

        when(setOperations.size(anyString())).thenReturn((long) MAX_TOKENS);

        assertThatThrownBy(() -> privateLobbyTokenService.generateToken(lobbyId, userId))
                .isInstanceOf(MaxActiveTokenException.class)
                .hasMessageContaining("Maximum number of active private tokens reached");

        verify(privateLobbyTokenService).cleanUpUserTokens(userKey);
        verify(secureInvitationsService, never()).createInvitationToken(any(Long.class), any(Long.class));
        verify(valueOperations, never()).set(anyString(), anyString(), any());
    }

    @Test
    void joinPrivateLobbyWithToken_success_returnsLobbyId() {
        String token = "token123";
        Long lobbyId = 100L;
        Long userId = 42L;
        String tokenLobbyValue = "lobby-" + lobbyId;
        String userKey = "user:" + userId + ":tokens";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForSet()).thenReturn(setOperations);

        TokenIdentifiers identifiers = new TokenIdentifiers(lobbyId, userId);

        when(valueOperations.getAndDelete(token)).thenReturn(tokenLobbyValue);
        when(secureInvitationsService.validateInvitationToken(token)).thenReturn(identifiers);

        Long returnedLobbyId = privateLobbyTokenService.joinPrivateLobbyWithToken(token);

        assertThat(returnedLobbyId).isEqualTo(lobbyId);
        verify(valueOperations).getAndDelete(token);
        verify(setOperations).remove(userKey, token);
    }

    @Test
    void joinPrivateLobbyWithToken_throwsTokenNotFoundException_whenTokenDoesNotExist() {
        String token = "token123";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.getAndDelete(token)).thenReturn(null);

        assertThatThrownBy(() -> privateLobbyTokenService.joinPrivateLobbyWithToken(token))
                .isInstanceOf(TokenNotFoundException.class)
                .hasMessageContaining("Token provided does not exist");

        verify(valueOperations).getAndDelete(token);
    }

    @Test
    void joinPrivateLobbyWithToken_throwsInvalidTokenException_whenTokenInvalid() {
        String token = "token123";
        Long lobbyId = 100L;
        Long userId = 42L;
        String tokenLobbyValue = "lobby-" + lobbyId;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.getAndDelete(token)).thenReturn(tokenLobbyValue);
        when(secureInvitationsService.validateInvitationToken(token)).thenReturn(null);

        assertThatThrownBy(() -> privateLobbyTokenService.joinPrivateLobbyWithToken(token))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid token provided");

        verify(valueOperations).getAndDelete(token);
    }

    @Test
    void joinPrivateLobbyWithToken_throwsInvalidTokenException_whenLobbyIdMismatch() {
        String token = "token123";
        Long lobbyIdInRedis = 100L;
        Long lobbyIdInToken = 999L; // mismatch
        Long userId = 42L;
        String tokenLobbyValue = "lobby-" + lobbyIdInRedis;
        String userKey = "user:" + userId + ":tokens";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForSet()).thenReturn(setOperations);

        TokenIdentifiers identifiers = new TokenIdentifiers(lobbyIdInToken, userId);

        when(valueOperations.getAndDelete(token)).thenReturn(tokenLobbyValue);
        when(secureInvitationsService.validateInvitationToken(token)).thenReturn(identifiers);

        assertThatThrownBy(() -> privateLobbyTokenService.joinPrivateLobbyWithToken(token))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Lobby ID associated with token in Redis doesn't match");

        verify(valueOperations).getAndDelete(token);
        verify(setOperations).remove(anyString(), anyString());
    }

}
