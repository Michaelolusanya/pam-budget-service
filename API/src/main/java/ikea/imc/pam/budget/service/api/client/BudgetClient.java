package ikea.imc.pam.budget.service.api.client;

import ikea.imc.pam.budget.service.api.dto.*;
import java.util.List;
import java.util.Optional;

public interface BudgetClient {

    Optional<BudgetDTO> getBudget(Long id);

    List<BudgetDTO> findBudgets(List<Long> hfbIds, List<String> fiscalYears);

    BudgetDTO deleteBudget(Long id);

    BudgetDTO createBudget(BudgetDTO requestBudgetDTO);

    BudgetDTO updateBudget(Long id, BudgetDTO requestBudgetDTO);

    ExpenseDTO updateExpense(Long budgetId, Long expenseId, ExpenseDTO expenseDTO);
}
