package org.imc.pam.boilerplate.api.exampleresponsemodels.smokeTest;

import io.swagger.annotations.ApiModel;

@SuppressWarnings("ALL")
@ApiModel(description = "GeneralResponse")
public class GeneralResponse {

    public boolean success = true;
    public int statusCode = 200;
    public String message;
    public Object data;
}
