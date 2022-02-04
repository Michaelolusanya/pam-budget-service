package org.imc.pam.boilerplate.api.models;

import java.util.ArrayList;
import java.util.List;

public class BatchCreateResponse {

    private List<Object> success = new ArrayList<>();
    private List<Object> error = new ArrayList<>();

    public List<Object> getSuccess() {
        return success;
    }

    public void setSuccess(List<Object> success) {
        this.success = success;
    }

    public void addSuccess(Object createInformation) {
        success.add(createInformation);
    }

    public List<Object> getError() {
        return error;
    }

    public void setError(List<Object> error) {
        this.error = error;
    }

    public void addError(Object createInformation) {
        error.add(createInformation);
    }

    public boolean containsSuccess() {
        return success != null && !success.isEmpty();
    }
}
