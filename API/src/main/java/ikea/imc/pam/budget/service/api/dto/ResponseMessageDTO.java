package ikea.imc.pam.budget.service.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ResponseMessageDTO<T> {
    private boolean success;
    private int statusCode;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ErrorDTO> errors;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    @JsonProperty("OpenApi-Documentation")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String openApiDocumentation;
    @JsonProperty("OpenApi-JSON-Documentation")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String openApiJSONDocumentation;

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ErrorDTO> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorDTO> errors) {
        this.errors = errors;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getOpenApiDocumentation() {
        return openApiDocumentation;
    }

    public void setOpenApiDocumentation(String openApiDocumentation) {
        this.openApiDocumentation = openApiDocumentation;
    }

    public String getOpenApiJSONDocumentation() {
        return openApiJSONDocumentation;
    }

    public void setOpenApiJSONDocumentation(String openApiJSONDocumentation) {
        this.openApiJSONDocumentation = openApiJSONDocumentation;
    }
}
