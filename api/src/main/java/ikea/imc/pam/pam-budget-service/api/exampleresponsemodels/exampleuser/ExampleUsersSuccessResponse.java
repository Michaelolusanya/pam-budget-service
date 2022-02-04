package org.imc.pam.boilerplate.api.exampleresponsemodels.exampleuser;

import java.util.List;
import org.imc.pam.boilerplate.api.models.exampleusers.ExampleUserDTO;

public class ExampleUsersSuccessResponse {

    private List<ExampleUserDTO> data;
    private Integer statusCode;
    private Boolean success;

    public List<ExampleUserDTO> getData() {
        return data;
    }

    public void setData(List<ExampleUserDTO> data) {
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
