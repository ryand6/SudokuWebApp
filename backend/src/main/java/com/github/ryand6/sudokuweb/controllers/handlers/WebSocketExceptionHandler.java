package com.github.ryand6.sudokuweb.controllers.handlers;

import com.github.ryand6.sudokuweb.util.OAuthUtil;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Map;

@ControllerAdvice(basePackages = "com.github.ryand6.sudokuweb.controllers.ws")
public class WebSocketExceptionHandler {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketExceptionHandler(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageExceptionHandler(Exception.class)
    public void handleWsExceptions(@AuthenticationPrincipal OAuth2User principal,
                                   OAuth2AuthenticationToken authToken,
                                   Exception ex) {
        // Get providerId so that updated Dto can be sent to correct user via websockets
        String providerName = OAuthUtil.retrieveOAuthProviderName(authToken);
        String providerId = OAuthUtil.retrieveOAuthProviderId(providerName, principal);
        String errorType = ErrorMapping.EXCEPTION_WS_ERROR_TYPE_MAP.getOrDefault(ex.getClass(), "UNKNOWN_ERROR");
        Map<String, Object> messageHeader = Map.of(
                "type", errorType,
                "code", ex.getClass().getSimpleName().toUpperCase(),
                "payload", ex.getMessage()
        );
        simpMessagingTemplate.convertAndSendToUser(
                providerId,
                "/queue/errors",
                messageHeader
        );
    }

}
