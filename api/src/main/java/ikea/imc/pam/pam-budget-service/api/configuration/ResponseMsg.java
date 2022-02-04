package org.imc.pam.boilerplate.api.configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.imc.pam.boilerplate.exceptions.ExampleFileAlreadyExistException;
import org.imc.pam.boilerplate.exceptions.ExampleFileNotFoundException;
import org.imc.pam.boilerplate.exceptions.ExampleUserAlreadyExistException;
import org.imc.pam.boilerplate.exceptions.ExampleUserDetailsAlreadyExistsException;
import org.imc.pam.boilerplate.exceptions.ExampleUserDetailsNotFoundException;
import org.imc.pam.boilerplate.exceptions.ExampleUserNotFoundException;
import org.imc.pam.boilerplate.properties.OAuthProperties;
import org.imc.pam.boilerplate.tools.ApplicationContextUtil;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException.UnprocessableEntity;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

public class ResponseMsg {
    private int statusCode;
    private HttpStatus httpStatus;
    private Object data;
    private String message;
    private Map<String, Object> respMap;
    // private Map<String, Object> warningLists;
    private Map<Integer, String> messageMap;

    public ResponseMsg(int statusCode) {
        this.statusCode = statusCode;
        this.respMap = setupRespMsg(statusCode, null, null);
    }

    public ResponseMsg(Exception ex) {
        this.statusCode = getStatusCodeByException(ex);
        this.respMap = setupRespMsg(statusCode, null, null);
    }

    public ResponseMsg(Exception ex, String message) {
        this.statusCode = getStatusCodeByException(ex);
        this.message = message;
        this.respMap = setupRespMsg(statusCode, message, null);
    }

    public ResponseMsg(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.respMap = setupRespMsg(statusCode, message, null);
    }

    public ResponseMsg(int statusCode, String message, Object data) {
        this.data = data;
        this.statusCode = statusCode;
        this.message = message;
        this.respMap = setupRespMsg(statusCode, message, data);
    }

    public ResponseMsg(int statusCode, Object data) {
        this.data = data;
        this.statusCode = statusCode;
        this.respMap = setupRespMsg(statusCode, null, data);
    }

    private boolean isSuccess(int statusCode) {
        return statusCode >= 200 && statusCode <= 206;
    }

    private Map<String, Object> setupRespMsg(int statusCode, String message, Object data) {
        setupMessageMap();
        setHttpStatus(statusCode);
        boolean failedWithNoMessage = (!isSuccess(statusCode) && message == null);
        boolean neitherMessageOrData = (data == null && message == null);
        this.statusCode = statusCode;
        respMap = new HashMap<>();
        respMap.put("success", isSuccess(statusCode));
        respMap.put("statusCode", this.statusCode);
        if (message != null) {
            respMap.put("message", message);
        } else {
            respMap.put("message", getStandardStatusMessage(statusCode));
        }
        if (data != null) {
            respMap.put("data", data);
        }
        if (failedWithNoMessage || neitherMessageOrData || message == null) {
            respMap.put("message", getStandardStatusMessage(statusCode));
        }
        if (!isSuccess(statusCode)) {
            OAuthProperties openApiProperties =
                    ApplicationContextUtil.getBean(OAuthProperties.class);
            respMap.put(
                    "OpenApi-Documentation", openApiProperties.getDocumentation().getOpenApiDocs());
            respMap.put(
                    "OpenApi-JSON-Documentation",
                    openApiProperties.getDocumentation().getOpenApiJsonDoc());
        }

        return respMap;
    }

    private String getStandardStatusMessage(int statusCode) {
        if (messageMap.get(statusCode) != null) {
            return messageMap.get(statusCode);
        } else {
            return "Unknown Internal Server Error has occured";
        }
    }

    private void setHttpStatus(int statusCode) {
        this.httpStatus = HttpStatus.valueOf(statusCode);
    }

    public Map<String, Object> getBody() {
        return this.respMap;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @SuppressWarnings("unused")
    public Object getData() {
        return this.data;
    }

    @SuppressWarnings("unused")
    public String getMessage() {
        return this.message;
    }

    private int getStatusCodeByException(Exception ex) {
        if (ex instanceof IOException) {
            return 500;
        }
        if (ex instanceof JSONException) {
            return 422;
        }
        if (ex instanceof UnprocessableEntity) {
            return 422;
        }
        if (ex instanceof ExampleFileNotFoundException
                || ex instanceof ExampleUserDetailsNotFoundException
                || ex instanceof ExampleUserNotFoundException) {
            return 404;
        }
        if (ex instanceof ExampleFileAlreadyExistException
                || ex instanceof ExampleUserDetailsAlreadyExistsException
                || ex instanceof ExampleUserAlreadyExistException) {
            return 400;
        }
        if (ex instanceof MaxUploadSizeExceededException) {
            return 413;
        }
        return 400;
    }

    private void setupMessageMap() {
        messageMap = new HashMap<>();
        messageMap.put(200, "OK");
        messageMap.put(201, "Created");
        messageMap.put(
                202,
                "The request has been accepted for processing, but the processing has not been completed.");
        messageMap.put(
                400,
                "Bad Request - The Server could not understand the request due to invalid syntax.");
        messageMap.put(
                401,
                "Unauthenticated - Client must be authenticated in order to get the requested response");
        messageMap.put(
                403,
                "Unauthorized - The authenticated client is not authorized to get the requested response");
        messageMap.put(404, "Not Found - The Server could not find the requested resource");
        messageMap.put(
                405,
                "Method Not Allowed - The request method is known by the server but has been disabled and cannot be used");
        messageMap.put(409, "Conflict");
        messageMap.put(413, "Request Entity Too Large");
        messageMap.put(
                414,
                "URI Too Long - The URI requested by the client is longer than the server is willing to interpret");
        messageMap.put(
                415,
                "Unsupported Media Type - The media format of the requested data is not supported by the server, so the server is rejecting the request");
        messageMap.put(417, "Expectation Failed");
        messageMap.put(422, "Unprocessable Entity - The data client requested does not exist");
        messageMap.put(
                429,
                "Too Many Requests - The client has sent too many requests in a given amount of time ('rate limiting')");
        messageMap.put(431, "Request Header Fields Too Large");
        messageMap.put(500, "Internal Server Error - An internal server error has occured");
        messageMap.put(502, "Bad Gateway");
        messageMap.put(503, "Service Unavailable - The server cannot handle the request");
    }
}
