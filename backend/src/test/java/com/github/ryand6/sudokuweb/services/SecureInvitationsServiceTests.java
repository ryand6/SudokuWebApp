package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.dto.TokenIdentifiers;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.base.Verify;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class SecureInvitationsServiceTests {

    private final String testSecretKey = "test-secret-key";
    private final Clock fixedClock = Clock.fixed(Instant.parse("2026-01-17T12:00:00Z"), ZoneOffset.UTC);

    private final SecureInvitationsService service = new SecureInvitationsService(testSecretKey, fixedClock);

    @Test
    void createInvitationToken_producesValidBase64() {
        Long lobbyId = 1L;
        Long userId = 2L;

        String token = service.createInvitationToken(lobbyId, userId);
        assertNotNull(token);

        String decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);

        // Verify the decoded string contains 5 parts
        String[] parts = decoded.split(":");
        assertEquals(parts.length, 5);

        // Verify the lobby and user IDs
        assertEquals(Long.valueOf(parts[0]), lobbyId);
        assertEquals(Long.valueOf(parts[1]), userId);

        // Verify the expiry time
        Long expectedExpiry = fixedClock.instant().plusSeconds(10 * 60).getEpochSecond();
        assertEquals(Long.valueOf(parts[2]), expectedExpiry);

        // Verify structure of the nonce - 16 bytes should map exactly to 22 Base-64 chars when converted
        assertEquals(22, parts[3].length());

        // Verify signature is not empty
        assertFalse(parts[4].isEmpty());
    }

    @Test
    void validateInvitationToken_verifiedValidToken() {
        Long lobbyId = 1L;
        Long userId = 1L;

        String token = service.createInvitationToken(lobbyId, userId);

        TokenIdentifiers tokenIdentifiers = service.validateInvitationToken(token);

        assertNotNull(tokenIdentifiers);
        assertEquals(lobbyId, tokenIdentifiers.getLobbyId());
        assertEquals(userId, tokenIdentifiers.getUserId());
    }

    @Test
    void validateInvitationToken_refusesExpiredToken() {
        Long lobbyId = 1L;
        Long userId = 1L;

        String token = service.createInvitationToken(lobbyId, userId);

        Clock updatedClock = Clock.fixed(
                fixedClock.instant().plusSeconds(11 * 60), ZoneOffset.UTC
        );

        SecureInvitationsService updatedService = new SecureInvitationsService(testSecretKey, updatedClock);

        assertNull(updatedService.validateInvitationToken(token));
    }

    @Test
    void validateInvitationToken_refusesTamperedPayload() {
        Long lobbyId = 1L;
        Long userId = 2L;

        String token = service.createInvitationToken(lobbyId, userId);

        byte[] decodedBytes = java.util.Base64.getUrlDecoder().decode(token);
        String decoded = new String(decodedBytes);

        System.out.println(decoded);

        String tampered = decoded.replace("1", "99");  // simple example
        String tamperedToken = java.util.Base64.getUrlEncoder().withoutPadding()
                .encodeToString(tampered.getBytes(StandardCharsets.UTF_8));

        assertNull(service.validateInvitationToken(tamperedToken));
    }

    @Test
    void validateInvitationToken_refusesMalformedToken() {
        assertNull(service.validateInvitationToken("1:1:1768651800:too-few-parts"));
        assertNull(service.validateInvitationToken("1:1:1768651800:f8nqMSpgg_NU8dNaF_JCSA:signature-does-not-match"));
    }

}
