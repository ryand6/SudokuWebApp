package com.github.ryand6.sudokuweb.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class HashUtils {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    public static String generateHmacHash(String input, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), HMAC_ALGORITHM);
            mac.init(secretKey);
            byte[] hash = mac.doFinal(input.toLowerCase().trim().getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash input", e);
        }
    }

}
