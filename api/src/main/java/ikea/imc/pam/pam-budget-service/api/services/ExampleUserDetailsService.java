package org.imc.pam.boilerplate.api.services;

import java.time.LocalDate;
import java.util.List;
import org.imc.pam.boilerplate.entitymodels.ExampleUserDetails;

public interface ExampleUserDetailsService {

    ExampleUserDetails getSingleExampleUserDetails(Long id);

    ExampleUserDetails createExampleUserDetails(
            ExampleUserDetails exampleUserDetails, Long exampleUserId);

    List<ExampleUserDetails> getExampleUserDetails();

    List<ExampleUserDetails> getExampleUserDetailsByDateOfBirth(List<LocalDate> birthDates);

    ExampleUserDetails replaceExampleUserDetails(ExampleUserDetails exampleUserDetails);

    ExampleUserDetails patchExampleUserDetails(ExampleUserDetails exampleUserDetails);

    ExampleUserDetails deleteExampleUserDetails(Long id);
}
