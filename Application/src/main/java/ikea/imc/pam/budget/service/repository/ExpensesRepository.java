package ikea.imc.pam.budget.service.repository;

import ikea.imc.pam.budget.service.repository.model.Expenses;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpensesRepository extends JpaRepository<Expenses, Long> {}
