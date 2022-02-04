package org.imc.pam.boilerplate.repositories;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.Optional;
import org.imc.pam.boilerplate.entitymodels.ExampleUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExampleUserRepositoryTest {

    private static final String EXISTING_USER_USERNAME = "existing username";
    private static final String EXISTING_USER_FIRSTNAME = "existing firstname";
    private static final String EXISTING_USER_LASTNAME = "existing lastname";
    private static final long EXISTING_USER_ID = 1L;
    private static final String NEW_USER_USERNAME = "new username";
    private static final String NEW_USER_FIRSTNAME = "new firstname";
    private static final String NEW_USER_LASTNAME = "new lastname";

    @Autowired private ExampleUserRepository repository;

    @BeforeAll
    public void setup() {
        ExampleUser user = new ExampleUser();
        user.setId(EXISTING_USER_ID);
        user.setEmail(EXISTING_USER_USERNAME);
        user.setFirstName(EXISTING_USER_FIRSTNAME);
        user.setLastName(EXISTING_USER_LASTNAME);

        repository.save(user);
    }

    @Test
    public void saveNewUser() {
        // Given
        ExampleUser user = new ExampleUser();
        user.setEmail(NEW_USER_USERNAME);
        user.setFirstName(NEW_USER_FIRSTNAME);
        user.setLastName(NEW_USER_LASTNAME);

        // When
        ExampleUser createdUser = repository.save(user);

        // Then
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getId()).isGreaterThan(0);
    }

    @Test
    public void findUser() {
        // Given
        long id = EXISTING_USER_ID;

        // When
        Optional<ExampleUser> user = repository.findById(id);

        // Then
        assertThat(user.isPresent()).isTrue();
    }

    @Test
    public void violateUniqueConstraint() {
        // Given
        ExampleUser user = new ExampleUser();
        user.setEmail(EXISTING_USER_USERNAME);
        user.setFirstName(NEW_USER_FIRSTNAME);
        user.setLastName(NEW_USER_LASTNAME);

        // When
        DataIntegrityViolationException thrown =
                Assertions.assertThrows(
                        DataIntegrityViolationException.class, () -> repository.save(user));

        // Then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getMessage()).isNotNull();
    }
}
