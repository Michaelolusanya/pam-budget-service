package org.imc.pam.boilerplate.api.exampleresponsemodels.common;

import org.imc.pam.boilerplate.api.models.BatchCreateResponse;

public class BatchSuccessResponse {

    private BatchCreateResponse data;
    private Integer statusCode;
    private Boolean success;

    public BatchCreateResponse getData() {
        return data;
    }

    public void setData(BatchCreateResponse data) {
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
