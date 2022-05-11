package com.ikea.imc.pam.budget.service.configuration;

import com.ikea.imc.pam.budget.service.client.dto.BudgetDTO;
import com.ikea.imc.pam.budget.service.client.dto.ExpenseDTO;
import com.ikea.imc.pam.budget.service.client.dto.PatchBudgetDTO;
import com.ikea.imc.pam.budget.service.client.dto.PatchExpenseDTO;
import com.ikea.imc.pam.budget.service.repository.model.Budget;
import com.ikea.imc.pam.budget.service.repository.model.Expenses;
import com.ikea.imc.pam.budget.service.repository.model.utils.InvoicingTypeOption;

import javax.validation.Valid;

import com.ikea.imc.pam.budget.service.service.entity.BudgetAreaParameters;
import com.ikea.imc.pam.common.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier()
public class BudgetMapper {

    private final UserService userService;

    public BudgetMapper(UserService userService) {
        this.userService = userService;
    }

    public BudgetDTO buildBudgetDTO(Budget budget) {
        return BudgetDTO.builder()
                .id(budget.getBudgetId())
                .parentType(budget.getBudgetVersion().getBudgetArea().getParentType())
                .parentId(budget.getBudgetVersion().getBudgetArea().getParentId())
                .projectId(budget.getProjectId())
                .fiscalYear(budget.getBudgetVersion().getBudgetArea().getFiscalYear())
                .estimatedCost(budget.getEstimatedBudget())
                .internalCost(budget.getInternalCost())
                .lastUpdatedByName(toUserFullName(budget.getLastUpdatedById()))
                .lastUpdatedAt(budget.getLastUpdated())
                .expenses(budget.getExpenses().stream().map(this::buildExpenseDTO).toList())
                .build();
    }

    public ExpenseDTO buildExpenseDTO(Expenses expenses) {
        return ExpenseDTO.builder()
                .id(expenses.getExpensesId())
                .budgetId(expenses.getBudget().getBudgetId())
                .priceItemId(expenses.getPriceItemId())
                .internalFraction(toFraction(expenses.getInternalPercent()))
                .internalCost(expenses.getInternalCost())
                .unitCost(expenses.getCostPerUnit())
                .unitCount(expenses.getUnits())
                .comment(expenses.getComment())
                .priceModel(expenses.toInvoicingTypeName())
                .build();
    }

    public BudgetAreaParameters buildBudgetAreaParameters(@Valid BudgetDTO budgetDTO) {
        return new BudgetAreaParameters(budgetDTO.getParentType(), budgetDTO.getParentId(), budgetDTO.getFiscalYear());
    }

    public Budget buildBudget(@Valid BudgetDTO dto) {
        Budget budget =
                Budget.builder()
                        .budgetId(dto.getId())
                        .projectId(dto.getProjectId())
                        .estimatedBudget(dto.getEstimatedCost())
                        .internalCost(dto.getInternalCost())
                        .build();

        if (dto.getExpenses() != null) {
            budget.setExpenses(
                    dto.getExpenses().stream()
                            .map(expenseDTO -> buildExpense(budget, expenseDTO))
                            .toList());
        }

        return budget;
    }

    public Budget buildBudget(PatchBudgetDTO dto) {
        return Budget.builder().estimatedBudget(dto.getEstimatedCost()).internalCost(dto.getInternalCost()).build();
    }

    private Expenses buildExpense(Budget budget, ExpenseDTO dto) {
        Expenses expenses = buildExpense(dto);
        expenses.setBudget(budget);
        return expenses;
    }

    public Expenses buildExpense(@Valid ExpenseDTO dto) {
        return Expenses.builder()
                .expensesId(dto.getId())
                .priceItemId(dto.getPriceItemId())
                .comment(dto.getComment())
                .internalCost(dto.getInternalCost())
                .costPerUnit(dto.getUnitCost())
                .internalPercent(toPercent(dto.getInternalFraction()))
                .units(dto.getUnitCount())
                .invoicingTypeOption(InvoicingTypeOption.get(dto.getPriceModel()))
                .build();
    }

    public Expenses buildExpense(@Valid PatchExpenseDTO dto) {
        return Expenses.builder()
                .expensesId(dto.getId())
                .comment(dto.getComment())
                .internalCost(dto.getInternalCost())
                .costPerUnit(dto.getUnitCost())
                .internalPercent(toPercent(dto.getInternalFraction()))
                .units(dto.getUnitCount())
                .build();
    }

    private String toUserFullName(String userId) {
        return userService.getUserInformation(userId).getFullName();
    }

    public static double toFraction(byte percent) {
        return percent / 100d;
    }

    public static Byte toPercent(Double fraction) {
        if (fraction == null) {
            return null;
        }
        return (byte) (fraction * 100);
    }
}
