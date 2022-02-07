package ikea.imc.pam.budget.service.api.client;

import ikea.imc.pam.budget.service.api.dto.*;

import java.util.List;
import java.util.Optional;

public interface BudgetClient {

    Optional<ResponseBudgetDTO> getBudget(Long id);
    List<ResponseBudgetDTO> findBudgets(List<Long> hfbIds, List<String> fiscalYears);
    ResponseBudgetDTO deleteBudget(Long id);
    ResponseBudgetDTO createBudget(RequestBudgetDTO requestBudgetDTO);
    ResponseBudgetDTO updateBudget(Long id, RequestPartialBudgetDTO requestPartialBudgetDTO);

    ResponseExpenseDTO updateExpense(Long budgetId, Long expenseId, RequestPartialExpenseDTO requestPartialExpenseDTO);
}
