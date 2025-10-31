package com.github.ryand6.sudokuweb.controllers.handlers;

import com.github.ryand6.sudokuweb.dto.ApiErrorDto;
import com.github.ryand6.sudokuweb.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
/*
Handles exceptions and through HTTP Status and Messaging - handles exceptions that propagate to Controller and passes to React via ResponseBody
 */
public class RestExceptionHandler {

    private static final Map<Class<? extends Throwable>, HttpStatus> EXCEPTION_HTTP_STATUS_MAP = Map.ofEntries(
            Map.entry(UserNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(LobbyNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(LobbyPlayerNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(TokenNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(LobbyFullException.class, HttpStatus.CONFLICT),
            Map.entry(UsernameTakenException.class, HttpStatus.CONFLICT),
            Map.entry(LobbyInactiveException.class, HttpStatus.GONE),
            Map.entry(OAuth2LoginRequiredException.class, HttpStatus.UNAUTHORIZED),
            Map.entry(InvalidTokenException.class, HttpStatus.UNAUTHORIZED),
            Map.entry(InvalidLobbyPublicStatusParameterException.class, HttpStatus.BAD_REQUEST),
            Map.entry(MessageProfanityException.class, HttpStatus.BAD_REQUEST),
            Map.entry(MessageTooSoonException.class, HttpStatus.TOO_MANY_REQUESTS)
    );

    // catches all Controller thrown exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleAllExceptions(Exception ex) {
        HttpStatus status = EXCEPTION_HTTP_STATUS_MAP.getOrDefault(ex.getClass(), HttpStatus.BAD_REQUEST);
        ApiErrorDto error = new ApiErrorDto(ex.getMessage());
        return ResponseEntity.status(status).body(error);
    }

}
