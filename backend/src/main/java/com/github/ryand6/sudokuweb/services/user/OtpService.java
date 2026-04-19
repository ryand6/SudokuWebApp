package com.github.ryand6.sudokuweb.services.user;

import com.github.ryand6.sudokuweb.exceptions.auth.InvalidOtpException;
import com.github.ryand6.sudokuweb.util.OtpUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;

@Service
public class OtpService {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${otp.expiry.minutes}")
    private long otpExpiryMinutes;

    private static final String OTP_PREFIX = "otp:";

    public OtpService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateAndStoreOtp(String sessionId) {
        String otp = OtpUtils.generateOtp();
        redisTemplate.opsForValue().set(OTP_PREFIX + sessionId, otp, Duration.ofMinutes(otpExpiryMinutes));
        return otp;
    }

    public void validateOtp(String sessionId, String otp) {
        String retrievedOtp = redisTemplate.opsForValue().get(OTP_PREFIX + sessionId);
        if (!otp.equals(retrievedOtp)) {
            throw new InvalidOtpException("Invalid or expired OTP");
        }
        redisTemplate.delete(OTP_PREFIX + sessionId);
    }

}
