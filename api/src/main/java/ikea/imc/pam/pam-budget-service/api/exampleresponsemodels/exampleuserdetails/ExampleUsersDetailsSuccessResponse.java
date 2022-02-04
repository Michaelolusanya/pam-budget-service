package org.imc.pam.boilerplate.api.exampleresponsemodels.exampleuserdetails;

import java.util.List;
import org.imc.pam.boilerplate.api.models.exampleusers.ExampleUserDetailsDTO;

public class ExampleUsersDetailsSuccessResponse {

    private List<ExampleUserDetailsDTO> data;
    private Integer statusCode;
    private Boolean success;

    public List<ExampleUserDetailsDTO> getData() {
        return data;
    }

    public void setData(List<ExampleUserDetailsDTO> data) {
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
