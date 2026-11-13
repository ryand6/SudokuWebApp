package com.github.ryand6.sudokuweb.config;

import com.github.ryand6.sudokuweb.dto.entity.user.UserDto;
import com.github.ryand6.sudokuweb.services.MembershipService;
import com.github.ryand6.sudokuweb.services.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.SimpMessageType;
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

    private final UserService userService;
    private final MembershipService membershipService;

    public WebSocketSecurityConfig(UserService userService,
                                   MembershipService membershipService) {
        this.userService = userService;
        this.membershipService = membershipService;
    }

    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        messages
                // Ensure that users can only send messages to lobbies that they are a member of
                .simpMessageDestMatchers("/app/lobby/{lobbyId}/**")
                    .access(this::checkLobbyMembership)
                // Ensure that users can only send spring messages to games that they are a member of
                .simpMessageDestMatchers("/app/game/{gameId}/**")
                    .access(this::checkGameMembership)
                .simpMessageDestMatchers("/app/user/update-settings")
                    .authenticated()
                // Spring automatically maps messages to the correct user using the Principal - additional validation to ensure user is authenticated
                .simpSubscribeDestMatchers("/user/queue/updates", "/user/queue/errors")
                    .authenticated()
                // Ensure that users can only subscribe to lobbies that they are a member of
                .simpSubscribeDestMatchers("/topic/lobby/{lobbyId}")
                    .access(this::checkLobbyMembership)
                // Ensure that users can only subscribe to games that they are a member of
                .simpSubscribeDestMatchers("/topic/game/{gameId}")
                    .access(this::checkGameMembership)
                .simpSubscribeDestMatchers("/topic/game/{gameId}/user/{userId}")
                    .access(this::checkGamePlayerMembership)
                // Ensures the WS connection session is authenticated using the principal when handling this message types
                .simpTypeMatchers(
                        SimpMessageType.CONNECT,
                        SimpMessageType.DISCONNECT,
                        SimpMessageType.UNSUBSCRIBE
                )
                    .authenticated()
                // Always allow heartbeat messages to keep the connection alive
                .simpTypeMatchers(
                        SimpMessageType.HEARTBEAT
                )
                    .permitAll()

                .anyMessage().denyAll();
        return messages.build();
    }

    // Uses Authentication context to find user in DB and confirm if they're an active member in the lobby
    private AuthorizationDecision checkLobbyMembership(Supplier<Authentication> authenticationSupplier, MessageAuthorizationContext<?> context) {
        UserDto user = resolveUserDtoFromAuthenticationSupplier(authenticationSupplier);
        if (user == null) {
            return new AuthorizationDecision(false);
        }
        Long lobbyId = Long.valueOf(context.getVariables().get("lobbyId"));
        return new AuthorizationDecision(membershipService.isUserInLobby(user.getId(), lobbyId));
    }

    // Uses Authentication context to find user in DB and confirm if they're a member of the game
    private AuthorizationDecision checkGameMembership(Supplier<Authentication> authenticationSupplier, MessageAuthorizationContext<?> context) {
        UserDto user = resolveUserDtoFromAuthenticationSupplier(authenticationSupplier);
        if (user == null) {
            return new AuthorizationDecision(false);
        }
        Long gameId = Long.valueOf(context.getVariables().get("gameId"));
        return new AuthorizationDecision(membershipService.isUserInGame(user.getId(), gameId));
    }

    private AuthorizationDecision checkGamePlayerMembership(Supplier<Authentication> authenticationSupplier, MessageAuthorizationContext<?> context) {
        UserDto user = resolveUserDtoFromAuthenticationSupplier(authenticationSupplier);
        if (user == null) {
            return new AuthorizationDecision(false);
        }
        Long gameId = Long.valueOf(context.getVariables().get("gameId"));
        Long targetUserId = Long.valueOf(context.getVariables().get("userId"));
        boolean isGameMember = membershipService.isUserInGame(user.getId(), gameId);
        boolean isOwnSubscription = user.getId().equals(targetUserId);
        return new AuthorizationDecision(isGameMember && isOwnSubscription);
    }

    // Get UserDto using Authentication object
    private UserDto resolveUserDtoFromAuthenticationSupplier(Supplier<Authentication> authenticationSupplier) {
        Authentication authentication = authenticationSupplier.get();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication instanceof OAuth2AuthenticationToken)) {
            return null;
        }
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User principal = token.getPrincipal();
        return userService.getCurrentUserByOAuth(principal, token);
    }

}
