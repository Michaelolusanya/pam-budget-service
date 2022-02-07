package ikea.imc.pam.budget.service.repository;

import ikea.imc.pam.budget.service.repository.model.BudgetVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetVersionRepository extends JpaRepository<BudgetVersion, Long> {}
