package com.abel.videochattingsystem.Exception;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.nio.file.AccessDeniedException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ApiExceptionHandler {


    @ExceptionHandler(value = {ApiRequestException.class})
    public ResponseEntity<Object> handleApirEQUESTException(ApiRequestException e){
        // create payload containing exception detials
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ApiException apiException = new ApiException(
                e.getMessage(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        // return response entity
        return new ResponseEntity<>(apiException, badRequest);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ExpiredJwtException> handleExpiredJwtToken(ExpiredJwtException e){
        HttpStatus badRequest = HttpStatus.UNAUTHORIZED;
        ExpiredJwtException jwtException = new ExpiredJwtException(e.getHeader(), e.getClaims(), "Jwt token expired");
        return ResponseEntity.status(400).body(jwtException);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity handleAccessDeniedException(AccessDeniedException e){

        HttpStatus badRequest = HttpStatus.UNAUTHORIZED;
        ApiException apiException = new ApiException( e.getMessage(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z")));

        return ResponseEntity.status(apiException.getHttpStatus()).body(apiException);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationException(MethodArgumentNotValidException ex){
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        return errorMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleInvalidPayloadException (NullPointerException ex){
        return new ResponseEntity<String>("Invalid request param provided", HttpStatus.OK);
    }
}
