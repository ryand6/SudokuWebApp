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

    public String generateAndStoreOtp(Long userId) {
        String otp = OtpUtils.generateOtp();
        redisTemplate.opsForValue().set(OTP_PREFIX + userId, otp, Duration.ofMinutes(otpExpiryMinutes));
        return otp;
    }

    public void validateOtp(Long userId, String otp) {
        String retrievedOtp = redisTemplate.opsForValue().get(OTP_PREFIX + userId);
        if (!otp.equals(retrievedOtp)) {
            throw new InvalidOtpException("Invalid or expired OTP");
        }
        redisTemplate.delete(OTP_PREFIX + userId);
    }

}
