package org.imc.pam.boilerplate.api.exampleresponsemodels.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FailureResponse {

    private Boolean success;
    private String message;
    private Integer statusCode;

    @JsonProperty("OpenApi-JSON-Documentation")
    private String openApiJsonDocumentation;

    @JsonProperty("OpenApi-Documentation")
    private String openApiDocumentation;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getOpenApiJsonDocumentation() {
        return openApiJsonDocumentation;
    }

    public void setOpenApiJsonDocumentation(String openApiJsonDocumentation) {
        this.openApiJsonDocumentation = openApiJsonDocumentation;
    }

    public String getOpenApiDocumentation() {
        return openApiDocumentation;
    }

    public void setOpenApiDocumentation(String openApiDocumentation) {
        this.openApiDocumentation = openApiDocumentation;
    }
}
