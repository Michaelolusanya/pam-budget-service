package ikea.imc.pam.budget.service.service;

import ikea.imc.pam.budget.service.configuration.BudgetMapper;
import ikea.imc.pam.budget.service.repository.BudgetRepository;
import ikea.imc.pam.budget.service.repository.model.Budget;
import ikea.imc.pam.budget.service.repository.model.Expenses;
import ikea.imc.pam.budget.service.repository.model.utils.Status;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class BudgetServiceV1 implements BudgetService {

    private final BudgetRepository repository;

    public BudgetServiceV1(BudgetRepository repository) {
        this.repository = repository;
    }

    @Override
    public Budget createBudget(String fiscalYear, Budget budget) {
        return null;
    }

    @Override
    public Optional<Budget> getById(Long budgetId) {
        return repository.findById(budgetId);
    }

    @Override
    public List<Budget> listBudgets(List<Long> projectIds, List<String> fiscalYears) {

        List<Integer> fiscalYearsAsInt =
                fiscalYears == null
                        ? List.of()
                        : fiscalYears.stream().map(BudgetMapper::toFiscalYear).collect(Collectors.toList());

        if ((projectIds == null || projectIds.isEmpty()) && fiscalYearsAsInt.isEmpty()) {
            return repository.getAllActive();
        }

        if (projectIds == null || projectIds.isEmpty()) {
            return repository.getBudgetByFiscalYear(fiscalYearsAsInt);
        }

        if (fiscalYearsAsInt.isEmpty()) {
            return repository.getBudgetByProjectId(projectIds);
        }

        return repository.getBudgetByProjectIdAndFiscalYear(projectIds, fiscalYearsAsInt);
    }

    @Override
    public Optional<Budget> patchBudget(Long budgetId, String fiscalYear, Budget updatedBudget) {

        Optional<Budget> optionalBudget = repository.findById(budgetId);
        if (optionalBudget.isEmpty() || optionalBudget.get().getStatus() == Status.ARCHIVED) {
            return Optional.empty();
        }

        // TODO

        return optionalBudget;
    }

    @Override
    public Optional<Expenses> patchExpense(Budget budget, Long expenseId, Expenses updatedExpenses) {

        if (budget == null || budget.getStatus() == Status.ARCHIVED) {
            return Optional.empty();
        }

        // TODO

        return Optional.empty();
    }

    @Override
    public Optional<Budget> deleteById(Long budgetId) {

        Optional<Budget> optionalBudget = repository.findById(budgetId);
        if (optionalBudget.isEmpty() || optionalBudget.get().getStatus() == Status.ARCHIVED) {
            return Optional.empty();
        }

        Budget budget = optionalBudget.get();
        budget.setStatus(Status.ARCHIVED);
        repository.saveAndFlush(budget);

        return optionalBudget;
    }
}
