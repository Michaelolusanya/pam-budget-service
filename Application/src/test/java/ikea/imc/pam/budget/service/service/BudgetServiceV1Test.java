package ikea.imc.pam.budget.service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ikea.imc.pam.budget.service.exception.NotFoundException;
import ikea.imc.pam.budget.service.repository.BudgetRepository;
import ikea.imc.pam.budget.service.repository.BudgetVersionRepository;
import ikea.imc.pam.budget.service.repository.ExpensesRepository;
import ikea.imc.pam.budget.service.repository.model.Budget;
import ikea.imc.pam.budget.service.repository.model.BudgetVersion;
import ikea.imc.pam.budget.service.repository.model.Expenses;
import ikea.imc.pam.budget.service.repository.model.utils.InvoicingTypeOption;
import ikea.imc.pam.budget.service.repository.model.utils.Status;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BudgetServiceV1Test {

    private static final Long BUDGET_ID = 1L;
    private static final Long BUDGET_ID_2 = 2L;
    private static final Long BUDGET_VERSION_ID = 3L;
    private static final Long EXPENSE_ID = 5L;
    private static final Long EXPENSE_ID_2 = 6L;

    private static final String BUDGET_VERSION_NAME = "budname";
    private static final LocalDate BUDGET_VERSION_DATE = LocalDate.of(2020, 3, 1);
    private static final Integer FISCAL_YEAR = 2020;

    private static final Long ESTIMATED_BUDGET = 100_000L;
    private static final Long ESTIMATED_BUDGET_2 = 200_000L;
    private static final double COMDEV_COST = 50_000d;
    private static final double COMDEV_COST_2 = 60_000d;
    private static final Long PROJECT_ID = 2L;

    private static final Long ASSET_TYPE_ID = 111L;
    private static final Long ASSET_TYPE_ID_2 = 222L;
    private static final String COMMENT = "comment1";
    private static final String COMMENT_2 = "comment2";
    private static final Integer COST = 123;
    private static final Integer COST_2 = 234;
    private static final Integer COST_PER_UNIT = 23;
    private static final Integer COST_PER_UNIT_2 = 34;
    private static final Byte PERCENT_COMDEV = 10;
    private static final Byte PERCENT_COMDEV_2 = 11;
    private static final Short UNITS = 2;
    private static final Short UNITS_2 = 4;
    private static final Byte WEEKS = 3;
    private static final Byte WEEKS_2 = 5;
    private static final InvoicingTypeOption INVOICING_TYPE_OPTION = InvoicingTypeOption.FIXED_PRICE;
    private static final InvoicingTypeOption INVOICING_TYPE_OPTION_2 = InvoicingTypeOption.HOURLY_PRICE;

    @Mock private BudgetRepository repository;

    @Mock private BudgetVersionRepository budgetVersionRepository;

    @Mock private ExpensesRepository expensesRepository;

    @InjectMocks private BudgetServiceV1 service;

    @Nested
    class CreateBudgetTest {

        @Captor ArgumentCaptor<BudgetVersion> budgetVersionCaptor;
        @Captor ArgumentCaptor<Budget> budgetCaptor;

        @Test
        void validateSavedBudgetVersion() {

            // Given
            Budget budget = generateBudget(BUDGET_ID);
            when(budgetVersionRepository.saveAndFlush(budgetVersionCaptor.capture()))
                    .thenReturn(generateBudgetVersion());
            when(repository.saveAndFlush(any())).thenReturn(budget);

            // When
            service.createBudget(FISCAL_YEAR, budget);

            // Then
            assertNotNull(budgetVersionCaptor.getValue());
            BudgetVersion version = budgetVersionCaptor.getValue();
            assertEquals(FISCAL_YEAR, version.getFiscalYear());
        }

        @Test
        void validateSavedBudget() {

            // Given
            when(budgetVersionRepository.saveAndFlush(any())).thenReturn(generateBudgetVersion());
            when(repository.saveAndFlush(budgetCaptor.capture())).thenReturn(generateBudget(BUDGET_ID));

            // When
            service.createBudget(FISCAL_YEAR, generateBudget(null));

            // Then
            assertNotNull(budgetCaptor.getValue());
            Budget budget = budgetCaptor.getValue();
            assertNull(budget.getBudgetId());
            assertEquals(PROJECT_ID, budget.getProjectId());
            assertEquals(ESTIMATED_BUDGET, budget.getEstimatedBudget());
            assertEquals(COMDEV_COST, budget.getCostCOMDEV());
            assertEquals(Status.ACTIVE, budget.getStatus());
            assertNotNull(budget.getBudgetVersion());
            assertEquals(BUDGET_VERSION_ID, budget.getBudgetVersion().getBudgetVersionId());
        }

        @Test
        void createBudgetWithoutExpenses() {

            // Given
            when(budgetVersionRepository.saveAndFlush(any())).thenReturn(generateBudgetVersion());
            when(repository.saveAndFlush(any())).thenReturn(generateBudget(BUDGET_ID));

            // When
            Budget budget = service.createBudget(FISCAL_YEAR, generateBudget(null));

            // Then
            assertEquals(BUDGET_ID, budget.getBudgetId());
            assertEquals(PROJECT_ID, budget.getProjectId());
            assertEquals(ESTIMATED_BUDGET, budget.getEstimatedBudget());
            assertEquals(COMDEV_COST, budget.getCostCOMDEV());
            assertEquals(Status.ACTIVE, budget.getStatus());
            assertNotNull(budget.getBudgetVersion());
            assertEquals(BUDGET_VERSION_ID, budget.getBudgetVersion().getBudgetVersionId());
            assertEquals(0, budget.getExpenses().size());
        }

        @Test
        void createBudgetWithExpenses() {

            // Given
            Budget inputBudget = generateBudget(BUDGET_ID);
            inputBudget.setExpenses(List.of(generateExpenses(null, inputBudget), generateExpenses2(null, inputBudget)));
            Budget outputBudget = generateBudget(BUDGET_ID);
            outputBudget.setExpenses(
                    List.of(generateExpenses(EXPENSE_ID, outputBudget), generateExpenses2(EXPENSE_ID_2, outputBudget)));
            when(budgetVersionRepository.saveAndFlush(any())).thenReturn(generateBudgetVersion());
            when(repository.saveAndFlush(any())).thenReturn(outputBudget);

            // When
            Budget budget = service.createBudget(FISCAL_YEAR, generateBudget(null));

            // Then
            assertEquals(2, budget.getExpenses().size());
            Expenses expenses = budget.getExpenses().get(0);
            assertEquals(EXPENSE_ID, expenses.getExpensesId());
            assertEquals(EXPENSE_ID_2, budget.getExpenses().get(1).getExpensesId());
            assertEquals(ASSET_TYPE_ID, expenses.getAssetTypeId());
            assertEquals(COMMENT, expenses.getComment());
            assertEquals(COST, expenses.getCost());
            assertEquals(COST_PER_UNIT, expenses.getCostPerUnit());
            assertEquals(PERCENT_COMDEV, expenses.getPercentCOMDEV());
            assertEquals(UNITS, expenses.getUnits());
            assertEquals(WEEKS, expenses.getWeeks());
            assertEquals(INVOICING_TYPE_OPTION, expenses.getInvoicingTypeOption());
        }
    }

    @Nested
    class GetByIdTest {

        @Test
        void notFound() {

            // Given
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.empty());

            // When
            Optional<Budget> optionalBudget = service.getById(BUDGET_ID);

            // Then
            assertTrue(optionalBudget.isEmpty());
        }

        @Test
        void oneFound() {

            // Given
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(generateBudget(BUDGET_ID)));

            // When
            Optional<Budget> optionalBudget = service.getById(BUDGET_ID);

            // Then
            assertTrue(optionalBudget.isPresent());
            Budget budget = optionalBudget.get();
            assertEquals(BUDGET_ID, budget.getBudgetId());
            assertEquals(ESTIMATED_BUDGET, budget.getEstimatedBudget());
            assertEquals(COMDEV_COST, budget.getCostCOMDEV());
            assertEquals(PROJECT_ID, budget.getProjectId());
        }
    }

    @Nested
    class ListBudgetsTest {

        @Test
        void nullInput() {

            // Given
            when(repository.getAllActive()).thenReturn(List.of(generateBudget(BUDGET_ID), generateBudget(BUDGET_ID_2)));

            // When
            List<Budget> budgets = service.listBudgets(null, null);

            // Then
            assertEquals(2, budgets.size());
            assertEquals(BUDGET_ID, budgets.get(0).getBudgetId());
            assertEquals(BUDGET_ID_2, budgets.get(1).getBudgetId());
        }

        @Test
        void emptyInput() {

            // Given
            when(repository.getAllActive()).thenReturn(List.of(generateBudget(BUDGET_ID), generateBudget(BUDGET_ID_2)));

            // When
            List<Budget> budgets = service.listBudgets(List.of(), List.of());

            // Then
            assertEquals(2, budgets.size());
            assertEquals(BUDGET_ID, budgets.get(0).getBudgetId());
            assertEquals(BUDGET_ID_2, budgets.get(1).getBudgetId());
        }

        @Test
        void searchProjectIdAndNullFiscalYear() {

            // Given
            List<Long> projectIds = List.of(PROJECT_ID);
            when(repository.getBudgetByProjectId(projectIds))
                    .thenReturn(List.of(generateBudget(BUDGET_ID), generateBudget(BUDGET_ID_2)));

            // When
            List<Budget> budgets = service.listBudgets(projectIds, null);

            // Then
            assertEquals(2, budgets.size());
            assertEquals(BUDGET_ID, budgets.get(0).getBudgetId());
            assertEquals(BUDGET_ID_2, budgets.get(1).getBudgetId());
        }

        @Test
        void searchFiscalYearAndNullProjectIds() {

            // Given
            List<Integer> fiscalYears = List.of(FISCAL_YEAR);
            when(repository.getBudgetByFiscalYear(List.of(FISCAL_YEAR)))
                    .thenReturn(List.of(generateBudget(BUDGET_ID), generateBudget(BUDGET_ID_2)));

            // When
            List<Budget> budgets = service.listBudgets(null, fiscalYears);

            // Then
            assertEquals(2, budgets.size());
            assertEquals(BUDGET_ID, budgets.get(0).getBudgetId());
            assertEquals(BUDGET_ID_2, budgets.get(1).getBudgetId());
        }

        @Test
        void searchProjectIdAndEmptyFiscalYear() {

            // Given
            List<Long> projectIds = List.of(PROJECT_ID);
            when(repository.getBudgetByProjectId(projectIds))
                    .thenReturn(List.of(generateBudget(BUDGET_ID), generateBudget(BUDGET_ID_2)));

            // When
            List<Budget> budgets = service.listBudgets(projectIds, List.of());

            // Then
            assertEquals(2, budgets.size());
            assertEquals(BUDGET_ID, budgets.get(0).getBudgetId());
            assertEquals(BUDGET_ID_2, budgets.get(1).getBudgetId());
        }

        @Test
        void searchFiscalYearAndEmptyProjectIds() {

            // Given
            List<Integer> fiscalYears = List.of(FISCAL_YEAR);
            when(repository.getBudgetByFiscalYear(List.of(FISCAL_YEAR)))
                    .thenReturn(List.of(generateBudget(BUDGET_ID), generateBudget(BUDGET_ID_2)));

            // When
            List<Budget> budgets = service.listBudgets(List.of(), fiscalYears);

            // Then
            assertEquals(2, budgets.size());
            assertEquals(BUDGET_ID, budgets.get(0).getBudgetId());
            assertEquals(BUDGET_ID_2, budgets.get(1).getBudgetId());
        }

        @Test
        void searchProjectIdsAndFiscalYear() {

            // Given
            List<Long> projectIds = List.of(PROJECT_ID);
            List<Integer> fiscalYears = List.of(FISCAL_YEAR);
            when(repository.getBudgetByProjectIdAndFiscalYear(projectIds, List.of(FISCAL_YEAR)))
                    .thenReturn(List.of(generateBudget(BUDGET_ID), generateBudget(BUDGET_ID_2)));

            // When
            List<Budget> budgets = service.listBudgets(projectIds, fiscalYears);

            // Then
            assertEquals(2, budgets.size());
            assertEquals(BUDGET_ID, budgets.get(0).getBudgetId());
            assertEquals(BUDGET_ID_2, budgets.get(1).getBudgetId());
        }
    }

    @Nested
    class DeleteBudgetTest {

        @Test
        void deleteNotFound() {

            // Given
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.empty());

            // When
            Optional<Budget> optionalBudget = service.deleteById(BUDGET_ID);

            // Then
            assertTrue(optionalBudget.isEmpty());
        }

        @Test
        void deleteAlreadyDeleted() {

            // Given
            Budget budget = generateBudget(BUDGET_ID);
            budget.setStatus(Status.ARCHIVED);
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(budget));

            // When
            Optional<Budget> optionalBudget = service.deleteById(BUDGET_ID);

            // Then
            assertTrue(optionalBudget.isEmpty());
        }

        @Test
        void deleteAsExpected() {

            // Given
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(generateBudget(BUDGET_ID)));

            // When
            Optional<Budget> optionalBudget = service.deleteById(BUDGET_ID);

            // Then
            assertTrue(optionalBudget.isPresent());
            Budget budget = optionalBudget.get();
            assertEquals(BUDGET_ID, budget.getBudgetId());
            assertEquals(Status.ARCHIVED, budget.getStatus());
        }
    }

    @Nested
    class PatchBudgetTest {

        @Test
        void budgetNotFound() {

            // Given
            Budget inputBudget = generateBudget(BUDGET_ID);
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.empty());

            // When
            Optional<Budget> optionalBudget = service.patchBudget(BUDGET_ID, FISCAL_YEAR, inputBudget);

            // Then
            assertTrue(optionalBudget.isEmpty());
        }

        @Test
        void updatedBudgetIsEmpty() {

            // Given
            Budget inputBudget = Budget.builder().build();
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(generateBudget(BUDGET_ID)));

            // When
            Optional<Budget> optionalBudget = service.patchBudget(BUDGET_ID, FISCAL_YEAR, inputBudget);

            // Then
            assertTrue(optionalBudget.isPresent());
            Budget budget = optionalBudget.get();
            assertEquals(BUDGET_ID, budget.getBudgetId());
            assertEquals(PROJECT_ID, budget.getProjectId());
            assertEquals(COMDEV_COST, budget.getCostCOMDEV());
            assertEquals(Status.ACTIVE, budget.getStatus());
        }

        @Test
        void budgetContainsNoUpdate() {

            // Given
            Budget inputBudget = generateBudget(BUDGET_ID);
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(inputBudget));

            // When
            Optional<Budget> optionalBudget = service.patchBudget(BUDGET_ID, FISCAL_YEAR, inputBudget);

            // Then
            assertTrue(optionalBudget.isPresent());
            Budget budget = optionalBudget.get();
            assertEquals(BUDGET_ID, budget.getBudgetId());
            assertEquals(PROJECT_ID, budget.getProjectId());
            assertEquals(COMDEV_COST, budget.getCostCOMDEV());
            assertEquals(Status.ACTIVE, budget.getStatus());
        }

        @Test
        void fiscalYearIsNull() {

            // Given
            Budget inputBudget = generateBudget(BUDGET_ID);
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(inputBudget));

            // When
            Optional<Budget> optionalBudget = service.patchBudget(BUDGET_ID, null, inputBudget);

            // Then
            assertTrue(optionalBudget.isPresent());
            Budget budget = optionalBudget.get();
            assertNotNull(budget.getBudgetVersion());
            assertEquals(FISCAL_YEAR, budget.getBudgetVersion().getFiscalYear());
        }

        @Test
        void fiscalYearIsZero() {

            // Given
            Budget inputBudget = generateBudget(BUDGET_ID);
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(inputBudget));

            // When
            Optional<Budget> optionalBudget = service.patchBudget(BUDGET_ID, 0, inputBudget);

            // Then
            assertTrue(optionalBudget.isPresent());
            Budget budget = optionalBudget.get();
            assertNotNull(budget.getBudgetVersion());
            assertEquals(FISCAL_YEAR, budget.getBudgetVersion().getFiscalYear());
        }

        @Test
        void budgetIsChanged() {

            // Given
            Budget inputBudget = generateBudget(BUDGET_ID);
            inputBudget.setEstimatedBudget(ESTIMATED_BUDGET_2);
            inputBudget.setCostCOMDEV(COMDEV_COST_2);

            ArgumentCaptor<Budget> budgetVersionCapture = ArgumentCaptor.forClass(Budget.class);
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(generateBudget(BUDGET_ID)));
            when(repository.saveAndFlush(budgetVersionCapture.capture())).thenReturn(inputBudget);

            // When
            Optional<Budget> optionalBudget = service.patchBudget(BUDGET_ID, FISCAL_YEAR, inputBudget);

            // Then
            assertTrue(optionalBudget.isPresent());
            Budget budget = optionalBudget.get();
            assertEquals(BUDGET_ID, budget.getBudgetId());
            assertEquals(PROJECT_ID, budget.getProjectId());
            assertEquals(ESTIMATED_BUDGET_2, budget.getEstimatedBudget());
            assertEquals(COMDEV_COST_2, budget.getCostCOMDEV());
            assertEquals(Status.ACTIVE, budget.getStatus());
        }

        @Test
        void fiscalYearIsChanged() {

            // Given
            Budget inputBudget = generateBudget(BUDGET_ID);
            Integer newFiscalYear = 2100;
            BudgetVersion updatedBudgetVersion = generateBudgetVersion();
            updatedBudgetVersion.setFiscalYear(newFiscalYear);
            ArgumentCaptor<BudgetVersion> budgetVersionCapture = ArgumentCaptor.forClass(BudgetVersion.class);
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(inputBudget));
            when(budgetVersionRepository.saveAndFlush(budgetVersionCapture.capture())).thenReturn(updatedBudgetVersion);

            // When
            Optional<Budget> optionalBudget = service.patchBudget(BUDGET_ID, newFiscalYear, inputBudget);

            // Then
            assertTrue(optionalBudget.isPresent());
            Budget budget = optionalBudget.get();
            assertNotNull(budget.getBudgetVersion());
            assertEquals(newFiscalYear, budget.getBudgetVersion().getFiscalYear());
            assertEquals(newFiscalYear, budgetVersionCapture.getValue().getFiscalYear());
        }
    }

    @Nested
    class PatchExpensesTest {

        @Captor private ArgumentCaptor<List<Expenses>> expensesListArgumentCaptor;

        @Test
        void budgetIsNull() {

            // Given
            List<Expenses> inputExpenses = List.of();

            // When
            NotFoundException notFoundException =
                    assertThrows(NotFoundException.class, () -> service.patchExpenses(null, inputExpenses));

            // Then
            assertEquals("Budget 0 not found", notFoundException.getMessage());
        }

        @Test
        void budgetIsDeleted() {

            // Given
            Budget budget = generateBudget(BUDGET_ID);
            budget.setStatus(Status.ARCHIVED);
            List<Expenses> inputExpenses = List.of();

            // When
            NotFoundException notFoundException =
                    assertThrows(NotFoundException.class, () -> service.patchExpenses(budget, inputExpenses));

            // Then
            assertEquals("Budget 1 not found", notFoundException.getMessage());
        }

        @Test
        void expenseIsNotFound() {

            // Given
            Budget budget = generateBudget(BUDGET_ID);
            budget.setExpenses(List.of());
            List<Expenses> inputExpenses = List.of(generateExpenses(EXPENSE_ID, budget));

            // When
            NotFoundException exception =
                    assertThrows(NotFoundException.class, () -> service.patchExpenses(budget, inputExpenses));

            // Then
            assertEquals("Expenses with id 5 not found", exception.getMessage());
        }

        @Test
        void updatedExpensesIsEmptyAndBudgetHasNoExpenses() {

            // Given
            Budget budget = generateBudget(BUDGET_ID);
            List<Expenses> inputExpenses = List.of();
            when(expensesRepository.saveAllAndFlush(any())).thenReturn(List.of());

            // When
            List<Expenses> expenses = service.patchExpenses(budget, inputExpenses);

            // Then
            assertEquals(0, expenses.size());
        }

        @Test
        void updatedExpensesIsEmpty() {

            // Given
            Budget budget = generateBudget(BUDGET_ID);
            budget.setExpenses(List.of(generateExpenses(EXPENSE_ID, budget), generateExpenses(EXPENSE_ID_2, budget)));
            List<Expenses> inputExpenses = List.of();
            when(expensesRepository.saveAllAndFlush(expensesListArgumentCaptor.capture())).thenReturn(List.of());

            // When
            List<Expenses> expenses = service.patchExpenses(budget, inputExpenses);

            // Then
            assertEquals(2, expenses.size());
            assertEquals(0, expensesListArgumentCaptor.getValue().size());
            assertEquals(EXPENSE_ID, expenses.get(0).getExpensesId());
            assertEquals(EXPENSE_ID_2, expenses.get(1).getExpensesId());
        }

        @Test
        void expensesContainsNoUpdate() {

            // Given
            Budget budget = generateBudget(BUDGET_ID);
            List<Expenses> inputExpenses =
                    List.of(generateExpenses(EXPENSE_ID, budget), generateExpenses(EXPENSE_ID_2, budget));
            budget.setExpenses(inputExpenses);
            when(expensesRepository.saveAllAndFlush(expensesListArgumentCaptor.capture())).thenReturn(List.of());

            // When
            List<Expenses> expenses = service.patchExpenses(budget, inputExpenses);

            // Then
            assertEquals(2, expenses.size());
            assertEquals(0, expensesListArgumentCaptor.getValue().size());
            assertEquals(EXPENSE_ID, expenses.get(0).getExpensesId());
            assertEquals(EXPENSE_ID_2, expenses.get(1).getExpensesId());
        }

        @Test
        void oneExpenseIsChanged() {

            // Given
            Budget budget = generateBudget(BUDGET_ID);
            budget.setExpenses(List.of(generateExpenses(EXPENSE_ID, budget), generateExpenses(EXPENSE_ID_2, budget)));
            List<Expenses> inputExpenses =
                    List.of(generateExpenses(EXPENSE_ID, budget), generateExpenses2(EXPENSE_ID_2, budget));
            when(expensesRepository.saveAllAndFlush(expensesListArgumentCaptor.capture()))
                    .thenReturn(List.of(generateExpenses2(EXPENSE_ID_2, budget)));

            // When
            List<Expenses> expenses = service.patchExpenses(budget, inputExpenses);

            // Then
            assertEquals(2, expenses.size());
            assertEquals(1, expensesListArgumentCaptor.getValue().size());
            assertEquals(EXPENSE_ID, expenses.get(0).getExpensesId());

            Expenses updatedExpense = expenses.get(1);
            assertEquals(EXPENSE_ID_2, updatedExpense.getExpensesId());
            assertEquals(ASSET_TYPE_ID_2, updatedExpense.getAssetTypeId());
            assertEquals(COMMENT_2, updatedExpense.getComment());
            assertEquals(COST_2, updatedExpense.getCost());
            assertEquals(COST_PER_UNIT_2, updatedExpense.getCostPerUnit());
            assertEquals(PERCENT_COMDEV_2, updatedExpense.getPercentCOMDEV());
            assertEquals(UNITS_2, updatedExpense.getUnits());
            assertEquals(WEEKS_2, updatedExpense.getWeeks());
            assertEquals(INVOICING_TYPE_OPTION_2, updatedExpense.getInvoicingTypeOption());
        }

        @Test
        void oneExpenseIsChangedSaveValidation() {

            // Given
            Budget budget = generateBudget(BUDGET_ID);
            budget.setExpenses(List.of(generateExpenses(EXPENSE_ID, budget), generateExpenses(EXPENSE_ID_2, budget)));
            List<Expenses> inputExpenses =
                    List.of(generateExpenses(EXPENSE_ID, budget), generateExpenses2(EXPENSE_ID_2, budget));
            when(expensesRepository.saveAllAndFlush(expensesListArgumentCaptor.capture()))
                    .thenReturn(List.of(generateExpenses2(EXPENSE_ID_2, budget)));

            // When
            service.patchExpenses(budget, inputExpenses);

            // Then
            assertEquals(1, expensesListArgumentCaptor.getValue().size());
            Expenses updatedExpense = expensesListArgumentCaptor.getValue().get(0);
            assertEquals(EXPENSE_ID_2, updatedExpense.getExpensesId());
            assertEquals(ASSET_TYPE_ID_2, updatedExpense.getAssetTypeId());
            assertEquals(COMMENT_2, updatedExpense.getComment());
            assertEquals(COST_2, updatedExpense.getCost());
            assertEquals(COST_PER_UNIT_2, updatedExpense.getCostPerUnit());
            assertEquals(PERCENT_COMDEV_2, updatedExpense.getPercentCOMDEV());
            assertEquals(UNITS_2, updatedExpense.getUnits());
            assertEquals(WEEKS_2, updatedExpense.getWeeks());
            assertEquals(INVOICING_TYPE_OPTION_2, updatedExpense.getInvoicingTypeOption());
        }

        @Test
        void allExpensesAreChanged() {

            // Given
            Budget budget = generateBudget(BUDGET_ID);
            budget.setExpenses(List.of(generateExpenses(EXPENSE_ID, budget), generateExpenses(EXPENSE_ID_2, budget)));
            List<Expenses> inputExpenses =
                    List.of(generateExpenses2(EXPENSE_ID, budget), generateExpenses2(EXPENSE_ID_2, budget));
            when(expensesRepository.saveAllAndFlush(expensesListArgumentCaptor.capture())).thenReturn(inputExpenses);

            // When
            List<Expenses> expenses = service.patchExpenses(budget, inputExpenses);

            // Then
            assertEquals(2, expenses.size());
            assertEquals(2, expensesListArgumentCaptor.getValue().size());
            assertEquals(EXPENSE_ID, expenses.get(0).getExpensesId());
            assertEquals(EXPENSE_ID_2, expenses.get(1).getExpensesId());
        }
    }

    private static Budget generateBudget(Long id) {
        return Budget.builder()
                .budgetId(id)
                .estimatedBudget(ESTIMATED_BUDGET)
                .costCOMDEV(COMDEV_COST)
                .projectId(PROJECT_ID)
                .status(Status.ACTIVE)
                .budgetVersion(generateBudgetVersion())
                .expenses(List.of())
                .build();
    }

    private static BudgetVersion generateBudgetVersion() {
        BudgetVersion budgetVersion = new BudgetVersion();
        budgetVersion.setBudgetVersionId(BUDGET_VERSION_ID);
        budgetVersion.setBudgetVersionName(BUDGET_VERSION_NAME);
        budgetVersion.setBudgetVersionDate(BUDGET_VERSION_DATE);
        budgetVersion.setFiscalYear(FISCAL_YEAR);
        return budgetVersion;
    }

    private static Expenses generateExpenses(Long id, Budget budget) {
        return Expenses.builder()
                .expensesId(id)
                .assetTypeId(ASSET_TYPE_ID)
                .comment(COMMENT)
                .cost(COST)
                .costCOMDEV(COMDEV_COST)
                .costPerUnit(COST_PER_UNIT)
                .percentCOMDEV(PERCENT_COMDEV)
                .units(UNITS)
                .weeks(WEEKS)
                .invoicingTypeOption(INVOICING_TYPE_OPTION)
                .budget(budget)
                .build();
    }

    private static Expenses generateExpenses2(Long id, Budget budget) {
        return Expenses.builder()
                .expensesId(id)
                .assetTypeId(ASSET_TYPE_ID_2)
                .comment(COMMENT_2)
                .cost(COST_2)
                .costCOMDEV(COMDEV_COST_2)
                .costPerUnit(COST_PER_UNIT_2)
                .percentCOMDEV(PERCENT_COMDEV_2)
                .units(UNITS_2)
                .weeks(WEEKS_2)
                .invoicingTypeOption(INVOICING_TYPE_OPTION_2)
                .budget(budget)
                .build();
    }
}
