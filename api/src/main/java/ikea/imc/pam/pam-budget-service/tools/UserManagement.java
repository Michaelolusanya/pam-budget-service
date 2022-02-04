package org.imc.pam.boilerplate.tools;

import java.util.HashMap;
import java.util.Map;
import org.imc.pam.boilerplate.entitymodels.ExampleUser;

public class UserManagement {

    public Map<String, Object> javaObjectToJsonObject(ExampleUser exampleUser) {
        Map<String, Object> json = new HashMap<>();
        json.put("id", exampleUser.getId());
        json.put("firstName", exampleUser.getFirstName());
        json.put("lastName", exampleUser.getLastName());
        json.put("email", exampleUser.getEmail());
        json.put("exampleUserDetails", exampleUser.getExampleUserDetails());
        return json;
    }
}
