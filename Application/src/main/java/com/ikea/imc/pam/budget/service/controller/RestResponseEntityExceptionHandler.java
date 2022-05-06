package com.ikea.imc.pam.budget.service.controller;

import com.ikea.imc.pam.budget.service.controller.dto.ResponseEntityFactory;
import com.ikea.imc.pam.budget.service.exception.RequestException;
import com.ikea.imc.pam.common.dto.ResponseMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class RestResponseEntityExceptionHandler {

    @ExceptionHandler(RequestException.class)
    protected ResponseEntity<ResponseMessageDTO<Object>> handleRequestException(RequestException ex) {
        log.warn(ex.getMessage(), ex);
        return ResponseEntityFactory.generateResponse(ex, ex.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    protected ResponseEntity<ResponseMessageDTO<Object>> handleAllOtherExceptions(Throwable ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntityFactory.generateResponse(ex);
    }
}
