package org.imc.pam.boilerplate.api.configuration;

import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imc.pam.boilerplate.exceptions.ExampleFileAlreadyExistException;
import org.imc.pam.boilerplate.exceptions.ExampleFileNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {

    private static Logger logger = LogManager.getLogger(RestResponseEntityExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleResourceNotFound(Exception ex) {
        ResponseMsg resp = new ResponseMsg(500, ex.getMessage());
        return new ResponseEntity<>(resp.getData(), resp.getHttpStatus());
    }

    @ExceptionHandler(ExampleFileNotFoundException.class)
    protected ResponseEntity<Object> handleExampleFileNotFound(
            ExampleFileNotFoundException ex, WebRequest request) {
        ResponseMsg resp = new ResponseMsg(ex);
        return new ResponseEntity<>(resp.getBody(), resp.getHttpStatus());
    }

    @ExceptionHandler(ExampleFileAlreadyExistException.class)
    protected ResponseEntity<Object> handleExampleFileAlreadyExist(
            ExampleFileAlreadyExistException ex, WebRequest request) {
        ResponseMsg resp = new ResponseMsg(ex);
        resp.getBody()
                .put(
                        "message",
                        "File could not be saved, because file with same name already exist, change name and try again");
        return new ResponseEntity<>(resp.getBody(), resp.getHttpStatus());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxSizeException(
            MaxUploadSizeExceededException ex,
            HttpServletRequest request,
            HttpServletResponse response) {
        ResponseMsg resp = new ResponseMsg(ex);
        return new ResponseEntity<>(resp.getBody(), resp.getHttpStatus());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleRequestDeserializationExceptions(
            HttpMessageNotReadableException ex) {
        String error = ex.getLocalizedMessage();
        ResponseMsg responseMsg = new ResponseMsg(HttpStatus.BAD_REQUEST.value(), error);
        return ResponseEntity.status(responseMsg.getHttpStatus()).body(responseMsg.getBody());
    }

    /**
     * MethodArgumentNotValidException.class is thrown when a @Valid annotated controller argument
     * is invalid and is therefore mapped to a Bad Request response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        String errors =
                ex.getAllErrors().stream()
                        .map(this::getValidationErrorMessage)
                        .collect(Collectors.joining(","));
        ResponseMsg responseMsg = new ResponseMsg(HttpStatus.BAD_REQUEST.value(), errors);
        return ResponseEntity.status(responseMsg.getHttpStatus()).body(responseMsg.getBody());
    }

    private String getValidationErrorMessage(ObjectError error) {
        if (error instanceof FieldError) {
            FieldError fieldError = (FieldError) error;
            return String.format("[%s] %s", fieldError.getField(), fieldError.getDefaultMessage());
        } else {
            return error.getDefaultMessage();
        }
    }
}
