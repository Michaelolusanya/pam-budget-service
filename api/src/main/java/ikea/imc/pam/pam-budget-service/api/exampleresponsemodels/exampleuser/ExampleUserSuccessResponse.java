package org.imc.pam.boilerplate.api.exampleresponsemodels.exampleuser;

import org.imc.pam.boilerplate.api.models.exampleusers.ExampleUserDTO;

public class ExampleUserSuccessResponse {

    private ExampleUserDTO data;
    private Integer statusCode;
    private Boolean success;

    public ExampleUserDTO getData() {
        return data;
    }

    public void setData(ExampleUserDTO data) {
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
