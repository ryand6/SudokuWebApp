package com.github.ryand6.sudokuweb.config;

import com.github.ryand6.sudokuweb.dto.UserDto;
import com.github.ryand6.sudokuweb.services.GameService;
import com.github.ryand6.sudokuweb.services.LobbyService;
import com.github.ryand6.sudokuweb.services.UserService;
import com.github.ryand6.sudokuweb.util.OAuthUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.messaging.access.intercept.MessageAuthorizationContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.messaging.Message;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

import java.util.function.Supplier;

@Component
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {

    private final LobbyService lobbyService;
    private final UserService userService;
    private final GameService gameService;

    public WebSocketSecurityConfig(LobbyService lobbyService, UserService userService, GameService gameService) {
        this.lobbyService = lobbyService;
        this.userService = userService;
        this.gameService = gameService;
    }

    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        messages
                .simpDestMatchers("/app/user/{providerId}")
                    .access(this::checkUserProviderIdMatch)
                // Ensure that users can only send messages to lobbies that they are a member of
                .simpDestMatchers("/app/lobby/{lobbyId}")
                    .access(this::checkLobbyMembership)
                // Ensure that users can only send messages to games that they are a member of
                .simpDestMatchers("/app/game/{gameId}")
                    .access(this::checkGameMembership)
                // Ensure specific user endpoint can only be subscribed to by that user
                .simpSubscribeDestMatchers("/topic/user/{providerId}")
                    .access(this::checkUserProviderIdMatch)
                // Ensure that users can only subscribe to lobbies that they are a member of
                .simpSubscribeDestMatchers("/topic/lobby/{lobbyId}")
                    .access(this::checkLobbyMembership)
                // Ensure that users can only subscribe to games that they are a member of
                .simpSubscribeDestMatchers("/topic/game/{gameId}")
                    .access(this::checkGameMembership)
                .anyMessage().denyAll();
        return messages.build();
    }

    // Ensure endpoint variable matches user's OAuth2 unique ID
    private AuthorizationDecision checkUserProviderIdMatch(Supplier<Authentication> authenticationSupplier, MessageAuthorizationContext<?> context) {
        Authentication authentication = authenticationSupplier.get();
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User principal = token.getPrincipal();
        String providerId = context.getVariables().get("providerId");
        String userProviderId = OAuthUtil.retrieveOAuthProviderId(token.getAuthorizedClientRegistrationId(), principal);
        return new AuthorizationDecision(userProviderId.equals(providerId));
    }

    // Uses Authentication context to find user in DB and confirm if they're an active member in the lobby
    private AuthorizationDecision checkLobbyMembership(Supplier<Authentication> authenticationSupplier, MessageAuthorizationContext<?> context) {
        UserDto user = resolveUserDtoFromAuthenticationSupplier(authenticationSupplier);
        Long lobbyId = Long.valueOf(context.getVariables().get("lobbyId"));
        return new AuthorizationDecision(lobbyService.isUserInLobby(user.getId(), lobbyId));
    }

    // Uses Authentication context to find user in DB and confirm if they're a member of the game
    private AuthorizationDecision checkGameMembership(Supplier<Authentication> authenticationSupplier, MessageAuthorizationContext<?> context) {
        UserDto user = resolveUserDtoFromAuthenticationSupplier(authenticationSupplier);
        Long gameId = Long.valueOf(context.getVariables().get("gameId"));
        return new AuthorizationDecision(gameService.isUserInGame(user.getId(), gameId));
    }

    // Get UserDto using Authentication object
    private UserDto resolveUserDtoFromAuthenticationSupplier(Supplier<Authentication> authenticationSupplier) {
        Authentication authentication = authenticationSupplier.get();
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User principal = token.getPrincipal();
        return userService.getCurrentUserByOAuth(principal, token);
    }

}
