package com.github.ryand6.sudokuweb.util;

import java.security.SecureRandom;

public final class OtpUtils {

    private static final SecureRandom random = new SecureRandom();

    public static String generateOtp() {
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

}
