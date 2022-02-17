package ikea.imc.pam.budget.service.service;

import ikea.imc.pam.budget.service.repository.BudgetRepository;
import ikea.imc.pam.budget.service.repository.model.Budget;
import ikea.imc.pam.budget.service.repository.model.Expenses;
import ikea.imc.pam.budget.service.repository.model.utils.Status;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class BudgetServiceV1 implements BudgetService {

    private static final Logger log = LogManager.getLogger(BudgetServiceV1.class);
    private final BudgetRepository repository;

    public BudgetServiceV1(BudgetRepository repository) {
        this.repository = repository;
    }

    @Override
    public Budget createBudget(Integer fiscalYear, Budget budget) {
        return null;
    }

    @Override
    public Optional<Budget> getById(Long budgetId) {
        log.debug("Get budget with id {}", budgetId);
        return repository.findById(budgetId);
    }

    @Override
    public List<Budget> listBudgets(List<Long> projectIds, List<Integer> fiscalYears) {

        if ((projectIds == null || projectIds.isEmpty()) && (fiscalYears == null || fiscalYears.isEmpty())) {
            log.debug("List all budgets due to no filters were applied");
            return repository.getAllActive();
        }

        if (projectIds == null || projectIds.isEmpty()) {
            log.debug("List all budgets with fiscalYears {}", fiscalYears);
            return repository.getBudgetByFiscalYear(fiscalYears);
        }

        if (fiscalYears == null || fiscalYears.isEmpty()) {
            log.debug("List all budgets with projectIds {}", projectIds);
            return repository.getBudgetByProjectId(projectIds);
        }

        log.debug("List all budgets with projectIds {} and fiscalYears {}", projectIds, fiscalYears);
        return repository.getBudgetByProjectIdAndFiscalYear(projectIds, fiscalYears);
    }

    @Override
    public Optional<Budget> patchBudget(Long budgetId, Integer fiscalYear, Budget updatedBudget) {

        Optional<Budget> optionalBudget = repository.findById(budgetId);
        if (optionalBudget.isEmpty() || optionalBudget.get().getStatus() == Status.ARCHIVED) {
            return Optional.empty();
        }

        // TODO

        return optionalBudget;
    }

    @Override
    public List<Expenses> patchExpenses(Budget budget, List<Expenses> updatedExpenses) {

        if (budget == null || budget.getStatus() == Status.ARCHIVED) {
            return null;
        }

        // TODO

        return List.of();
    }

    @Override
    public Optional<Budget> deleteById(Long budgetId) {

        log.debug("Delete budget with id {}", budgetId);
        Optional<Budget> optionalBudget = repository.findById(budgetId);
        if (optionalBudget.isEmpty()) {
            log.debug("Budget with id {} doesn't exist", budgetId);
            return Optional.empty();
        }
        if (optionalBudget.get().getStatus() == Status.ARCHIVED) {
            log.debug("Budget with id {} has already been deleted", budgetId);
            return Optional.empty();
        }

        Budget budget = optionalBudget.get();
        budget.setStatus(Status.ARCHIVED);
        repository.saveAndFlush(budget);

        return optionalBudget;
    }
}
