package org.imc.pam.boilerplate.api.exampleresponsemodels.exampleFile;

import io.swagger.annotations.ApiModel;
import org.imc.pam.boilerplate.api.models.ExampleFile;

@SuppressWarnings("ALL")
@ApiModel(description = "ExampleFileModelResponse")
public class ExampleFileResponse {

    public boolean success = true;
    public int statusCode = 200;
    public String message;
    public ExampleFile data;
}
