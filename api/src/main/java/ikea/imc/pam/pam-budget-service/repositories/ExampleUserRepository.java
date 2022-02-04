package org.imc.pam.boilerplate.repositories;

import java.util.Optional;
import org.imc.pam.boilerplate.entitymodels.ExampleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface ExampleUserRepository extends JpaRepository<ExampleUser, Long> {

    Optional<ExampleUser> findByEmail(@Param("email") String email);
}
