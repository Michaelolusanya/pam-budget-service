package ikea.imc.pam.budget.service.service;

import ikea.imc.pam.budget.service.exception.NotFoundException;
import ikea.imc.pam.budget.service.repository.BudgetRepository;
import ikea.imc.pam.budget.service.repository.BudgetVersionRepository;
import ikea.imc.pam.budget.service.repository.ExpensesRepository;
import ikea.imc.pam.budget.service.repository.model.Budget;
import ikea.imc.pam.budget.service.repository.model.BudgetVersion;
import ikea.imc.pam.budget.service.repository.model.Expenses;
import ikea.imc.pam.budget.service.repository.model.utils.Status;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class BudgetServiceV1 implements BudgetService {

    private static final Logger log = LogManager.getLogger(BudgetServiceV1.class);
    private final BudgetRepository repository;
    private final BudgetVersionRepository budgetVersionRepository;
    private final ExpensesRepository expensesRepository;

    public BudgetServiceV1(
            BudgetRepository repository,
            BudgetVersionRepository budgetVersionRepository,
            ExpensesRepository expensesRepository) {
        this.repository = repository;
        this.budgetVersionRepository = budgetVersionRepository;
        this.expensesRepository = expensesRepository;
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

        // Implemented with the assumption that the expenses are not included in the updatedBudget

        log.debug("Patch budget with id {}", budgetId);
        Optional<Budget> optionalBudget = repository.findById(budgetId);
        if (optionalBudget.isEmpty() || optionalBudget.get().getStatus() == Status.ARCHIVED) {
            return Optional.empty();
        }

        Budget budget = Budget.merge(optionalBudget.get(), updatedBudget);
        if (!optionalBudget.get().isEqual(budget)) {
            log.debug("Changes were found and budget with id {} is updated", budgetId);
            budget = repository.saveAndFlush(budget);
        }

        if (fiscalYear != null
                && fiscalYear != 0
                && optionalBudget.get().getBudgetVersion().getFiscalYear() != fiscalYear) {
            // Implemented with the assumption that one budget version only has one budget connected to it
            log.debug("Fiscal year is changed and the budget version is updated for budget with id {}", budgetId);
            BudgetVersion budgetVersion = optionalBudget.get().getBudgetVersion();
            budgetVersion.setFiscalYear(fiscalYear);
            budgetVersionRepository.saveAndFlush(budgetVersion);
        }

        return Optional.of(budget);
    }

    @Override
    public List<Expenses> patchExpenses(Budget budget, List<Expenses> updatedExpenses) {

        if (budget == null || budget.getStatus() == Status.ARCHIVED) {
            throw new NotFoundException(
                    String.format("Budget %d not found", budget != null ? budget.getBudgetId() : 0));
        }
        log.debug("Patching expenses for budget {}", budget.getBudgetId());

        Map<Long, Expenses> expensesMap =
                budget.getExpenses().stream().collect(Collectors.toMap(Expenses::getExpensesId, expenses -> expenses));

        List<Expenses> savedExpenses = updateExpenses(updatedExpenses, expensesMap);

        return expensesMap.keySet().stream()
                .map(id -> toUpdatedExpenses(id, expensesMap, savedExpenses))
                .sorted(Comparator.comparing(Expenses::getExpensesId))
                .collect(Collectors.toList());
    }

    private List<Expenses> updateExpenses(List<Expenses> updatedExpenses, Map<Long, Expenses> expensesMap) {

        List<Expenses> expensesToUpdate = new ArrayList<>();
        for (Expenses updatedExpense : updatedExpenses) {

            Expenses expenses = mergeExpense(expensesMap.get(updatedExpense.getExpensesId()), updatedExpense);
            if (!expensesMap.get(updatedExpense.getExpensesId()).isEqual(expenses)) {
                log.debug("Patching expense {}", expenses.getExpensesId());
                expensesToUpdate.add(expenses);
            }
        }

        return expensesRepository.saveAllAndFlush(expensesToUpdate);
    }

    private Expenses mergeExpense(Expenses currentExpense, Expenses updatedExpense) {

        if (currentExpense == null) {
            throw new NotFoundException(String.format("Expenses with id %d not found", updatedExpense.getExpensesId()));
        }

        return Expenses.merge(currentExpense, updatedExpense);
    }

    private Expenses toUpdatedExpenses(
            Long expenseId, Map<Long, Expenses> allExpenses, List<Expenses> updatedExpenses) {
        Optional<Expenses> optionalExpenses =
                updatedExpenses.stream()
                        .filter(expenses -> Objects.equals(expenses.getExpensesId(), expenseId))
                        .findAny();
        return optionalExpenses.orElseGet(() -> allExpenses.get(expenseId));
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
