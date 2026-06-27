package com.example.config.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponseDTO> handleNotFound(
                ResourceNotFoundException ex,
                HttpServletRequest request
        ) {
                return buildResponse(
                        HttpStatus.NOT_FOUND,
                        ex.getMessage(),
                        request,
                        null
                );
        }

        @ExceptionHandler(AuthorizationDeniedException.class)
        public ResponseEntity<ErrorResponseDTO> handleAccessDenied(
                AuthorizationDeniedException ex,
                HttpServletRequest request
        ) {
        return buildResponse(
                HttpStatus.FORBIDDEN,
                "No tenés permisos para realizar esta acción.",
                request,
                null
        );
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ErrorResponseDTO> handleBadRequest(
                BadRequestException ex,
                HttpServletRequest request
        ) {
                return buildResponse(
                        HttpStatus.BAD_REQUEST,
                        ex.getMessage(),
                        request,
                        null
                );
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponseDTO> handleValidation(
                MethodArgumentNotValidException ex,
                HttpServletRequest request
        ) {
                Map<String, String> errores = new HashMap<>();

                ex.getBindingResult().getFieldErrors().forEach(error ->
                        errores.put(error.getField(), error.getDefaultMessage())
                );

                return buildResponse(
                        HttpStatus.BAD_REQUEST,
                        "Error de validación",
                        request,
                        errores
                );
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ErrorResponseDTO> handleBadCredentials(
                BadCredentialsException ex,
                HttpServletRequest request
        ) {
                return buildResponse(
                        HttpStatus.UNAUTHORIZED,
                        "Email o contraseña incorrectos",
                        request,
                        null
                );
        }

        @ExceptionHandler(UsernameNotFoundException.class)
        public ResponseEntity<ErrorResponseDTO> handleUsernameNotFound(
                UsernameNotFoundException ex,
                HttpServletRequest request
        ) {
                return buildResponse(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado",
                        request,
                        null
                );
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponseDTO> handleGeneral(
                Exception ex,
                HttpServletRequest request
        ) {
                return buildResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error interno del servidor",
                        request,
                        null
                );
        }

        private ResponseEntity<ErrorResponseDTO> buildResponse(
                HttpStatus status,
                String message,
                HttpServletRequest request,
                Map<String, String> validationErrors
        ) {
                ErrorResponseDTO error = new ErrorResponseDTO(
                        LocalDateTime.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        message,
                        request.getRequestURI(),
                        validationErrors
                );

                return ResponseEntity.status(status).body(error);
        }

        @ExceptionHandler(BadCredentialsCustomException.class)
        public ResponseEntity<ErrorResponseDTO> handleBadCredentialsCustom(
                BadCredentialsCustomException ex,
                HttpServletRequest request
        ) {
        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                request,
                null
        );
        }
}