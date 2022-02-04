package org.imc.pam.boilerplate.api.exampleresponsemodels.exampleuserdetails;

import org.imc.pam.boilerplate.api.models.exampleusers.ExampleUserDetailsDTO;

public class ExampleUserDetailsSuccessResponse {

    private ExampleUserDetailsDTO data;
    private Integer statusCode;
    private Boolean success;

    public ExampleUserDetailsDTO getData() {
        return data;
    }

    public void setData(ExampleUserDetailsDTO data) {
        this.data = data;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
