package com.github.ryand6.sudokuweb.controllers.handlers;

import com.github.ryand6.sudokuweb.exceptions.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class ErrorMapping {

    public static final Map<Class<? extends Throwable>, HttpStatus> EXCEPTION_HTTP_STATUS_MAP = Map.ofEntries(
            Map.entry(UserNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(LobbyNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(LobbyPlayerNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(TokenNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(LobbyFullException.class, HttpStatus.CONFLICT),
            Map.entry(UsernameTakenException.class, HttpStatus.CONFLICT),
            Map.entry(LobbyInactiveException.class, HttpStatus.GONE),
            Map.entry(OAuth2LoginRequiredException.class, HttpStatus.UNAUTHORIZED),
            Map.entry(InvalidTokenException.class, HttpStatus.UNAUTHORIZED),
            Map.entry(UserExistsInActiveLobbyException.class, HttpStatus.UNAUTHORIZED),
            Map.entry(InvalidLobbyPublicStatusParameterException.class, HttpStatus.BAD_REQUEST),
            Map.entry(MessageProfanityException.class, HttpStatus.BAD_REQUEST),
            Map.entry(MessageTooSoonException.class, HttpStatus.TOO_MANY_REQUESTS)
    );

    public static final Map<Class<? extends Throwable>, String> EXCEPTION_WS_ERROR_TYPE_MAP = Map.ofEntries(
            Map.entry(MessageProfanityException.class, "CHAT_ERROR"),
            Map.entry(MessageTooSoonException.class, "CHAT_ERROR"),
            Map.entry(LobbyNotFoundException.class, "LOBBY_ERROR"),
            Map.entry(LobbyPlayerNotFoundException.class, "LOBBY_ERROR"),
            Map.entry(LobbyFullException.class, "LOBBY_ERROR"),
            Map.entry(LobbyInactiveException.class, "LOBBY_ERROR"),
            Map.entry(InvalidLobbyPublicStatusParameterException.class, "LOBBY_ERROR"),
            Map.entry(ConstraintViolationException.class, "GENERAL_ERROR")
    );

}
