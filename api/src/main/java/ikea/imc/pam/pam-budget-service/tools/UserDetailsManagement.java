package org.imc.pam.boilerplate.tools;

import java.util.HashMap;
import java.util.Map;
import org.imc.pam.boilerplate.entitymodels.ExampleUserDetails;

public class UserDetailsManagement {

    public Map<String, Object> javaObjectToJsonObject(ExampleUserDetails exampleUserDetails) {
        Map<String, Object> json = new HashMap<>();
        json.put("id", exampleUserDetails.getId());
        json.put("adress", exampleUserDetails.getAdress());
        json.put("phoneNumber", exampleUserDetails.getPhonenumber());
        json.put("dateOfBirth", exampleUserDetails.getDateOfBirth());
        json.put("exampleUserId", exampleUserDetails.getExampleUser().getId());
        return json;
    }
}
