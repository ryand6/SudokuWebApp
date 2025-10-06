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

    private static final Map<Class<? extends Throwable>, HttpStatus> EXCEPTION_HTTP_STATUS_MAP = Map.of(
            UserNotFoundException.class, HttpStatus.NOT_FOUND,
            LobbyNotFoundException.class, HttpStatus.NOT_FOUND,
            TokenNotFoundException.class, HttpStatus.NOT_FOUND,
            LobbyFullException.class, HttpStatus.CONFLICT,
            UsernameTakenException.class, HttpStatus.CONFLICT,
            LobbyInactiveException.class, HttpStatus.GONE,
            OAuth2LoginRequiredException.class, HttpStatus.UNAUTHORIZED,
            InvalidTokenException.class, HttpStatus.UNAUTHORIZED
    );

    // catches all Controller thrown exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleAllExceptions(Exception ex) {
        HttpStatus status = EXCEPTION_HTTP_STATUS_MAP.getOrDefault(ex.getClass(), HttpStatus.BAD_REQUEST);
        ApiErrorDto error = new ApiErrorDto(ex.getMessage());
        return ResponseEntity.status(status).body(error);
    }

}
