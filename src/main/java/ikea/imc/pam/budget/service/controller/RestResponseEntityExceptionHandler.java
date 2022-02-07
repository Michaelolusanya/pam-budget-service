package ikea.imc.pam.budget.service.controller;

import ikea.imc.pam.budget.service.controller.dto.ResponseEntityFactory;
import ikea.imc.pam.budget.service.controller.dto.ResponseMessageDTO;
import ikea.imc.pam.budget.service.exception.RequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {

    private static final Logger log = LogManager.getLogger(RestResponseEntityExceptionHandler.class);

    @ExceptionHandler(RequestException.class)
    protected ResponseEntity<ResponseMessageDTO<Object>> handleRequestException(RequestException ex) {
        log.warn(ex.getMessage(), ex);
        return ResponseEntityFactory.generateResponse(ex, ex.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    protected ResponseEntity<ResponseMessageDTO<Object>> handleAllOtherExceptions(Throwable ex) {
        log.error(ex);
        return ResponseEntityFactory.generateResponse(ex);
    }
}
