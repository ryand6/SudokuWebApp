package com.github.ryand6.sudokuweb.controllers.handlers;

import com.github.ryand6.sudokuweb.dto.ApiErrorDto;
import com.github.ryand6.sudokuweb.exceptions.InvalidDifficultyException;
import com.github.ryand6.sudokuweb.exceptions.TooManyActivePlayersException;
import com.github.ryand6.sudokuweb.exceptions.UserNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.UsernameTakenException;
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

        // different behavior depending on exception type
        if (ex instanceof UserNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        }

        ApiErrorDto error = new ApiErrorDto(ex.getMessage());
        return ResponseEntity.status(status).body(error);
    }

}
