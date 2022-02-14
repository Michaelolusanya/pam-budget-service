package ikea.imc.pam.budget.service.configuration;

import ikea.imc.pam.budget.service.api.dto.BudgetDTO;
import ikea.imc.pam.budget.service.api.dto.ExpenseDTO;
import ikea.imc.pam.budget.service.exception.BadRequestException;
import ikea.imc.pam.budget.service.repository.model.Budget;
import ikea.imc.pam.budget.service.repository.model.Expenses;
import ikea.imc.pam.budget.service.repository.model.utils.InvoicingTypeOption;

import java.util.stream.Collectors;

public class BudgetMapper {

    private BudgetMapper() {}

    public static BudgetDTO buildBudgetDTO(Budget budget) {
        return BudgetDTO
                .builder()
                .id(budget.getBudgetId())
                .projectId(budget.getProjectId())
                .fiscalYear(toFiscalYear(budget.getBudgetVersion().getFiscalYear()))
                .estimatedCost(budget.getEstimatedBudget())
                .comdevCost(budget.getCostCOMDEV())
                .expenses(budget.getExpenses().stream().map(BudgetMapper::buildExpenseDTO).collect(Collectors.toList()))
                .build();
    }

    public static ExpenseDTO buildExpenseDTO(Expenses expenses) {
        return ExpenseDTO
                .builder()
                .id(expenses.getExpensesId())
                .budgetId(expenses.getBudget().getBudgetId())
                .assignmentId(expenses.getAssignmentId())
                .assetTypeId(expenses.getAssetTypeId())
                .comdevFraction(toFraction(expenses.getPercentCOMDEV()))
                .comdevCost(expenses.getCostCOMDEV())
                .unitCost(expenses.getCostPerUnit())
                .unitCount(expenses.getUnits())
                .weekCount(expenses.getWeeks())
                .comment(expenses.getComment())
                .priceModel(expenses.getInvoicingTypeName())
                .build();
    }

    public static Budget buildBudget(BudgetDTO dto) {
        Budget budget = Budget
                .builder()
                .budgetId(dto.getId())
                .projectId(dto.getProjectId())
                .estimatedBudget(dto.getEstimatedCost())
                .costCOMDEV(dto.getComdevCost())
                .build();

        if (dto.getExpenses() != null) {
            budget.setExpenses(dto.getExpenses().stream().map(expenseDTO -> buildExpense(budget, expenseDTO)).collect(Collectors.toList()));
        }

        return budget;
    }

    private static Expenses buildExpense(Budget budget, ExpenseDTO dto) {
        Expenses expenses = buildExpense(dto);
        expenses.setBudget(budget);
        return expenses;
    }

    public static Expenses buildExpense(ExpenseDTO dto) {
        return Expenses
                .builder()
                .expensesId(dto.getId())
                .assignmentId(dto.getAssignmentId())
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

    public static String toFiscalYear(int fiscalYear) {
        return "FY" + fiscalYear;
    }

    public static int toFiscalYear(String fiscalYear) {
        if (fiscalYear.startsWith("FY")) {
            try {
                return Integer.parseInt(fiscalYear.substring(2));
            } catch (NumberFormatException ignored) {
                throw new BadRequestException("Fiscal year " + fiscalYear + " is malformed");
            }
        }

        throw new BadRequestException("Fiscal year " + fiscalYear + " is malformed");
    }

    public static double toFraction(byte percent) {
        return percent / 100d;
    }

    public static byte toPercent(double fraction) {
        return (byte) (fraction * 100);
    }
}
