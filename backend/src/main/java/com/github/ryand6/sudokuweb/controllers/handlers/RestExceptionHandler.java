package com.github.ryand6.sudokuweb.controllers.handlers;

import com.github.ryand6.sudokuweb.dto.ApiErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = RestController.class)
/*
Handles exceptions and through HTTP Status and Messaging - handles exceptions that propagate to Controller and passes to React via ResponseBody
 */
public class RestExceptionHandler {

    // catches all Controller thrown exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleAllExceptions(Exception ex) {
        if (ex.getClass().isInstance(ObjectOptimisticLockingFailureException.class)) {
            HttpStatus status = HttpStatus.CONFLICT;
            ApiErrorDto error = new ApiErrorDto("")
        }
        HttpStatus status = ErrorMapping.EXCEPTION_HTTP_STATUS_MAP.getOrDefault(ex.getClass(), HttpStatus.BAD_REQUEST);
        ApiErrorDto error = new ApiErrorDto(ex.getMessage());
        return ResponseEntity.status(status).body(error);
    }

}
