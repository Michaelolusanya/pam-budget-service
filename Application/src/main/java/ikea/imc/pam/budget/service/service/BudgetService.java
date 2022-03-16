package ikea.imc.pam.budget.service.service;

import ikea.imc.pam.budget.service.repository.model.Budget;
import ikea.imc.pam.budget.service.repository.model.Expenses;
import java.util.List;
import java.util.Optional;

public interface BudgetService {

    Budget createBudget(Integer fiscalYear, Budget budget);

    Optional<Budget> getById(Long budgetId);

    List<Budget> listBudgets(List<Long> projectIds, List<Integer> fiscalYears);

    Optional<Budget> patchBudget(Long budgetId, Integer fiscalYear, Budget updatedBudget);

    Expenses createExpenses(Budget budget, Expenses createExpense);

    List<Expenses> patchExpenses(Budget budget, List<Expenses> updatedExpenses);

    Optional<Budget> deleteById(Long budgetId);
}
