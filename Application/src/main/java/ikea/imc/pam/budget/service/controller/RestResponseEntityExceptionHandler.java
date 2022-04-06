package ikea.imc.pam.budget.service.controller;

import ikea.imc.pam.budget.service.client.dto.ResponseMessageDTO;
import ikea.imc.pam.budget.service.controller.dto.ResponseEntityFactory;
import ikea.imc.pam.budget.service.exception.RequestException;
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
