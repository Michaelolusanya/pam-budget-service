package ikea.imc.pam.budget.service.controller.dto;

import ikea.imc.pam.budget.service.exception.BadRequestException;
import ikea.imc.pam.budget.service.exception.NotFoundException;
import ikea.imc.pam.budget.service.exception.RequestException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResponseEntityFactoryTest {

    @Test
    void generateSimpleStringResponse() {
        String data = "Any class type data";
        HttpStatus httpStatus = HttpStatus.OK;

        ResponseEntity<ResponseMessageDTO<String>> responseEntity = ResponseEntityFactory.generateResponse(httpStatus, data);

        assertEquals(httpStatus, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(200, responseEntity.getBody().getStatusCode());
        assertTrue(responseEntity.getBody().getSuccess());
        assertEquals(data, responseEntity.getBody().getData());
        assertEquals(HttpStatus.OK.getReasonPhrase(), responseEntity.getBody().getMessage());
    }

    @Test
    void generateSimpleListResponse() {
        List<Integer> data = new ArrayList<>();
        data.add(1);
        data.add(2);
        HttpStatus httpStatus = HttpStatus.ACCEPTED;

        ResponseEntity<ResponseMessageDTO<List<Integer>>> responseEntity = ResponseEntityFactory.generateResponse(httpStatus, data);

        assertEquals(httpStatus, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(202, responseEntity.getBody().getStatusCode());
        assertTrue(responseEntity.getBody().getSuccess());
        assertEquals(2, responseEntity.getBody().getData().size());
        assertEquals(1, responseEntity.getBody().getData().get(0));
        assertEquals(2, responseEntity.getBody().getData().get(1));
        assertEquals(HttpStatus.ACCEPTED.getReasonPhrase(), responseEntity.getBody().getMessage());
    }

    @Test
    void generateSimpleStringResponseWithMessage() {
        String data = "Any class type data";
        String message = "message1";
        HttpStatus httpStatus = HttpStatus.OK;

        ResponseEntity<ResponseMessageDTO<String>> responseEntity = ResponseEntityFactory.generateResponse(httpStatus, message, data);

        assertEquals(httpStatus, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(200, responseEntity.getBody().getStatusCode());
        assertTrue(responseEntity.getBody().getSuccess());
        assertEquals(data, responseEntity.getBody().getData());
        assertEquals(message, responseEntity.getBody().getMessage());
    }

    @Test
    void generateSimpleListResponseWithMessage() {
        List<Integer> data = new ArrayList<>();
        data.add(1);
        data.add(2);
        String message = "message1";
        HttpStatus httpStatus = HttpStatus.ACCEPTED;

        ResponseEntity<ResponseMessageDTO<List<Integer>>> responseEntity = ResponseEntityFactory.generateResponse(httpStatus, message, data);

        assertEquals(httpStatus, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(202, responseEntity.getBody().getStatusCode());
        assertTrue(responseEntity.getBody().getSuccess());
        assertEquals(2, responseEntity.getBody().getData().size());
        assertEquals(1, responseEntity.getBody().getData().get(0));
        assertEquals(2, responseEntity.getBody().getData().get(1));
        assertEquals(message, responseEntity.getBody().getMessage());
    }

    @Test
    void generateRequestExceptionResponseWithMessage() {
        String message = "not found";
        RequestException exception = new NotFoundException(message);

        ResponseEntity<ResponseMessageDTO<Object>> responseEntity = ResponseEntityFactory.generateResponse(exception, message);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertNull(responseEntity.getBody().getData());
        assertEquals(404, responseEntity.getBody().getStatusCode());
        assertFalse(responseEntity.getBody().getSuccess());
        assertEquals(message, responseEntity.getBody().getMessage());
    }

    @Test
    void generateNotFoundExceptionResponse() {
        String message = "not found";
        NotFoundException exception = new NotFoundException(message);

        ResponseEntity<ResponseMessageDTO<Object>> responseEntity = ResponseEntityFactory.generateResponse(exception);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertNull(responseEntity.getBody().getData());
        assertEquals(404, responseEntity.getBody().getStatusCode());
        assertFalse(responseEntity.getBody().getSuccess());
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), responseEntity.getBody().getMessage());
    }

    @Test
    void generateBadRequestExceptionResponse() {
        String message = "bad input";
        RequestException exception = new BadRequestException(message);

        ResponseEntity<ResponseMessageDTO<Object>> responseEntity = ResponseEntityFactory.generateResponse(exception);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertNull(responseEntity.getBody().getData());
        assertEquals(400, responseEntity.getBody().getStatusCode());
        assertFalse(responseEntity.getBody().getSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), responseEntity.getBody().getMessage());
    }

    @Test
    void generateNullPointerExceptionResponse() {
        NullPointerException exception = new NullPointerException();

        ResponseEntity<ResponseMessageDTO<Object>> responseEntity = ResponseEntityFactory.generateResponse(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertNull(responseEntity.getBody().getData());
        assertEquals(500, responseEntity.getBody().getStatusCode());
        assertFalse(responseEntity.getBody().getSuccess());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), responseEntity.getBody().getMessage());
    }
}
