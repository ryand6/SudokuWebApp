package com.github.ryand6.sudokuweb.controllers.handlers;

import com.github.ryand6.sudokuweb.exceptions.auth.OAuth2LoginRequiredException;
import com.github.ryand6.sudokuweb.exceptions.game.GameNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.game.InvalidCellCoordinatesException;
import com.github.ryand6.sudokuweb.exceptions.game.player.GamePlayerNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.lobby.*;
import com.github.ryand6.sudokuweb.exceptions.lobby.chat.MessageProfanityException;
import com.github.ryand6.sudokuweb.exceptions.lobby.chat.MessageTooSoonException;
import com.github.ryand6.sudokuweb.exceptions.lobby.player.LobbyPlayerNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.lobby.settings.InvalidLobbyPublicStatusParameterException;
import com.github.ryand6.sudokuweb.exceptions.lobby.settings.LobbySettingsLockedException;
import com.github.ryand6.sudokuweb.exceptions.lobby.token.InvalidTokenException;
import com.github.ryand6.sudokuweb.exceptions.lobby.token.TokenNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.user.UserNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.user.UsernameTakenException;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;

import java.util.Map;

public class ErrorMapping {

    public static final Map<Class<? extends Throwable>, HttpStatus> EXCEPTION_HTTP_STATUS_MAP = Map.ofEntries(
            Map.entry(UserNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(LobbyNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(GameNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(GamePlayerNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(LobbyPlayerNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(TokenNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(LobbyFullException.class, HttpStatus.CONFLICT),
            Map.entry(LobbySettingsLockedException.class, HttpStatus.CONFLICT),
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
            // Handle lobby chat errors
            Map.entry(MessageProfanityException.class, "CHAT_ERROR"),
            Map.entry(MessageTooSoonException.class, "CHAT_ERROR"),
            // Handle general lobby errors
            Map.entry(LobbyNotFoundException.class, "LOBBY_ERROR"),
            Map.entry(LobbyPlayerNotFoundException.class, "LOBBY_ERROR"),
            Map.entry(LobbyFullException.class, "LOBBY_ERROR"),
            Map.entry(LobbyInactiveException.class, "LOBBY_ERROR"),
            Map.entry(LobbySettingsLockedException.class, "LOBBY_ERROR"),
            Map.entry(InvalidLobbyPublicStatusParameterException.class, "LOBBY_ERROR"),
            // Handle game errors
            Map.entry(GameNotFoundException.class, "GAME_ERROR"),
            Map.entry(GamePlayerNotFoundException.class, "GAME_ERROR"),
            Map.entry(InvalidCellCoordinatesException.class, "GAME_ERROR"),
            // Handle DTO field validation errors
            Map.entry(MethodArgumentNotValidException.class, "GENERAL_ERROR")
    );

}
