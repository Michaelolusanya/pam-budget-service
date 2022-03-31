package ikea.imc.pam.budget.service.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import ikea.imc.pam.budget.service.client.dto.BudgetDTO;
import ikea.imc.pam.budget.service.client.dto.ExpenseDTO;
import ikea.imc.pam.budget.service.client.dto.PatchBudgetDTO;
import ikea.imc.pam.budget.service.client.dto.PatchExpenseDTO;
import ikea.imc.pam.budget.service.repository.model.Budget;
import ikea.imc.pam.budget.service.repository.model.BudgetVersion;
import ikea.imc.pam.budget.service.repository.model.Expenses;
import ikea.imc.pam.budget.service.repository.model.UserInformation;
import ikea.imc.pam.budget.service.repository.model.utils.InvoicingTypeOption;
import ikea.imc.pam.budget.service.service.UserService;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BudgetMapperTest {

    private static final Long BUDGET_ID = 1L;
    private static final Long BUDGET_VERSION_ID = 3L;
    private static final Long PROJECT_ID = 2L;
    private static final Long EXPENSE_ID = 4L, EXPENSE_ID_2 = 112L;
    private static final Long PRICE_ITEM_ID = 5L;

    private static final String BUDGET_VERSION_NAME = "budname";
    private static final LocalDate BUDGET_VERSION_DATE = LocalDate.of(2020, 3, 1);
    private static final double COMDEV_COST = 50_000d;

    private static final String EXPENSE_COMMENT = "EXPENSE_COMMENT123";
    private static final int EXPENSE_COST = 450;
    private static final double EXPENSE_COST_COMDEV = 400d;
    private static final int EXPENSE_COST_PER_UNIT = 40;
    private static final byte EXPENSE_PERCENT_COMDEV = 80;
    private static final double EXPENSE_FRACTION_COMDEV = EXPENSE_PERCENT_COMDEV / 100d;
    private static final short EXPENSE_UNITS = 41;
    private static final byte EXPENSE_WEEKS = 42;
    private static final InvoicingTypeOption EXPENSES_INVOICINGTYPEOPTION = InvoicingTypeOption.FIXED_PRICE;

    private static final Integer FISCAL_YEAR = 2020;
    private static final Long ESTIMATED_BUDGET = 100_000L;
    private static final Long ESTIMATED_COST = 100_000L;

    private static final String LAST_UPDATED_BY_ID = "user-id-1000-010000";
    private static final String LAST_UPDATED_BY_FIRST_NAME = "Last Updaters first";
    private static final String LAST_UPDATED_BY_SURNAME = "surname";
    private static final String LAST_UPDATED_BY_FULL_NAME = LAST_UPDATED_BY_FIRST_NAME + " " + LAST_UPDATED_BY_SURNAME;

    private static final Date LAST_UPDATED;

    static {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, Calendar.MARCH, 3, 10, 11, 12);
        LAST_UPDATED = calendar.getTime();
    }

    @Mock private UserService userService;

    @InjectMocks private BudgetMapper budgetMapper;

    @Test
    void buildBudgetDTO() {

        // Given
        Budget budget = generateBudget();
        budget.setExpenses(List.of(generateExpense(budget, EXPENSE_ID), generateExpense(budget, EXPENSE_ID_2)));
        when(userService.getUserInformation(LAST_UPDATED_BY_ID)).thenReturn(generateUserInformation());

        // When
        BudgetDTO dto = budgetMapper.buildBudgetDTO(budget);

        // Then
        assertNotNull(dto);
        assertEquals(BUDGET_ID, dto.getId());
        assertEquals(PROJECT_ID, dto.getProjectId());
        assertEquals(FISCAL_YEAR, dto.getFiscalYear());
        assertEquals(ESTIMATED_COST, dto.getEstimatedCost());
        assertEquals(COMDEV_COST, dto.getComdevCost());
        assertEquals(LAST_UPDATED_BY_FULL_NAME, dto.getLastUpdatedByName());
        assertNotNull(dto.getExpenses());
        assertEquals(2, dto.getExpenses().size());
    }

    @Test
    void buildExpenseDTO() {

        // Given
        Budget budget = generateBudget();
        Expenses expenses = generateExpense(budget, EXPENSE_ID);

        // When
        ExpenseDTO dto = budgetMapper.buildExpenseDTO(expenses);

        assertNotNull(dto);
        assertEquals(EXPENSE_ID, dto.getId());
        assertEquals(BUDGET_ID, dto.getBudgetId());
        assertEquals(PRICE_ITEM_ID, dto.getPriceItemId());
        assertEquals(EXPENSE_FRACTION_COMDEV, dto.getComdevFraction());
        assertEquals(EXPENSE_COST_COMDEV, dto.getComdevCost());
        assertEquals(EXPENSE_COST_PER_UNIT, expenses.getCostPerUnit());
        assertEquals(EXPENSE_UNITS, dto.getUnitCount());
        assertEquals(EXPENSE_WEEKS, dto.getWeekCount());
        assertEquals(EXPENSE_COMMENT, dto.getComment());
        assertEquals(EXPENSES_INVOICINGTYPEOPTION.getDescription(), dto.getPriceModel());
    }

    @Test
    void buildBudget_WithoutExpenses() {

        // Given
        BudgetDTO dto = generateBudgetDTO();

        // When
        Budget budget = budgetMapper.buildBudget(dto);

        // Then
        assertEquals(BUDGET_ID, budget.getBudgetId());
        assertEquals(PROJECT_ID, budget.getProjectId());
        assertEquals(ESTIMATED_BUDGET, budget.getEstimatedBudget());
        assertEquals(COMDEV_COST, budget.getCostCOMDEV());
    }

    @Test
    void buildBudget_WithExpenses() {

        // Given
        BudgetDTO dto = generateBudgetDTO();
        dto.setExpenses(List.of(generateExpenseDTO()));

        // When
        Budget budget = budgetMapper.buildBudget(dto);

        // Then
        assertEquals(BUDGET_ID, budget.getBudgetId());
        assertEquals(PROJECT_ID, budget.getProjectId());
        assertEquals(ESTIMATED_BUDGET, budget.getEstimatedBudget());
        assertEquals(COMDEV_COST, budget.getCostCOMDEV());
        assertNotNull(budget.getExpenses());
        assertEquals(1, budget.getExpenses().size());
        assertEquals(EXPENSE_ID, budget.getExpenses().get(0).getExpensesId());
    }

    @Test
    void buildPatchBudget() {

        // Given
        PatchBudgetDTO dto = generatePatchBudgetDTO();

        // When
        Budget budget = budgetMapper.buildBudget(dto);

        // Then
        assertEquals(ESTIMATED_COST, budget.getEstimatedBudget());
        assertEquals(COMDEV_COST, budget.getCostCOMDEV());
    }

    @Test
    void buildExpense() {

        // Given
        ExpenseDTO dto = generateExpenseDTO();

        // When
        Expenses expenses = budgetMapper.buildExpense(dto);

        // Then
        assertEquals(EXPENSE_ID, expenses.getExpensesId());
        assertEquals(PRICE_ITEM_ID, expenses.getPriceItemId());
        assertEquals(EXPENSE_COMMENT, expenses.getComment());
        assertEquals(EXPENSE_COST_COMDEV, expenses.getCostCOMDEV());
        assertEquals(EXPENSE_COST_PER_UNIT, expenses.getCostPerUnit());
        assertEquals(EXPENSE_PERCENT_COMDEV, expenses.getPercentCOMDEV());
        assertEquals(EXPENSE_UNITS, expenses.getUnits());
        assertEquals(EXPENSE_WEEKS, expenses.getWeeks());
        assertEquals(EXPENSES_INVOICINGTYPEOPTION, expenses.getInvoicingTypeOption());
    }

    @Test
    void buildPatchExpense() {

        // Given
        PatchExpenseDTO dto = generatePatchExpenseDTO();

        // When
        Expenses expenses = budgetMapper.buildExpense(dto);

        // Then
        assertEquals(EXPENSE_ID, expenses.getExpensesId());
        assertEquals(EXPENSE_COMMENT, expenses.getComment());
        assertEquals(EXPENSE_COST_COMDEV, expenses.getCostCOMDEV());
        assertEquals(EXPENSE_COST_PER_UNIT, expenses.getCostPerUnit());
        assertEquals(EXPENSE_PERCENT_COMDEV, expenses.getPercentCOMDEV());
        assertEquals(EXPENSE_UNITS, expenses.getUnits());
        assertEquals(EXPENSE_WEEKS, expenses.getWeeks());
    }

    private static BudgetVersion generateBudgetVersion() {
        BudgetVersion budgetVersion = new BudgetVersion();
        budgetVersion.setBudgetVersionId(BUDGET_VERSION_ID);
        budgetVersion.setBudgetVersionName(BUDGET_VERSION_NAME);
        budgetVersion.setBudgetVersionDate(BUDGET_VERSION_DATE);
        budgetVersion.setFiscalYear(FISCAL_YEAR);

        return budgetVersion;
    }

    private static Budget generateBudget() {
        Budget budget =
                Budget.builder()
                        .budgetId(BUDGET_ID)
                        .estimatedBudget(ESTIMATED_BUDGET)
                        .costCOMDEV(COMDEV_COST)
                        .projectId(PROJECT_ID)
                        .budgetVersion(generateBudgetVersion())
                        .expenses(List.of())
                        .build();

        ReflectionTestUtils.setField(budget, "lastUpdatedById", LAST_UPDATED_BY_ID);
        ReflectionTestUtils.setField(budget, "lastUpdated", LAST_UPDATED);

        return budget;
    }

    private static Expenses generateExpense(Budget budget, Long id) {
        Expenses expenses =
                Expenses.builder()
                        .expensesId(id)
                        .priceItemId(PRICE_ITEM_ID)
                        .comment(EXPENSE_COMMENT)
                        .cost(EXPENSE_COST)
                        .costCOMDEV(EXPENSE_COST_COMDEV)
                        .costPerUnit(EXPENSE_COST_PER_UNIT)
                        .percentCOMDEV(EXPENSE_PERCENT_COMDEV)
                        .units(EXPENSE_UNITS)
                        .weeks(EXPENSE_WEEKS)
                        .invoicingTypeOption(EXPENSES_INVOICINGTYPEOPTION)
                        .budget(budget)
                        .build();

        ReflectionTestUtils.setField(expenses, "lastUpdatedById", LAST_UPDATED_BY_ID);
        ReflectionTestUtils.setField(expenses, "lastUpdated", LAST_UPDATED);

        return expenses;
    }

    private static BudgetDTO generateBudgetDTO() {
        return BudgetDTO.builder()
                .id(BUDGET_ID)
                .projectId(PROJECT_ID)
                .fiscalYear(FISCAL_YEAR)
                .estimatedCost(ESTIMATED_COST)
                .comdevCost(COMDEV_COST)
                .lastUpdatedByName(LAST_UPDATED_BY_FULL_NAME)
                .expenses(List.of())
                .build();
    }

    private static ExpenseDTO generateExpenseDTO() {
        return ExpenseDTO.builder()
                .id(EXPENSE_ID)
                .priceItemId(PRICE_ITEM_ID)
                .budgetId(BUDGET_ID)
                .comdevFraction(EXPENSE_FRACTION_COMDEV)
                .comdevCost(EXPENSE_COST_COMDEV)
                .unitCost(EXPENSE_COST_PER_UNIT)
                .unitCount(EXPENSE_UNITS)
                .weekCount(EXPENSE_WEEKS)
                .comment(EXPENSE_COMMENT)
                .priceModel(EXPENSES_INVOICINGTYPEOPTION.getDescription())
                .build();
    }

    private static PatchBudgetDTO generatePatchBudgetDTO() {
        return PatchBudgetDTO.builder()
                .estimatedCost(ESTIMATED_COST)
                .fiscalYear(FISCAL_YEAR)
                .comdevCost(COMDEV_COST)
                .build();
    }

    private static PatchExpenseDTO generatePatchExpenseDTO() {
        return PatchExpenseDTO.builder()
                .id(EXPENSE_ID)
                .comdevFraction(EXPENSE_FRACTION_COMDEV)
                .unitCost(EXPENSE_COST_PER_UNIT)
                .comdevCost(EXPENSE_COST_COMDEV)
                .weekCount(EXPENSE_WEEKS)
                .comment(EXPENSE_COMMENT)
                .unitCount(EXPENSE_UNITS)
                .build();
    }

    private UserInformation generateUserInformation() {

        return new UserInformation() {
            @Override
            public String getId() {
                return LAST_UPDATED_BY_ID;
            }

            @Override
            public String getEmail() {
                return null;
            }

            @Override
            public String getAlias() {
                return null;
            }

            @Override
            public String getFullName() {
                return LAST_UPDATED_BY_FULL_NAME;
            }

            @Override
            public String getFirstName() {
                return LAST_UPDATED_BY_FIRST_NAME;
            }

            @Override
            public String getSurname() {
                return LAST_UPDATED_BY_SURNAME;
            }
        };
    }
}
