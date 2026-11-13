package com.github.ryand6.sudokuweb.controllers.testing;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

@RestController
@RequestMapping("/beta-access")
public class BetaAccessController {

    private static final String SESSION_KEY = "betaAccessGranted";

    @Value("${BETA_ACCESS_ENABLED:false}")
    private boolean enabled;

    @Value("${BETA_ACCESS_PASSWORD:}")
    private String betaPassword;

    public record AccessRequest(String password) {}

    @GetMapping
    public ResponseEntity<Map<String, Boolean>> checkAccess(
            HttpServletRequest request) {

        if (!enabled || betaPassword.isBlank()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }

        var session = request.getSession(false);

        boolean unlocked = session != null &&
                Boolean.TRUE.equals(session.getAttribute(SESSION_KEY));

        return ResponseEntity.ok(Map.of("unlocked", unlocked));
    }

    @PostMapping
    public ResponseEntity<Void> unlock(
            @RequestBody AccessRequest body,
            HttpServletRequest request) {

        if (!enabled || betaPassword.isBlank()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }

        if (body.password() == null ||
                !MessageDigest.isEqual(
                        betaPassword.getBytes(StandardCharsets.UTF_8),
                        body.password().getBytes(StandardCharsets.UTF_8))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        request.getSession(true).setAttribute(SESSION_KEY, true);

        return ResponseEntity.noContent().build();
    }
}


