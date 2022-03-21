package ikea.imc.pam.budget.service.configuration;

import ikea.imc.pam.budget.service.client.dto.BudgetDTO;
import ikea.imc.pam.budget.service.client.dto.ExpenseDTO;
import ikea.imc.pam.budget.service.client.dto.PatchBudgetDTO;
import ikea.imc.pam.budget.service.client.dto.PatchExpenseDTO;
import ikea.imc.pam.budget.service.repository.model.Budget;
import ikea.imc.pam.budget.service.repository.model.Expenses;
import ikea.imc.pam.budget.service.repository.model.utils.InvoicingTypeOption;
import java.util.stream.Collectors;
import javax.validation.Valid;

public class BudgetMapper {

    private BudgetMapper() {}

    public static BudgetDTO buildBudgetDTO(Budget budget) {
        return BudgetDTO.builder()
                .id(budget.getBudgetId())
                .projectId(budget.getProjectId())
                .fiscalYear(budget.getBudgetVersion().getFiscalYear())
                .estimatedCost(budget.getEstimatedBudget())
                .comdevCost(budget.getCostCOMDEV())
                .expenses(budget.getExpenses().stream().map(BudgetMapper::buildExpenseDTO).collect(Collectors.toList()))
                .build();
    }

    public static ExpenseDTO buildExpenseDTO(Expenses expenses) {
        return ExpenseDTO.builder()
                .id(expenses.getExpensesId())
                .budgetId(expenses.getBudget().getBudgetId())
                .assetTypeId(expenses.getAssetTypeId())
                .comdevFraction(toFraction(expenses.getPercentCOMDEV()))
                .comdevCost(expenses.getCostCOMDEV())
                .unitCost(expenses.getCostPerUnit())
                .unitCount(expenses.getUnits())
                .weekCount(expenses.getWeeks())
                .comment(expenses.getComment())
                .priceModel(expenses.toInvoicingTypeName())
                .build();
    }

    public static Budget buildBudget(@Valid BudgetDTO dto) {
        Budget budget =
                Budget.builder()
                        .budgetId(dto.getId())
                        .projectId(dto.getProjectId())
                        .estimatedBudget(dto.getEstimatedCost())
                        .costCOMDEV(dto.getComdevCost())
                        .build();

        if (dto.getExpenses() != null) {
            budget.setExpenses(
                    dto.getExpenses().stream()
                            .map(expenseDTO -> buildExpense(budget, expenseDTO))
                            .collect(Collectors.toList()));
        }

        return budget;
    }

    public static Budget buildBudget(PatchBudgetDTO dto) {
        return Budget.builder().estimatedBudget(dto.getEstimatedCost()).costCOMDEV(dto.getComdevCost()).build();
    }

    private static Expenses buildExpense(Budget budget, ExpenseDTO dto) {
        Expenses expenses = buildExpense(dto);
        expenses.setBudget(budget);
        return expenses;
    }

    public static Expenses buildExpense(@Valid ExpenseDTO dto) {
        return Expenses.builder()
                .expensesId(dto.getId())
                .assetTypeId(dto.getAssetTypeId())
                .comment(dto.getComment())
                .costCOMDEV(dto.getComdevCost())
                .costPerUnit(dto.getUnitCost())
                .percentCOMDEV(toPercent(dto.getComdevFraction()))
                .units(dto.getUnitCount())
                .weeks(dto.getWeekCount())
                .invoicingTypeOption(InvoicingTypeOption.get(dto.getPriceModel()))
                .build();
    }

    public static Expenses buildExpense(@Valid PatchExpenseDTO dto) {
        return Expenses.builder()
                .expensesId(dto.getId())
                .comment(dto.getComment())
                .costCOMDEV(dto.getComdevCost())
                .costPerUnit(dto.getUnitCost())
                .percentCOMDEV(toPercent(dto.getComdevFraction()))
                .units(dto.getUnitCount())
                .weeks(dto.getWeekCount())
                .build();
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
