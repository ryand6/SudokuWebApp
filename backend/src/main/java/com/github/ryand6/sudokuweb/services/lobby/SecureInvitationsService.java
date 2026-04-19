package com.github.ryand6.sudokuweb.services.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.token.TokenIdentifiers;
import com.github.ryand6.sudokuweb.util.HashUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;

@Service
public class SecureInvitationsService {

        private static final String HMAC_ALGORITHM = "HmacSHA256";
        // Token available for 10 minutes
        private static final long INVITATION_VALIDITY_MINUTES = 10;
        private static final SecureRandom SECURE_RANDOM = new SecureRandom();
        private final String SECRET_KEY;
        // Injectable clock, which allows testing to be carried out on time thresholds
        private final Clock clock;

        public SecureInvitationsService(@Value("${hmac.secret-key}") String hmacSecretKey,
                                        Clock clock) {
            SECRET_KEY = hmacSecretKey;
            this.clock = clock;
        }

        /**
         * Creates a secure invitation token that expires after 10 minutes
         * @param lobbyId The lobby ID
         * @param userId The user ID creating the invitation
         * @return A secure, time-limited invitation token
         */
        public String createInvitationToken(Long lobbyId, Long userId) {
            long expiryTime = Instant.now(clock).plusSeconds(INVITATION_VALIDITY_MINUTES * 60).getEpochSecond();
            String nonce = generateNonce();
            String payload = lobbyId + ":" + userId + ":" + expiryTime + ":" + nonce;
            String signature = HashUtils.generateHmacHash(payload, SECRET_KEY);
            // Attach the HMAC signature to the token
            String token = payload + ":" + signature;
            // Return Base64 encoded token that is safe for use in lobby URL
            return Base64.getUrlEncoder().withoutPadding().encodeToString(token.getBytes(StandardCharsets.UTF_8));
        }

        /**
         * Validates and extracts lobby ID and ID of user that created token from invitation token
         * @param token The invitation token
         * @return The TokenIdentifiers DTO (containing lobby ID and user ID) if valid, null if invalid or expired
         */
        public TokenIdentifiers validateInvitationToken(String token) {
            try {
                String decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
                String[] parts = decoded.split(":");

                // Validate that the token has 5x parts as expected
                if (parts.length != 5) {
                    return null;
                }
                Long lobbyId = Long.parseLong(parts[0]);
                Long userId = Long.parseLong(parts[1]);
                Long expiryTime = Long.parseLong(parts[2]);
                String nonce = parts[3];
                String signature = parts[4];

                // Check if expired - additional preventative measure, also handled in Redis
                if (Instant.now(clock).getEpochSecond() > expiryTime) {
                    return null;
                }

                // Verify signature
                String payload = lobbyId + ":" + userId + ":" + expiryTime + ":" + nonce;
                String expectedSignature = HashUtils.generateHmacHash(payload, SECRET_KEY);
                // Signature of received token must match the signature generated for the token to be authenticated as the same secret key is used
                if (!signature.equals(expectedSignature)) {
                    return null;
                }

                return new TokenIdentifiers(lobbyId, userId);

            } catch (Exception e) {
                return null;
            }
        }

        // Create string of randomised bytes for use in payload to prevent predictability
        private String generateNonce() {
            // Take a byte array and fill it with randomised bytes
            byte[] nonce = new byte[16];
            SECURE_RANDOM.nextBytes(nonce);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(nonce);
        }
}
