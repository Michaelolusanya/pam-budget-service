package org.imc.pam.boilerplate.repositories;

import org.imc.pam.boilerplate.entitymodels.ExampleUserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExampleUserDetailsRepository extends JpaRepository<ExampleUserDetails, Long> {}
