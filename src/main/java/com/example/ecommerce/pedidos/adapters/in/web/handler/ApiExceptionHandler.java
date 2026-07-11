package com.example.ecommerce.pedidos.adapters.in.web.handler;

import com.example.ecommerce.pedidos.adapters.in.web.dto.ErroResponse;
import com.example.ecommerce.pedidos.application.exception.MensagemPublicacaoException;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidation(MethodArgumentNotValidException exception) {
        List<String> detalhes = exception.getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ErroResponse response = new ErroResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Requisição inválida",
                detalhes,
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResponse> handleIllegalArgument(IllegalArgumentException exception) {
        ErroResponse response = new ErroResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de regra de domínio",
                List.of(exception.getMessage()),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MensagemPublicacaoException.class)
    public ResponseEntity<ErroResponse> handleMensagemPublicacao(MensagemPublicacaoException exception) {
        ErroResponse response = new ErroResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro ao publicar mensagem",
                List.of(exception.getMessage()),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErroResponse> handleIllegalState(IllegalStateException exception) {
        ErroResponse response = new ErroResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro ao processar operação",
                List.of(exception.getMessage()),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
