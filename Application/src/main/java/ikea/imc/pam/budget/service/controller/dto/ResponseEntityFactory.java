package ikea.imc.pam.budget.service.controller.dto;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import ikea.imc.pam.budget.service.api.dto.ErrorDTO;
import ikea.imc.pam.budget.service.api.dto.ResponseMessageDTO;
import ikea.imc.pam.budget.service.exception.BadRequestException;
import ikea.imc.pam.budget.service.exception.NotFoundException;
import ikea.imc.pam.budget.service.util.ApplicationContextUtil;
import ikea.imc.pam.budget.service.configuration.properties.OpenApiProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseEntityFactory {

    private ResponseEntityFactory() {}

    public static <T> ResponseEntity<ResponseMessageDTO<T>> generateResponse(HttpStatus httpStatus, T data) {
        return generateResponse(httpStatus, null, data);
    }

    public static <T> ResponseEntity<ResponseMessageDTO<T>> generateResponse(HttpStatus httpStatus, String message, T data) {

        ResponseMessageDTO<T> dto = new ResponseMessageDTO<>();
        dto.setSuccess(isSuccess(httpStatus));
        dto.setStatusCode(httpStatus.value());
        dto.setMessage(getMessage(httpStatus, message));
        dto.setData(data);

        return generateResponseEntity(httpStatus, dto);
    }

    public static <T> ResponseEntity<ResponseMessageDTO<T>> generateResponse(HttpStatus httpStatus) {
        return generateResponse(httpStatus, null, null);
    }

    public static <T> ResponseEntity<ResponseMessageDTO<T>> generateResponseMessage(HttpStatus httpStatus, String message) {
        return generateResponse(httpStatus, message, null);
    }

    public static ResponseEntity<ResponseMessageDTO<Object>> generateResponse(Throwable ex) {
        return generateResponse(ex, null);
    }

    public static ResponseEntity<ResponseMessageDTO<Object>> generateResponse(Throwable ex, String message) {

        HttpStatus httpStatus = getStatusCode(ex);
        ResponseMessageDTO<Object> dto = new ResponseMessageDTO<>();
        dto.setSuccess(isSuccess(httpStatus));
        dto.setStatusCode(httpStatus.value());
        dto.setMessage(getMessage(httpStatus, message, ex));
        dto.setErrors(getErrors(ex));

        if (!isSuccess(httpStatus)) {
            OpenApiProperties properties = ApplicationContextUtil.getBean(OpenApiProperties.class);
            dto.setOpenApiDocumentation(properties.getDocumentation().getOpenApiDocs());
            dto.setOpenApiJSONDocumentation(properties.getDocumentation().getOpenApiJsonDoc());
        }

        return generateResponseEntity(httpStatus, dto);
    }

    private static <T> ResponseEntity<ResponseMessageDTO<T>> generateResponseEntity(HttpStatus httpStatus, ResponseMessageDTO<T> dto) {
        return new ResponseEntity<>(dto, httpStatus);
    }

    private static boolean isSuccess(HttpStatus httpStatus) {
        return httpStatus != null && httpStatus.is2xxSuccessful();
    }

    private static String getMessage(HttpStatus httpStatus, String message, Throwable throwable) {
        if (message != null) {
            return getMessage(httpStatus, message);
        }

        if (throwable instanceof HttpMessageNotReadableException) {
            HttpMessageNotReadableException httpException = (HttpMessageNotReadableException) throwable;
            Throwable rootCause = httpException.getRootCause();
            if (rootCause instanceof JsonParseException) {
                JsonParseException ex = (JsonParseException) rootCause;
                message = String.format("Line: %d, column: %d, exception: %s",
                        ex.getLocation().getLineNr(),
                        ex.getLocation().getColumnNr(),
                        ex.getOriginalMessage());
            }
        }

        return getMessage(httpStatus, message);
    }

    private static String getMessage(HttpStatus httpStatus, String message) {
        return message != null ? message : getMessageFromStatusCode(httpStatus);
    }

    private static String getMessageFromStatusCode(HttpStatus httpStatus) {
        return httpStatus.getReasonPhrase();
    }

    private static List<ErrorDTO> getErrors(Throwable throwable) {

        if (throwable instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) throwable;
            return ex.getAllErrors().stream().map(ResponseEntityFactory::mapErrorDTO).collect(Collectors.toList());
        }

        if (throwable instanceof ConstraintViolationException) {
            ConstraintViolationException ex = (ConstraintViolationException) throwable;
            return ex.getConstraintViolations().stream()
                    .map(ResponseEntityFactory::mapErrorDTO)
                    .collect(Collectors.toList());
        }

        if (throwable instanceof JsonMappingException) {
            return List.of(mapErrorDTO((JsonMappingException) throwable));
        }

        if (throwable instanceof HttpMessageNotReadableException) {
            HttpMessageNotReadableException httpException = (HttpMessageNotReadableException) throwable;
            Throwable rootCause = httpException.getRootCause();
            if (rootCause instanceof JsonMappingException) {
                return List.of(mapErrorDTO((JsonMappingException) rootCause));
            }
        }

        return List.of();
    }

    private static ErrorDTO mapErrorDTO(JsonMappingException exception) {

        ErrorDTO dto = new ErrorDTO();
        dto.setMessage(exception.getOriginalMessage());
        String pointer = exception.getPath().stream()
                .map(reference -> {
                    if (reference.getFrom() instanceof List) {
                        return "[" + reference.getIndex() + "]";
                    }
                    if (reference.getFrom() instanceof Map) {
                        return "[" + reference.getFieldName() + "]";
                    }
                    return "." + reference.getFieldName();
                })
                .collect(Collectors.joining());
        if (pointer.startsWith(".")) {
            pointer = pointer.substring(1);
        }
        dto.setPointer(pointer);

        return dto;
    }

    private static ErrorDTO mapErrorDTO(ObjectError error) {
        ErrorDTO dto = new ErrorDTO();
        dto.setMessage(error.getDefaultMessage());
        if (error instanceof FieldError) {
            dto.setPointer(((FieldError)error).getField());
        } else {
            dto.setPointer(error.getObjectName());
        }
        return dto;
    }

    private static ErrorDTO mapErrorDTO(ConstraintViolation<?> violation) {
        ErrorDTO dto = new ErrorDTO();
        dto.setMessage(violation.getMessage());

        for (Path.Node node : violation.getPropertyPath()) {
            if (ElementKind.PARAMETER == node.getKind()) {
                dto.setPointer(node.getName());
            }
        }

        return dto;
    }

    private static HttpStatus getStatusCode(Throwable ex) {

        if (isBadRequest(ex)) {
            return HttpStatus.BAD_REQUEST;
        }

        if (ex instanceof NotFoundException) {
            return HttpStatus.NOT_FOUND;
        }

        if (ex instanceof MaxUploadSizeExceededException) {
            return HttpStatus.PAYLOAD_TOO_LARGE;
        }

        if (ex instanceof HttpClientErrorException.UnprocessableEntity) {
            return HttpStatus.UNPROCESSABLE_ENTITY;
        }

        if (ex instanceof UnsupportedOperationException) {
            return HttpStatus.NOT_IMPLEMENTED;
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private static boolean isBadRequest(Throwable throwable) {
        if (throwable instanceof BadRequestException) {
            return true;
        }

        if (throwable instanceof MethodArgumentNotValidException) {
            return true;
        }

        if (throwable instanceof ConstraintViolationException) {
            return true;
        }

        if (throwable instanceof HttpMessageNotReadableException) {
            Throwable rootCause = ((HttpMessageNotReadableException) throwable).getRootCause();
            if (rootCause instanceof JsonMappingException) {
                return true;
            }
            return rootCause instanceof JsonParseException;
        }

        return false;
    }
}
