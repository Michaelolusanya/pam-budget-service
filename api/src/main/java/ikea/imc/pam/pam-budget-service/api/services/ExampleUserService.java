package org.imc.pam.boilerplate.api.services;

import java.util.List;
import java.util.Optional;
import org.imc.pam.boilerplate.api.configuration.ResponseMsg;
import org.imc.pam.boilerplate.entitymodels.ExampleUser;

public interface ExampleUserService {

    ExampleUser getExampleUserById(Long id);

    Optional<ExampleUser> findExampleUserById(Long id);

    ExampleUser createUser(ExampleUser exampleUser);

    ExampleUser updateExampleUser(ExampleUser newExampleUser);

    ResponseMsg deleteExampleUser(Long id);

    List<ExampleUser> getExampleUsers();

    List<ExampleUser> getExampleUsersByEmail(List<String> emails);

    ExampleUser patchExampleUser(ExampleUser exampleUser);
}
