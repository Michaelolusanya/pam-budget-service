package com.ikea.imc.pam.budget.service.service;

import com.ikea.imc.pam.budget.service.exception.NotFoundException;
import com.ikea.imc.pam.budget.service.repository.BudgetRepository;
import com.ikea.imc.pam.budget.service.repository.BudgetVersionRepository;
import com.ikea.imc.pam.budget.service.repository.ExpensesRepository;
import com.ikea.imc.pam.budget.service.repository.model.Budget;
import com.ikea.imc.pam.budget.service.repository.model.BudgetArea;
import com.ikea.imc.pam.budget.service.repository.model.BudgetVersion;
import com.ikea.imc.pam.budget.service.repository.model.Expenses;
import com.ikea.imc.pam.budget.service.repository.model.utils.Status;
import com.ikea.imc.pam.budget.service.service.entity.BudgetAreaParameters;
import com.ikea.imc.pam.budget.service.service.entity.BudgetContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BudgetServiceV1 implements BudgetService {
    
    private final BudgetAreaService budgetAreaService;
    private final BudgetRepository repository;
    private final BudgetVersionRepository budgetVersionRepository;
    private final ExpensesRepository expensesRepository;
    
    public BudgetServiceV1(
        BudgetAreaService budgetAreaService,
        BudgetRepository repository,
        BudgetVersionRepository budgetVersionRepository,
        ExpensesRepository expensesRepository) {
        this.budgetAreaService = budgetAreaService;
        this.repository = repository;
        this.budgetVersionRepository = budgetVersionRepository;
        this.expensesRepository = expensesRepository;
    }
    
    @Override
    public BudgetContent createBudget(BudgetAreaParameters budgetAreaParameters, Budget budget) {
        
        BudgetArea budgetArea = budgetAreaService.putBudgetArea(BudgetArea.toBudgetArea(budgetAreaParameters));
        
        BudgetVersion budgetVersion = BudgetVersion.builder()
            .budgetArea(budgetArea)
            .budgetVersionName(budgetAreaParameters.fiscalYear().toString())
            .budgetVersionDate(LocalDate.of(budgetAreaParameters.fiscalYear(), 1, 1))
            .build();
        budgetVersion = budgetVersionRepository.saveAndFlush(budgetVersion);
        
        budget.setBudgetVersion(budgetVersion);
        budget.setStatus(Status.ACTIVE);
        
        return new BudgetContent(repository.saveAndFlush(budget));
    }
    
    @Override
    public Optional<BudgetContent> getById(Long budgetId) {
        log.debug("Get budget with id {}", budgetId);
        return repository.findById(budgetId).map(BudgetContent::new);
    }
    
    @Override
    public List<BudgetContent> listBudgets(List<Long> projectIds, List<Integer> fiscalYears) {
        
        if ((projectIds == null || projectIds.isEmpty()) && (fiscalYears == null || fiscalYears.isEmpty())) {
            log.debug("List all budgets due to no filters were applied");
            return repository.getAllActive().stream().map(BudgetContent::new).toList();
        }
        
        if (projectIds == null || projectIds.isEmpty()) {
            log.debug("List all budgets with fiscalYears {}", fiscalYears);
            return repository.getBudgetByFiscalYear(fiscalYears).stream().map(BudgetContent::new).toList();
        }
        
        if (fiscalYears == null || fiscalYears.isEmpty()) {
            log.debug("List all budgets with projectIds {}", projectIds);
            return repository.getBudgetByProjectId(projectIds).stream().map(BudgetContent::new).toList();
        }
        
        log.debug("List all budgets with projectIds {} and fiscalYears {}", projectIds, fiscalYears);
        return repository.getBudgetByProjectIdAndFiscalYear(projectIds, fiscalYears)
            .stream()
            .map(BudgetContent::new)
            .toList();
    }
    
    @Override
    public Optional<BudgetContent> patchBudget(Long budgetId, Integer fiscalYear, Budget updatedBudget) {
        
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
        
        if (isBudgetAreaChanged(optionalBudget.get(), fiscalYear)) {
            // Implemented with the assumption that one budget version only has one budget connected to it
            log.debug("Fiscal year is changed and the budget version is updated for budget with id {}", budgetId);
            BudgetVersion budgetVersion = optionalBudget.get().getBudgetVersion();
            
            BudgetAreaParameters budgetAreaParameters =
                new BudgetAreaParameters(budgetVersion.getBudgetArea().getParentType(),
                    budgetVersion.getBudgetArea().getParentId(),
                    fiscalYear
                );
            BudgetArea budgetArea = budgetAreaService.putBudgetArea(BudgetArea.toBudgetArea(budgetAreaParameters));
            
            budgetVersion.setBudgetArea(budgetArea);
            budgetVersionRepository.saveAndFlush(budgetVersion);
        }
        
        return Optional.of(new BudgetContent(budget));
    }
    
    private boolean isBudgetAreaChanged(Budget budget, Integer fiscalYear) {
        if (fiscalYear == null || fiscalYear == 0) {
            return false;
        }
        
        return !Objects.equals(budget.getBudgetVersion().getBudgetArea().getFiscalYear(), fiscalYear);
    }
    
    @Override
    public Expenses createExpenses(Budget budget, Expenses createExpense) {
        
        if (budget == null || budget.getStatus() == Status.ARCHIVED) {
            throw new NotFoundException(String.format(
                "Budget %d not found",
                budget != null ? budget.getBudgetId() : 0
            ));
        }
        log.debug("Creating expenses for budget {}", budget.getBudgetId());
        
        createExpense.setBudget(budget);
        Expenses expenses = expensesRepository.saveAndFlush(createExpense);
        log.debug("Created expenses with id {} for budget {} ", expenses.getExpensesId(), budget.getBudgetId());
        return expenses;
    }
    
    @Override
    public List<Expenses> patchExpenses(Budget budget, List<Expenses> updatedExpenses) {
        
        if (budget == null || budget.getStatus() == Status.ARCHIVED) {
            throw new NotFoundException(String.format(
                "Budget %d not found",
                budget != null ? budget.getBudgetId() : 0
            ));
        }
        log.debug("Patching expenses for budget {}", budget.getBudgetId());
        
        Map<Long, Expenses> expensesMap =
            budget.getExpenses().stream().collect(Collectors.toMap(Expenses::getExpensesId, expenses -> expenses));
        
        List<Expenses> savedExpenses = updateExpenses(updatedExpenses, expensesMap);
        
        return expensesMap.keySet()
            .stream()
            .map(id -> toUpdatedExpenses(id, expensesMap, savedExpenses))
            .sorted(Comparator.comparing(Expenses::getExpensesId))
            .toList();
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
            updatedExpenses.stream().filter(expenses -> Objects.equals(expenses.getExpensesId(), expenseId)).findAny();
        return optionalExpenses.orElseGet(() -> allExpenses.get(expenseId));
    }
    
    @Override
    public Optional<BudgetContent> deleteById(Long budgetId) {
        
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
        
        return Optional.of(new BudgetContent(budget));
    }
}
