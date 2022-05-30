package com.ikea.imc.pam.budget.service.service;

import com.ikea.imc.pam.budget.service.repository.model.Budget;
import com.ikea.imc.pam.budget.service.repository.model.Expenses;
import com.ikea.imc.pam.budget.service.service.entity.BudgetAreaParameters;
import com.ikea.imc.pam.budget.service.service.entity.BudgetContent;

import java.util.List;
import java.util.Optional;

public interface BudgetService {

    BudgetContent createBudget(BudgetAreaParameters budgetAreaParameters, Budget budget);

    Optional<BudgetContent> getById(Long budgetId);

    List<BudgetContent> listBudgets(List<Long> projectIds, List<Integer> fiscalYears);

    Optional<BudgetContent> patchBudget(Long budgetId, Integer fiscalYear, Budget updatedBudget);

    Expenses createExpenses(Budget budget, Expenses createExpense);

    List<Expenses> patchExpenses(Budget budget, List<Expenses> updatedExpenses);

    Optional<BudgetContent> deleteById(Long budgetId);
}
