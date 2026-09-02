package com.tranquility;

import com.tranquility.common.auth.AuthException;
import com.tranquility.common.dto.ApiResponse;
import com.tranquility.common.file.FileReadException;
import com.tranquility.common.file.FileStorageException;
import com.tranquility.common.user.exception.NoProfilePicException;
import com.tranquility.common.user.exception.UserAlreadyExistsException;
import com.tranquility.common.user.exception.UserNotFoundException;
import com.tranquility.common.utils.CursorCodecException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.io.IOException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({
            ConstraintViolationException.class,
            HandlerMethodValidationException.class
    })
    public ResponseEntity<?> handleValidationException(Exception ex) {
        String message = switch (ex) {
            case ConstraintViolationException e -> e.getConstraintViolations()
                    .stream()
                    .findFirst()
                    .map(ConstraintViolation::getMessage)
                    .orElse("Validation failed!");

            case HandlerMethodValidationException e -> e.getAllErrors()
                    .stream()
                    .findFirst()
                    .map(MessageSourceResolvable::getDefaultMessage)
                    .orElse("Validation failed!");
            default -> "Validation failed!";
        };

        log.error("Validation failed: {}", message);
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), message));
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<?> handleException(RestClientException e) {
        log.error("Something went wrong during api call. Message: {}, Cause: {}", e.getMessage(), e.getCause().getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponse.error(HttpStatus.BAD_GATEWAY.value(), "Something went wrong!"));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleException(IOException e) {
        log.error("Something went wrong during file handling. Message: {}, Cause: {}", e.getMessage(), e.getCause().getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponse.error(HttpStatus.BAD_GATEWAY.value(), "Something went wrong!"));
    }

//    ----------------------- Custom Exceptions ---------------------------------

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<?> handleException(AuthException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleException(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleException(UserAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(HttpStatus.CONFLICT.value(), e.getMessage()));
    }

    @ExceptionHandler(NoProfilePicException.class)
    public ResponseEntity<?> handleException(NoProfilePicException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<?> handleException(FileStorageException e) {
        log.error("{}: {}", e.getMessage(), e.srcExc.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
    }

    @ExceptionHandler(CursorCodecException.class)
    public ResponseEntity<?> handleException(CursorCodecException e) {
        log.error("{}: {}", "Cursor encoding/decoding failed!", e.srcExc.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong. Please try again later."));
    }

    @ExceptionHandler(FileReadException.class)
    public ResponseEntity<?> handleException(FileReadException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
    }
}