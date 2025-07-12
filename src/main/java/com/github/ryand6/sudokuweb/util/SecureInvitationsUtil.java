package com.github.ryand6.sudokuweb.util;

import com.github.ryand6.sudokuweb.dto.TokenIdentifiers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Component
public class SecureInvitationsUtil {

        private static byte[] SECRET_KEY;
        private static final String HMAC_ALGORITHM = "HmacSHA256";
        // Token available for 10 minutes
        private static final long INVITATION_VALIDITY_MINUTES = 10;
        private static final SecureRandom SECURE_RANDOM = new SecureRandom();

        public SecureInvitationsUtil(@Value("${hmac.secret_key}") String hmacSecretKey) {
            SECRET_KEY = hmacSecretKey.getBytes(StandardCharsets.UTF_8);
        }

        /**
         * Creates a secure invitation token that expires after 24 hours
         * @param lobbyId The lobby ID
         * @param userId The user ID creating the invitation
         * @return A secure, time-limited invitation token
         */
        public static String createInvitationToken(Long lobbyId, Long userId) {
            long expiryTime = Instant.now().plusSeconds(INVITATION_VALIDITY_MINUTES * 60).getEpochSecond();
            String nonce = generateNonce();
            String payload = lobbyId + ":" + userId + ":" + expiryTime + ":" + nonce;
            String signature = generateSignature(payload);
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
        public static TokenIdentifiers validateInvitationToken(String token) {
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
                if (Instant.now().getEpochSecond() > expiryTime) {
                    return null;
                }

                // Verify signature
                String payload = lobbyId + ":" + userId + ":" + expiryTime + ":" + nonce;
                String expectedSignature = generateSignature(payload);
                // Signature of received token must match the signature generated for the token to be authenticated as the same secret key is used
                if (!signature.equals(expectedSignature)) {
                    return null;
                }

                return new TokenIdentifiers(lobbyId, userId);

            } catch (Exception e) {
                return null;
            }
        }

        /**
         * Attempt to hash a payload using HMAC algorithm and secret key
         * @param payload The message to hash
         * @return The hashed value of the message
         */
        private static String generateSignature(String payload) {
            try {
                Mac mac = Mac.getInstance(HMAC_ALGORITHM);
                // Create key spec to initialise mac object - key spec associating the secret key with the relevant cryptography algorithm to be applied
                SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY, HMAC_ALGORITHM);
                mac.init(secretKeySpec);
                // Create the HMAC signature using the payload
                byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
                return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
                // Caught if hashing algorithm not found or the secret key provided is invalid for that algorithm
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                throw new RuntimeException("Failed to generate signature", e);
            }
        }

        // Create string of randomised bytes for use in payload to prevent predictability
        private static String generateNonce() {
            // Take a byte array and fill it with randomised bytes
            byte[] nonce = new byte[16];
            SECURE_RANDOM.nextBytes(nonce);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(nonce);
        }
}
