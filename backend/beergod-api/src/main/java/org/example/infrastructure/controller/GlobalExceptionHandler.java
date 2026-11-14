package org.example.infrastructure.controller;

import org.example.domain.exception.RegraNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity handleRegraNegocioException(RegraNegocioException ex) {
        {
            {

                Map<String, Object> body = new LinkedHashMap<>();
                body.put("timestamp", LocalDateTime.now());
                body.put("status", HttpStatus.BAD_REQUEST.value());
                body.put("erro", "Regra de Negócio Violada");
                body.put("mensagem", ex.getMessage());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
            }
        }
    }
}
