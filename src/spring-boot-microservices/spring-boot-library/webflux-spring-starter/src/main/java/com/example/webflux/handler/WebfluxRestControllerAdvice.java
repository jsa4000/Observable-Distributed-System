package com.example.webflux.handler;

import com.example.webflux.model.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebfluxRestControllerAdvice {

    private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.APPLICATION_JSON;

    @ExceptionHandler(Exception.class)
    public Mono<ErrorMessage> exceptionHandler(Exception ex, ServerWebExchange serverWebExchange) {
        log.debug("WebExceptionHandler, message: {}, type: {}", ex.getMessage(), ex.toString());

        ErrorMessage errorMessage = ErrorMessage.builder()
                .code(INTERNAL_SERVER_ERROR.value())
                .message(INTERNAL_SERVER_ERROR.getReasonPhrase())
                .description(ex.getMessage())
                .build();

        return generateResponse(errorMessage,INTERNAL_SERVER_ERROR, DEFAULT_MEDIA_TYPE, serverWebExchange);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ErrorMessage> exceptionHandler(ResponseStatusException ex, ServerWebExchange serverWebExchange) {
        log.debug("WebExceptionHandler, message: {}, type: {}", ex.getMessage(), ex.toString());

        ErrorMessage errorMessage = ErrorMessage.builder()
                .code(ex.getRawStatusCode())
                .message(ex.getMessage())
                .description(ex.getReason())
                .build();

        return generateResponse(errorMessage,ex.getStatus(), DEFAULT_MEDIA_TYPE, serverWebExchange);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ErrorMessage> webExchangeBindExceptionHandler(WebExchangeBindException ex,
                                                              ServerWebExchange serverWebExchange) {
        log.debug("WebExceptionHandler, message: {}", ex.getMessage());

        ErrorMessage errorMessage = ErrorMessage.builder()
                .code(BAD_REQUEST.value())
                .message(BAD_REQUEST.getReasonPhrase())
                .description(ex.getFieldErrors().stream().map(FieldError::toString).collect(Collectors.joining()))
                .build();

        return generateResponse(errorMessage, BAD_REQUEST, DEFAULT_MEDIA_TYPE, serverWebExchange);
    }

    private Mono<ErrorMessage> generateResponse(ErrorMessage errorMessage, HttpStatus httpStatus, MediaType mediaType,
                                   ServerWebExchange serverWebExchange) {
        serverWebExchange.getResponse().setStatusCode(httpStatus);
        serverWebExchange.getResponse().getHeaders().setContentType(mediaType);
        return Mono.just(errorMessage);
    }

}
