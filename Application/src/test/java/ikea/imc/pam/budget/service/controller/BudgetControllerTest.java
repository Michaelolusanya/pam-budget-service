package ikea.imc.pam.budget.service.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ikea.imc.pam.budget.service.api.dto.*;
import ikea.imc.pam.budget.service.exception.NotFoundException;
import ikea.imc.pam.budget.service.repository.model.Budget;
import ikea.imc.pam.budget.service.repository.model.BudgetVersion;
import ikea.imc.pam.budget.service.repository.model.Expenses;
import ikea.imc.pam.budget.service.repository.model.utils.InvoicingTypeOption;
import ikea.imc.pam.budget.service.service.BudgetService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class BudgetControllerTest {

    private static final Long BUDGET_ID = 1L;
    private static final Long BUDGET_VERSION_ID = 3L;
    private static final Long PROJECT_ID = 2L;
    private static final Long EXPENSE_ID = 4L, EXPENSE_ID_2 = 112L;
    private static final Long ASSET_TYPE_ID = 5L;

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

    @Mock private BudgetService budgetService;

    @InjectMocks private BudgetController controller;

    @Nested
    class GetBudgetTest {

        @Test
        void notFound() {

            // Given
            when(budgetService.getById(BUDGET_ID)).thenReturn(Optional.empty());

            // When
            ResponseEntity<ResponseMessageDTO<BudgetDTO>> response = controller.getBudget(BUDGET_ID);

            // Then
            ResponseMessageDTO<BudgetDTO> messageDTO = response.getBody();
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNotNull(messageDTO);
            assertNull(messageDTO.getData());
            assertEquals(404, messageDTO.getStatusCode());
            assertEquals("Budget 1 not found", messageDTO.getMessage());
        }

        @Test
        void getBudgetWithoutExpenses() {

            // Given
            Budget budget = generateBudget(BUDGET_ID);
            when(budgetService.getById(BUDGET_ID)).thenReturn(Optional.of(budget));

            // When
            ResponseEntity<ResponseMessageDTO<BudgetDTO>> response = controller.getBudget(BUDGET_ID);

            // Then
            ResponseMessageDTO<BudgetDTO> messageDTO = response.getBody();
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(messageDTO);
            assertEquals(200, messageDTO.getStatusCode());
            assertNotNull(messageDTO.getData());

            BudgetDTO dto = messageDTO.getData();
            assertEquals(BUDGET_ID, dto.getId());
            assertEquals(COMDEV_COST, dto.getComdevCost());
            assertEquals(ESTIMATED_COST, dto.getEstimatedCost());
            assertEquals(FISCAL_YEAR, dto.getFiscalYear());
            assertEquals(0, dto.getExpenses().size());
        }

        @Test
        void getBudgetWithExpenses() {

            // Given
            Budget budget = generateBudget(BUDGET_ID);
            List<Expenses> expenses =
                    List.of(generateExpense(budget, EXPENSE_ID), generateExpense(budget, EXPENSE_ID_2));
            budget.setExpenses(expenses);
            when(budgetService.getById(BUDGET_ID)).thenReturn(Optional.of(budget));

            // When
            ResponseEntity<ResponseMessageDTO<BudgetDTO>> response = controller.getBudget(BUDGET_ID);

            // Then
            ResponseMessageDTO<BudgetDTO> messageDTO = response.getBody();
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(messageDTO);
            assertEquals(200, messageDTO.getStatusCode());
            assertNotNull(messageDTO.getData());

            BudgetDTO dto = messageDTO.getData();
            assertEquals(2, dto.getExpenses().size());
            ExpenseDTO expenses1 = dto.getExpenses().get(0);
            assertEquals(EXPENSE_ID, expenses1.getId());
            assertEquals(EXPENSE_ID_2, dto.getExpenses().get(1).getId());
            assertEquals(BUDGET_ID, expenses1.getBudgetId());
            assertEquals(ASSET_TYPE_ID, expenses1.getAssetTypeId());
            assertEquals(EXPENSE_COMMENT, expenses1.getComment());
            assertEquals(EXPENSE_COST_COMDEV, expenses1.getComdevCost());
            assertEquals(EXPENSE_COST_PER_UNIT, expenses1.getUnitCost());
            assertEquals(EXPENSE_FRACTION_COMDEV, expenses1.getComdevFraction());
            assertEquals(EXPENSE_UNITS, expenses1.getUnitCount());
            assertEquals(EXPENSE_WEEKS, expenses1.getWeekCount());
            assertEquals(EXPENSES_INVOICINGTYPEOPTION.getDescription(), expenses1.getPriceModel());
        }
    }

    @Nested
    class FindBudgetsTest {

        @Captor private ArgumentCaptor<List<Long>> projectIdListsArgumentCaptor;
        @Captor private ArgumentCaptor<List<Integer>> fiscalYearListsArgumentCaptor;

        @Test
        void filterOnProjectsAndFiscalYears() {

            // Given
            List<Long> projectIds = List.of(1L, 5L);
            List<Integer> fiscalYears = List.of(2020, 2021);
            when(budgetService.listBudgets(
                            projectIdListsArgumentCaptor.capture(), fiscalYearListsArgumentCaptor.capture()))
                    .thenReturn(List.of());

            // When
            controller.findBudgets(projectIds, fiscalYears);

            // Then
            assertEquals(1L, projectIdListsArgumentCaptor.getValue().get(0));
            assertEquals(5L, projectIdListsArgumentCaptor.getValue().get(1));
            assertEquals(2020, fiscalYearListsArgumentCaptor.getValue().get(0));
            assertEquals(2021, fiscalYearListsArgumentCaptor.getValue().get(1));
        }

        @Test
        void emptyList() {

            // Given
            List<Long> projectIds = List.of();
            List<Integer> fiscalYears = List.of();
            when(budgetService.listBudgets(projectIds, fiscalYears)).thenReturn(List.of());

            // When
            ResponseEntity<ResponseMessageDTO<List<BudgetDTO>>> response =
                    controller.findBudgets(projectIds, fiscalYears);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getData());

            List<BudgetDTO> dtoList = response.getBody().getData();
            assertEquals(0, dtoList.size());
        }

        @Test
        void listResponse() {

            // Given
            List<Long> projectIds = List.of();
            List<Integer> fiscalYears = List.of();
            Budget budget = generateBudget(BUDGET_ID);
            Budget budget2 = generateBudget(2L);
            budget.setExpenses(List.of(generateExpense(budget, EXPENSE_ID), generateExpense(budget, EXPENSE_ID_2)));
            budget2.setExpenses(List.of(generateExpense(budget2, EXPENSE_ID)));
            when(budgetService.listBudgets(projectIds, fiscalYears)).thenReturn(List.of(budget, budget2));

            // When
            ResponseEntity<ResponseMessageDTO<List<BudgetDTO>>> response =
                    controller.findBudgets(projectIds, fiscalYears);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getData());

            List<BudgetDTO> dtoList = response.getBody().getData();
            assertEquals(2, dtoList.size());
            assertEquals(BUDGET_ID, dtoList.get(0).getId());
            assertEquals(2L, dtoList.get(1).getId());
            assertEquals(2, dtoList.get(0).getExpenses().size());
            assertEquals(1, dtoList.get(1).getExpenses().size());
        }
    }

    @Nested
    class DeleteBudgetTest {

        @Test
        void notFoundBudget() {

            // Given
            when(budgetService.deleteById(BUDGET_ID)).thenReturn(Optional.empty());

            // When
            ResponseEntity<ResponseMessageDTO<BudgetDTO>> response = controller.deleteBudget(BUDGET_ID);

            // Then
            ResponseMessageDTO<BudgetDTO> messageDTO = response.getBody();
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertNotNull(messageDTO);
            assertNull(messageDTO.getData());
            assertEquals(204, messageDTO.getStatusCode());
            assertEquals("No Content", messageDTO.getMessage());
        }

        @Test
        void deleteBudget() {

            // Given
            Budget budget = generateBudget(BUDGET_ID);
            when(budgetService.deleteById(BUDGET_ID)).thenReturn(Optional.of(budget));

            // When
            ResponseEntity<ResponseMessageDTO<BudgetDTO>> response = controller.deleteBudget(BUDGET_ID);

            // Then
            ResponseMessageDTO<BudgetDTO> messageDTO = response.getBody();
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(messageDTO);
            assertNotNull(messageDTO.getData());
            assertEquals(200, messageDTO.getStatusCode());
            assertEquals(BUDGET_ID, messageDTO.getData().getId());
        }
    }

    @Nested
    class CreateBudgetTest {

        @Test
        void createBudgetInputTest() {

            // Given
            BudgetDTO requestBudgetDTO = generateRequestBudget();
            ArgumentCaptor<Integer> inputFiscalYear = ArgumentCaptor.forClass(Integer.class);
            ArgumentCaptor<Budget> inputBudget = ArgumentCaptor.forClass(Budget.class);
            when(budgetService.createBudget(inputFiscalYear.capture(), inputBudget.capture()))
                    .thenReturn(generateBudget(BUDGET_ID));

            // When
            controller.createBudget(requestBudgetDTO);

            // Then
            assertNotNull(inputBudget.getValue());
            Budget budget = inputBudget.getValue();
            assertEquals(FISCAL_YEAR, inputFiscalYear.getValue());
            assertEquals(ESTIMATED_COST, budget.getEstimatedBudget());
        }

        @Test
        void createBudgetOutputTest() {

            // Given
            BudgetDTO requestBudgetDTO = generateRequestBudget();
            ArgumentCaptor<Integer> inputFiscalYear = ArgumentCaptor.forClass(Integer.class);
            ArgumentCaptor<Budget> inputBudget = ArgumentCaptor.forClass(Budget.class);
            when(budgetService.createBudget(inputFiscalYear.capture(), inputBudget.capture()))
                    .thenReturn(generateBudget(BUDGET_ID));

            // When
            ResponseEntity<ResponseMessageDTO<BudgetDTO>> response = controller.createBudget(requestBudgetDTO);

            // Then
            ResponseMessageDTO<BudgetDTO> messageDTO = response.getBody();
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(messageDTO);
            assertEquals(201, messageDTO.getStatusCode());
            assertNotNull(messageDTO.getData());

            BudgetDTO dto = messageDTO.getData();
            assertEquals(BUDGET_ID, dto.getId());
            assertEquals(PROJECT_ID, dto.getProjectId());
            assertEquals(ESTIMATED_COST, dto.getEstimatedCost());
            assertEquals(FISCAL_YEAR, dto.getFiscalYear());
            assertEquals(0, dto.getExpenses().size());
        }
    }

    @Nested
    class UpdateBudgetTest {

        @Test
        void notFoundBudget() {

            // Given
            PatchBudgetDTO requestBudgetDTO = generateRequestPatchBudget();
            ArgumentCaptor<Long> inputBudgetId = ArgumentCaptor.forClass(Long.class);
            ArgumentCaptor<Integer> inputFiscalYear = ArgumentCaptor.forClass(Integer.class);
            ArgumentCaptor<Budget> inputBudget = ArgumentCaptor.forClass(Budget.class);
            when(budgetService.patchBudget(inputBudgetId.capture(), inputFiscalYear.capture(), inputBudget.capture()))
                    .thenReturn(Optional.empty());

            // When
            ResponseEntity<ResponseMessageDTO<BudgetDTO>> response =
                    controller.updateBudget(BUDGET_ID, requestBudgetDTO);

            // Then
            ResponseMessageDTO<BudgetDTO> messageDTO = response.getBody();
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNotNull(messageDTO);
            assertNull(messageDTO.getData());
            assertEquals(404, messageDTO.getStatusCode());
            assertEquals("Budget 1 not found", messageDTO.getMessage());
        }

        @Test
        void inputValues() {

            // Given
            PatchBudgetDTO requestBudgetDTO = generateRequestPatchBudget();
            ArgumentCaptor<Long> inputBudgetId = ArgumentCaptor.forClass(Long.class);
            ArgumentCaptor<Integer> inputFiscalYear = ArgumentCaptor.forClass(Integer.class);
            ArgumentCaptor<Budget> inputBudget = ArgumentCaptor.forClass(Budget.class);
            when(budgetService.patchBudget(inputBudgetId.capture(), inputFiscalYear.capture(), inputBudget.capture()))
                    .thenReturn(Optional.of(generateBudget(BUDGET_ID)));

            // When
            controller.updateBudget(BUDGET_ID, requestBudgetDTO);

            // Then
            Budget budget = inputBudget.getValue();
            assertEquals(BUDGET_ID, inputBudgetId.getValue());
            assertEquals(FISCAL_YEAR, inputFiscalYear.getValue());
            assertNotNull(budget);
            assertEquals(ESTIMATED_COST, budget.getEstimatedBudget());
            assertEquals(COMDEV_COST, budget.getCostCOMDEV());
        }

        @Test
        void outputValues() {

            // Given
            ArgumentCaptor<Long> inputBudgetId = ArgumentCaptor.forClass(Long.class);
            ArgumentCaptor<Integer> inputFiscalYear = ArgumentCaptor.forClass(Integer.class);
            ArgumentCaptor<Budget> inputBudget = ArgumentCaptor.forClass(Budget.class);
            when(budgetService.patchBudget(inputBudgetId.capture(), inputFiscalYear.capture(), inputBudget.capture()))
                    .thenReturn(Optional.of(generateBudget(BUDGET_ID)));

            // When
            ResponseEntity<ResponseMessageDTO<BudgetDTO>> response =
                    controller.updateBudget(BUDGET_ID, generateRequestPatchBudget());

            // Then
            ResponseMessageDTO<BudgetDTO> messageDTO = response.getBody();
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(messageDTO);
            assertEquals(200, messageDTO.getStatusCode());
            BudgetDTO dto = messageDTO.getData();
            assertNotNull(dto);

            assertEquals(BUDGET_ID, dto.getId());
            assertEquals(COMDEV_COST, dto.getComdevCost());
            assertEquals(ESTIMATED_COST, dto.getEstimatedCost());
            assertEquals(FISCAL_YEAR, dto.getFiscalYear());
            assertEquals(0, dto.getExpenses().size());
        }
    }

    @Nested
    class CreateExpenseTest {

        @Captor private ArgumentCaptor<Budget> budgetArgumentCaptor;

        @Captor private ArgumentCaptor<Expenses> expensesArgumentCaptor;

        @Test
        void notFoundBudget() {

            // Given
            when(budgetService.getById(BUDGET_ID)).thenReturn(Optional.empty());

            // When
            ResponseEntity<ResponseMessageDTO<ExpenseDTO>> response =
                    controller.createExpense(BUDGET_ID, generateExpenseDTO());

            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNotNull(response.getBody());
            ResponseMessageDTO<ExpenseDTO> messageDTO = response.getBody();
            assertEquals(404, messageDTO.getStatusCode());
            assertFalse(messageDTO.getSuccess());
            assertEquals("Budget 1 not found", messageDTO.getMessage());
        }

        @Test
        void inputValues() {

            // Given
            when(budgetService.getById(BUDGET_ID)).thenReturn(Optional.of(generateBudget(BUDGET_ID)));

            when(budgetService.createExpenses(budgetArgumentCaptor.capture(), expensesArgumentCaptor.capture()))
                    .thenReturn(generateExpense(generateBudget(BUDGET_ID), EXPENSE_ID));

            // When
            controller.createExpense(BUDGET_ID, generateExpenseDTO());

            // Then
            assertNotNull(budgetArgumentCaptor.getValue());
            assertNotNull(expensesArgumentCaptor.getValue());
            assertEquals(BUDGET_ID, budgetArgumentCaptor.getValue().getBudgetId());
            Expenses expenses = expensesArgumentCaptor.getValue();
            assertEquals(ASSET_TYPE_ID, expenses.getAssetTypeId());
            assertEquals(EXPENSE_PERCENT_COMDEV, expenses.getPercentCOMDEV());
            assertEquals(EXPENSE_COST_PER_UNIT, expenses.getCostPerUnit());
            assertEquals(EXPENSE_COST_COMDEV, expenses.getCostCOMDEV());
            assertEquals(EXPENSE_COMMENT, expenses.getComment());
            assertEquals(EXPENSE_WEEKS, expenses.getWeeks());
            assertEquals(EXPENSE_UNITS, expenses.getUnits());
            assertEquals(EXPENSES_INVOICINGTYPEOPTION, expenses.getInvoicingTypeOption());
        }

        @Test
        void outputValues() {

            // Given
            Budget budget = generateBudget(BUDGET_ID);
            when(budgetService.getById(BUDGET_ID)).thenReturn(Optional.of(budget));
            when(budgetService.createExpenses(any(), any())).thenReturn(generateExpense(budget, EXPENSE_ID));

            // When
            ResponseEntity<ResponseMessageDTO<ExpenseDTO>> response =
                    controller.createExpense(BUDGET_ID, generateExpenseDTO());

            // Then
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            ResponseMessageDTO<ExpenseDTO> messageDTO = response.getBody();
            assertEquals(201, messageDTO.getStatusCode());
            assertTrue(messageDTO.getSuccess());
            assertNotNull(messageDTO.getData());
            ExpenseDTO dto = messageDTO.getData();
            assertEquals(EXPENSE_ID, dto.getId());
            assertEquals(BUDGET_ID, dto.getBudgetId());
            assertEquals(ASSET_TYPE_ID, dto.getAssetTypeId());
            assertEquals(EXPENSE_COMMENT, dto.getComment());
            assertEquals(EXPENSE_COST_COMDEV, dto.getComdevCost());
            assertEquals(EXPENSE_COST_PER_UNIT, dto.getUnitCost());
            assertEquals(EXPENSE_FRACTION_COMDEV, dto.getComdevFraction());
            assertEquals(EXPENSE_UNITS, dto.getUnitCount());
            assertEquals(EXPENSE_WEEKS, dto.getWeekCount());
            assertEquals(EXPENSES_INVOICINGTYPEOPTION.getDescription(), dto.getPriceModel());
        }
    }

    @Nested
    class UpdateExpenseTest {

        @Captor private ArgumentCaptor<Budget> budgetArgumentCaptor;
        @Captor private ArgumentCaptor<List<Expenses>> expensesListsArgumentCaptor;

        @Test
        void notFoundBudget() {

            // Given
            when(budgetService.getById(BUDGET_ID)).thenReturn(Optional.empty());

            // When
            ResponseEntity<ResponseMessageDTO<List<ExpenseDTO>>> response =
                    controller.updateExpense(BUDGET_ID, generateExpenseBatchDTO());

            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNotNull(response.getBody());
            ResponseMessageDTO<List<ExpenseDTO>> messageDTO = response.getBody();
            assertEquals(404, messageDTO.getStatusCode());
            assertFalse(messageDTO.getSuccess());
            assertEquals("Budget 1 not found", messageDTO.getMessage());
        }

        @Test
        void notFoundExpense() {

            // Given
            Budget budget = generateBudget(BUDGET_ID);
            ExpenseBatchDTO expenseBatchDTO = generateExpenseBatchDTO();
            when(budgetService.getById(BUDGET_ID)).thenReturn(Optional.of(budget));
            when(budgetService.patchExpenses(any(), any())).thenThrow(new NotFoundException("Expense 4 not found"));

            // When
            NotFoundException notFoundException =
                    assertThrows(NotFoundException.class, () -> controller.updateExpense(BUDGET_ID, expenseBatchDTO));

            // Then
            assertEquals("Expense 4 not found", notFoundException.getMessage());
        }

        @Test
        void inputValues() {

            // Given
            when(budgetService.getById(BUDGET_ID)).thenReturn(Optional.of(generateBudget(BUDGET_ID)));
            when(budgetService.patchExpenses(budgetArgumentCaptor.capture(), expensesListsArgumentCaptor.capture()))
                    .thenReturn(List.of(generateExpense(generateBudget(BUDGET_ID), EXPENSE_ID)));

            // When
            controller.updateExpense(BUDGET_ID, generateExpenseBatchDTO());

            // Then
            assertNotNull(budgetArgumentCaptor.getValue());
            assertEquals(BUDGET_ID, budgetArgumentCaptor.getValue().getBudgetId());
            assertNotNull(expensesListsArgumentCaptor.getValue());
            List<Expenses> expensesList = expensesListsArgumentCaptor.getValue();
            assertEquals(1, expensesList.size());
            Expenses expenses = expensesList.get(0);
            assertEquals(EXPENSE_PERCENT_COMDEV, expenses.getPercentCOMDEV());
            assertEquals(EXPENSE_COST_PER_UNIT, expenses.getCostPerUnit());
            assertEquals(EXPENSE_COST_COMDEV, expenses.getCostCOMDEV());
            assertEquals(EXPENSE_COMMENT, expenses.getComment());
            assertEquals(EXPENSE_WEEKS, expenses.getWeeks());
            assertEquals(EXPENSE_UNITS, expenses.getUnits());
        }

        @Test
        void outputValues() {

            // Given
            when(budgetService.getById(BUDGET_ID)).thenReturn(Optional.of(generateBudget(BUDGET_ID)));
            when(budgetService.patchExpenses(budgetArgumentCaptor.capture(), expensesListsArgumentCaptor.capture()))
                    .thenReturn(List.of(generateExpense(generateBudget(BUDGET_ID), EXPENSE_ID)));

            // When
            ResponseEntity<ResponseMessageDTO<List<ExpenseDTO>>> response =
                    controller.updateExpense(BUDGET_ID, generateExpenseBatchDTO());

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            ResponseMessageDTO<List<ExpenseDTO>> messageDTO = response.getBody();
            assertEquals(200, messageDTO.getStatusCode());
            assertTrue(messageDTO.getSuccess());
            List<ExpenseDTO> dtos = messageDTO.getData();
            assertNotNull(dtos);
            assertEquals(1, dtos.size());
            ExpenseDTO dto = dtos.get(0);
            assertEquals(EXPENSE_ID, dto.getId());
            assertEquals(BUDGET_ID, dto.getBudgetId());
            assertEquals(ASSET_TYPE_ID, dto.getAssetTypeId());
            assertEquals(EXPENSE_COMMENT, dto.getComment());
            assertEquals(EXPENSE_COST_COMDEV, dto.getComdevCost());
            assertEquals(EXPENSE_COST_PER_UNIT, dto.getUnitCost());
            assertEquals(EXPENSE_FRACTION_COMDEV, dto.getComdevFraction());
            assertEquals(EXPENSE_UNITS, dto.getUnitCount());
            assertEquals(EXPENSE_WEEKS, dto.getWeekCount());
            assertEquals(EXPENSES_INVOICINGTYPEOPTION.getDescription(), dto.getPriceModel());
        }
    }

    private static PatchBudgetDTO generateRequestPatchBudget() {
        return PatchBudgetDTO.builder()
                .estimatedCost(ESTIMATED_COST)
                .fiscalYear(FISCAL_YEAR)
                .comdevCost(COMDEV_COST)
                .build();
    }

    private static BudgetDTO generateRequestBudget() {
        return BudgetDTO.builder()
                .estimatedCost(ESTIMATED_COST)
                .fiscalYear(FISCAL_YEAR)
                .comdevCost(COMDEV_COST)
                .build();
    }

    private static ExpenseBatchDTO generateExpenseBatchDTO() {
        return ExpenseBatchDTO.builder().data(List.of(generatePatchExpenseDTO())).build();
    }

    private static ExpenseDTO generateExpenseDTO() {
        return ExpenseDTO.builder()
                .assetTypeId(ASSET_TYPE_ID)
                .comdevFraction(EXPENSE_FRACTION_COMDEV)
                .comdevCost(EXPENSE_COST_COMDEV)
                .unitCost(EXPENSE_COST_PER_UNIT)
                .unitCount(EXPENSE_UNITS)
                .weekCount(EXPENSE_WEEKS)
                .comment(EXPENSE_COMMENT)
                .priceModel(EXPENSES_INVOICINGTYPEOPTION.getDescription())
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

    private static Expenses generateExpense(Budget budget, Long id) {
        return Expenses.builder()
                .expensesId(id)
                .assetTypeId(ASSET_TYPE_ID)
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
    }

    private static Budget generateBudget(Long id) {
        return Budget.builder()
                .budgetId(id)
                .estimatedBudget(ESTIMATED_BUDGET)
                .costCOMDEV(COMDEV_COST)
                .projectId(PROJECT_ID)
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
}
