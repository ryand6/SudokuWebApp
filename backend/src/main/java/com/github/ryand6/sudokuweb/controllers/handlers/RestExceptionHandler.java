package com.github.ryand6.sudokuweb.controllers.handlers;

import com.github.ryand6.sudokuweb.dto.ApiErrorDto;
import com.github.ryand6.sudokuweb.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
/*
Handles exceptions and through HTTP Status and Messaging - handles exceptions that propagate to Controller and passes to React via ResponseBody
 */
public class RestExceptionHandler {

    // catches all Controller thrown exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleAllExceptions(Exception ex) {

        // Default HTTP code
        HttpStatus status = HttpStatus.BAD_REQUEST;

        // different behavior depending on exception type (404 error)
        if (ex instanceof UserNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        }

        if (ex instanceof UsernameTakenException) {
            status = HttpStatus.CONFLICT;
        }

        // 401 error
        if (ex instanceof OAuth2LoginRequiredException) {
            status = HttpStatus.UNAUTHORIZED;
        }

        ApiErrorDto error = new ApiErrorDto(ex.getMessage());
        return ResponseEntity.status(status).body(error);
    }

}
