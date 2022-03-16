package ikea.imc.pam.budget.service.api.client;

import ikea.imc.pam.budget.service.api.dto.*;
import java.util.List;
import java.util.Optional;

public interface BudgetClient {

    Optional<BudgetDTO> getBudget(Long id);

    List<BudgetDTO> findBudgets(List<Long> projectIds, List<Integer> fiscalYears);

    BudgetDTO deleteBudget(Long id);

    BudgetDTO createBudget(BudgetDTO requestBudgetDTO);

    BudgetDTO updateBudget(Long id, PatchBudgetDTO requestBudgetDTO);

    ExpenseDTO createExpense(Long budgetId, ExpenseDTO requestExpenseDTO);

    List<ExpenseDTO> updateExpense(Long budgetId, List<PatchExpenseDTO> expenseDTO);
}
