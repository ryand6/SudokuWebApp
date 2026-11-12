package com.github.ryand6.sudokuweb.services.user;

import com.github.ryand6.sudokuweb.exceptions.auth.InvalidOtpException;
import com.github.ryand6.sudokuweb.exceptions.auth.TooManyOtpRequestsException;
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

    private static final String OTP_ATTEMPTS_PREFIX = "otp-attempts:";
    private static final int MAX_OTP_ATTEMPTS = 5;

    private static final String OTP_REQUEST_COOLDOWN_PREFIX = "otp-request-cooldown:";
    private static final String OTP_REQUEST_HOURLY_PREFIX = "otp-request-hourly:";
    private static final int OTP_REQUEST_COOLDOWN_SECONDS = 60;
    private static final int MAX_OTP_REQUESTS_PER_HOUR = 5;

    public OtpService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateAndStoreOtp(Long userId) {
        String otp = OtpUtils.generateOtp();
        redisTemplate.opsForValue().set(OTP_PREFIX + userId, otp, Duration.ofMinutes(otpExpiryMinutes));
        redisTemplate.delete(OTP_ATTEMPTS_PREFIX + userId);
        return otp;
    }

    public void validateOtp(Long userId, String otp) {
        String retrievedOtp = redisTemplate.opsForValue().get(OTP_PREFIX + userId);
        if (!otp.equals(retrievedOtp)) {
            invalidateOtpOnExceededAttempts(userId);
            throw new InvalidOtpException("Invalid or expired OTP");
        }
        redisTemplate.delete(OTP_PREFIX + userId);
    }

    private void invalidateOtpOnExceededAttempts(Long userId) {
        String attemptsKey = OTP_ATTEMPTS_PREFIX + userId;
        Long attempts = redisTemplate.opsForValue().increment(attemptsKey);
        if (attempts != null && attempts == 1) {
            redisTemplate.expire(
                    attemptsKey,
                    Duration.ofMinutes(otpExpiryMinutes)
            );
        }
        if (attempts != null && attempts >= MAX_OTP_ATTEMPTS) {
            redisTemplate.delete(OTP_PREFIX + userId);
        }
    }

    public void checkOtpRequestLimit(String emailHash) {
        String cooldownKey = OTP_REQUEST_COOLDOWN_PREFIX + emailHash;
        String hourlyKey = OTP_REQUEST_HOURLY_PREFIX + emailHash;
        Boolean cooldownSet = redisTemplate.opsForValue().setIfAbsent(cooldownKey, "1", Duration.ofSeconds(OTP_REQUEST_COOLDOWN_SECONDS));
        if (!Boolean.TRUE.equals(cooldownSet)) {
            throw new TooManyOtpRequestsException(
                    "Please wait before requesting another verification code."
            );
        }
        Long requests = redisTemplate.opsForValue().increment(hourlyKey);
        if (requests != null) {
            redisTemplate.expire(hourlyKey, Duration.ofHours(1));
            if (requests > MAX_OTP_REQUESTS_PER_HOUR) {
                throw new TooManyOtpRequestsException(
                        "Too many verification code requests. Please try again later."
                );
            }
        }
    }

}
