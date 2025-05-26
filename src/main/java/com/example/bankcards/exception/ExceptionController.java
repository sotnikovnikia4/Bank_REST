package com.example.bankcards.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Date;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {
    private final ObjectMapper objectMapper;

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ExceptionMessage> handleException(ValidationException e){
        ExceptionMessage message = ExceptionMessage.builder().message(e.getMessage()).status(HttpStatus.BAD_REQUEST.value()).timestamp(new Date()).build();
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ExceptionMessage> handleException(UsernameNotFoundException e){
        ExceptionMessage message = ExceptionMessage.builder().message(e.getMessage()).status(HttpStatus.UNAUTHORIZED.value()).timestamp(new Date()).build();
        return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionMessage> handleException(BadCredentialsException e){
        ExceptionMessage message = ExceptionMessage.builder().message(e.getMessage()).status(HttpStatus.UNAUTHORIZED.value()).timestamp(new Date()).build();
        return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<ExceptionMessage> handleException(JWTVerificationException e){
        ExceptionMessage message = ExceptionMessage.builder().message(e.getMessage()).status(HttpStatus.BAD_REQUEST.value()).timestamp(new Date()).build();
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationServiceException.class)
    public ResponseEntity<ExceptionMessage> handleException(AuthenticationServiceException e){
        ExceptionMessage message = ExceptionMessage.builder().message(e.getMessage()).status(HttpStatus.UNAUTHORIZED.value()).timestamp(new Date()).build();
        return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionMessage> handleException(HttpMessageNotReadableException e){
        ExceptionMessage message = ExceptionMessage.builder().message("Not readable request's body").status(HttpStatus.BAD_REQUEST.value()).timestamp(new Date()).build();
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionMessage> handleException(MissingServletRequestParameterException e){
        ExceptionMessage message = ExceptionMessage.builder().message(e.getMessage()).status(HttpStatus.BAD_REQUEST.value()).timestamp(new Date()).build();
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionMessage> handleException(MethodArgumentTypeMismatchException e){
        ExceptionMessage message = ExceptionMessage.builder().message(e.getMessage()).status(HttpStatus.BAD_REQUEST.value()).timestamp(new Date()).build();
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @SneakyThrows
    public void accessDenied(HttpServletRequest request, HttpServletResponse response, Exception e) {
        ExceptionMessage message = ExceptionMessage.builder().message(e.getMessage()).status(HttpServletResponse.SC_FORBIDDEN).timestamp(new Date()).build();

        response.setStatus(message.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(message));
    }

    @SneakyThrows
    public void entryPoint(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
        ExceptionMessage message = ExceptionMessage.builder().message("Authentication is required").status(HttpServletResponse.SC_UNAUTHORIZED).timestamp(new Date()).build();

        response.setStatus(message.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(message));
    }
}
