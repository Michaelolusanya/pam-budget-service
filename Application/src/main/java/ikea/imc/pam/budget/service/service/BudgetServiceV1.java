package ikea.imc.pam.budget.service.service;

import ikea.imc.pam.budget.service.repository.model.Budget;
import ikea.imc.pam.budget.service.repository.model.Expenses;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class BudgetServiceV1 implements BudgetService {

    public BudgetServiceV1() {}

    @Override
    public Budget createBudget(String fiscalYear, Budget budget) {
        return null;
    }

    @Override
    public Optional<Budget> getById(Long budgetId) {
        return Optional.empty();
    }

    @Override
    public List<Budget> listBudgets(List<Long> projectIds, List<String> fiscalYears) {
        return List.of();
    }

    @Override
    public Optional<Budget> patchBudget(Long budgetId, String fiscalYear, Budget updatedBudget) {
        return Optional.empty();
    }

    @Override
    public Optional<Expenses> patchExpense(Budget budget, Long expenseId, Expenses updatedExpenses) {
        return Optional.empty();
    }

    @Override
    public Optional<Budget> deleteById(Long budgetId) {
        return Optional.empty();
    }
}
