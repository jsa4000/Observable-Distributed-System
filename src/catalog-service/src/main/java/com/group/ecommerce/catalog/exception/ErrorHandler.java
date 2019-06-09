package com.group.ecommerce.catalog.exception;

import com.group.ecommerce.catalog.web.api.model.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDto> processRuntimeException(HttpServletRequest req, RuntimeException ex) throws Exception {
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        ErrorDto errorDto = new ErrorDto()
                .message(ex.getMessage())
                .code(responseStatus != null ? responseStatus.value().value() :
                        HttpStatus.INTERNAL_SERVER_ERROR.value());
        log.debug(errorDto.toString());
        return ResponseEntity
                .status(HttpStatus.valueOf(errorDto.getCode()))
                .body(errorDto);
    }
}
