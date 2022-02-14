package ikea.imc.pam.budget.service.service;

import ikea.imc.pam.budget.service.repository.model.Budget;
import ikea.imc.pam.budget.service.repository.model.Expenses;
import java.util.List;
import java.util.Optional;

public interface BudgetService {

    Budget createBudget(String fiscalYear, Budget budget);

    Optional<Budget> getById(Long budgetId);

    List<Budget> listBudgets(List<Long> projectIds, List<String> fiscalYears);

    Optional<Budget> patchBudget(Long budgetId, String fiscalYear, Budget updatedBudget);

    Optional<Expenses> patchExpense(Budget budget, Long expenseId, Expenses updatedExpenses);

    Optional<Budget> deleteById(Long budgetId);
}
