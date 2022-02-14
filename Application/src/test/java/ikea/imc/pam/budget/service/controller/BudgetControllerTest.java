package ikea.imc.pam.budget.service.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ikea.imc.pam.budget.service.api.dto.*;
import ikea.imc.pam.budget.service.configuration.ModelMapperConfiguration;
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
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BudgetControllerTest {

    private static final Long BUDGET_ID = 1L;
    private static final Long BUDGET_VERSION_ID = 3L;
    private static final Long PROJECT_ID = 2L;
    private static final Long EXPENSE_ID = 4L, EXPENSE_ID_2 = 112L;
    private static final Long ASSET_TYPE_ID = 5L;
    private static final Long ASSIGNMENT_ID = 5L;

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

    private static final int FISCAL_YEAR = 20;
    private static final String REQUEST_FISCAL_YEAR = "FY20";
    private static final Long ESTIMATED_BUDGET = 100_000L;
    private static final Long ESTIMATED_COST = 100_000L;

    private final BudgetService budgetService = mock(BudgetService.class);
    private final BudgetController controller =
            new BudgetController(budgetService, new ModelMapperConfiguration().modelMapper());

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
            assertEquals("FY" + FISCAL_YEAR, dto.getFiscalYear());
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
            assertEquals(ASSIGNMENT_ID, expenses1.getAssignmentId());
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

        @Test
        void filterOnProjectsAndFiscalYears() {

            // Given
            ArgumentCaptor<List<Long>> argumentCaptorProjectIds = ArgumentCaptor.forClass(List.class);
            ArgumentCaptor<List<String>> argumentCaptorFiscalYears = ArgumentCaptor.forClass(List.class);

            List<Long> projectIds = List.of(1L, 5L);
            List<String> fiscalYears = List.of("FY20", "FY21");
            when(budgetService.listBudgets(argumentCaptorProjectIds.capture(), argumentCaptorFiscalYears.capture()))
                    .thenReturn(List.of());

            // When
            controller.findBudgets(projectIds, fiscalYears);

            // Then
            assertEquals(1L, argumentCaptorProjectIds.getValue().get(0));
            assertEquals(5L, argumentCaptorProjectIds.getValue().get(1));
            assertEquals("FY20", argumentCaptorFiscalYears.getValue().get(0));
            assertEquals("FY21", argumentCaptorFiscalYears.getValue().get(1));
        }

        @Test
        void emptyList() {

            // Given
            List<Long> projectIds = List.of();
            List<String> fiscalYears = List.of();
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
            List<String> fiscalYears = List.of();
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
            ArgumentCaptor<String> inputFiscalYear = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<Budget> inputBudget = ArgumentCaptor.forClass(Budget.class);
            when(budgetService.createBudget(inputFiscalYear.capture(), inputBudget.capture()))
                    .thenReturn(generateBudget(BUDGET_ID));

            // When
            controller.createBudget(requestBudgetDTO);

            // Then
            assertNotNull(inputBudget.getValue());
            Budget budget = inputBudget.getValue();
            assertEquals(REQUEST_FISCAL_YEAR, inputFiscalYear.getValue());
            assertEquals(ESTIMATED_COST, budget.getEstimatedBudget());
        }

        @Test
        void createBudgetOutputTest() {

            // Given
            BudgetDTO requestBudgetDTO = generateRequestBudget();
            ArgumentCaptor<String> inputFiscalYear = ArgumentCaptor.forClass(String.class);
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
            assertEquals("FY" + FISCAL_YEAR, dto.getFiscalYear());
            assertEquals(0, dto.getExpenses().size());
        }
    }

    @Nested
    class UpdateBudgetTest {

        @Test
        void notFoundBudget() {

            // Given
            BudgetDTO requestBudgetDTO = generateRequestBudget();
            ArgumentCaptor<Long> inputBudgetId = ArgumentCaptor.forClass(Long.class);
            ArgumentCaptor<String> inputFiscalYear = ArgumentCaptor.forClass(String.class);
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
            BudgetDTO requestBudgetDTO = generateRequestBudget();
            ArgumentCaptor<Long> inputBudgetId = ArgumentCaptor.forClass(Long.class);
            ArgumentCaptor<String> inputFiscalYear = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<Budget> inputBudget = ArgumentCaptor.forClass(Budget.class);
            when(budgetService.patchBudget(inputBudgetId.capture(), inputFiscalYear.capture(), inputBudget.capture()))
                    .thenReturn(Optional.of(generateBudget(BUDGET_ID)));

            // When
            controller.updateBudget(BUDGET_ID, requestBudgetDTO);

            // Then
            Budget budget = inputBudget.getValue();
            assertEquals(BUDGET_ID, inputBudgetId.getValue());
            assertEquals(REQUEST_FISCAL_YEAR, inputFiscalYear.getValue());
            assertNotNull(budget);
            assertEquals(ESTIMATED_COST, budget.getEstimatedBudget());
            assertEquals(COMDEV_COST, budget.getCostCOMDEV());
        }

        @Test
        void outputValues() {

            // Given
            ArgumentCaptor<Long> inputBudgetId = ArgumentCaptor.forClass(Long.class);
            ArgumentCaptor<String> inputFiscalYear = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<Budget> inputBudget = ArgumentCaptor.forClass(Budget.class);
            when(budgetService.patchBudget(inputBudgetId.capture(), inputFiscalYear.capture(), inputBudget.capture()))
                    .thenReturn(Optional.of(generateBudget(BUDGET_ID)));

            // When
            ResponseEntity<ResponseMessageDTO<BudgetDTO>> response =
                    controller.updateBudget(BUDGET_ID, generateRequestBudget());

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
            assertEquals("FY" + FISCAL_YEAR, dto.getFiscalYear());
            assertEquals(0, dto.getExpenses().size());
        }
    }

    @Nested
    class UpdateExpenseTest {

        @Test
        void notFoundBudget() {

            // Given
            when(budgetService.getById(BUDGET_ID)).thenReturn(Optional.empty());

            // When
            ResponseEntity<ResponseMessageDTO<ExpenseDTO>> response =
                    controller.updateExpense(BUDGET_ID, EXPENSE_ID, generateExpenseDTO());

            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNotNull(response.getBody());
            ResponseMessageDTO<ExpenseDTO> messageDTO = response.getBody();
            assertEquals(404, messageDTO.getStatusCode());
            assertFalse(messageDTO.getSuccess());
            assertEquals("Budget 1 not found", messageDTO.getMessage());
        }

        @Test
        void notFoundExpense() {

            // Given
            Budget budget = generateBudget(BUDGET_ID);
            when(budgetService.getById(BUDGET_ID)).thenReturn(Optional.of(budget));
            when(budgetService.patchExpense(any(), any(), any())).thenReturn(Optional.empty());

            // When
            ResponseEntity<ResponseMessageDTO<ExpenseDTO>> response =
                    controller.updateExpense(BUDGET_ID, EXPENSE_ID, generateExpenseDTO());

            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNotNull(response.getBody());
            ResponseMessageDTO<ExpenseDTO> messageDTO = response.getBody();
            assertEquals(404, messageDTO.getStatusCode());
            assertFalse(messageDTO.getSuccess());
            assertEquals("Expenses 4 not found", messageDTO.getMessage());
        }

        @Test
        void inputValues() {

            // Given
            ArgumentCaptor<Budget> inputBudget = ArgumentCaptor.forClass(Budget.class);
            ArgumentCaptor<Long> inputExpenseId = ArgumentCaptor.forClass(Long.class);
            ArgumentCaptor<Expenses> inputExpenses = ArgumentCaptor.forClass(Expenses.class);
            when(budgetService.getById(BUDGET_ID)).thenReturn(Optional.of(generateBudget(BUDGET_ID)));
            when(budgetService.patchExpense(inputBudget.capture(), inputExpenseId.capture(), inputExpenses.capture()))
                    .thenReturn(Optional.of(generateExpense(generateBudget(BUDGET_ID), EXPENSE_ID)));

            // When
            controller.updateExpense(BUDGET_ID, EXPENSE_ID, generateExpenseDTO());

            // Then
            assertNotNull(inputBudget.getValue());
            assertEquals(BUDGET_ID, inputBudget.getValue().getBudgetId());
            assertEquals(EXPENSE_ID, inputExpenseId.getValue());
            assertNotNull(inputExpenses.getValue());
            Expenses expenses = inputExpenses.getValue();
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
            ArgumentCaptor<Budget> inputBudget = ArgumentCaptor.forClass(Budget.class);
            ArgumentCaptor<Long> inputExpenseId = ArgumentCaptor.forClass(Long.class);
            ArgumentCaptor<Expenses> inputExpenses = ArgumentCaptor.forClass(Expenses.class);
            when(budgetService.getById(BUDGET_ID)).thenReturn(Optional.of(generateBudget(BUDGET_ID)));
            when(budgetService.patchExpense(inputBudget.capture(), inputExpenseId.capture(), inputExpenses.capture()))
                    .thenReturn(Optional.of(generateExpense(generateBudget(BUDGET_ID), EXPENSE_ID)));

            // When
            ResponseEntity<ResponseMessageDTO<ExpenseDTO>> response =
                    controller.updateExpense(BUDGET_ID, EXPENSE_ID, generateExpenseDTO());

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            ResponseMessageDTO<ExpenseDTO> messageDTO = response.getBody();
            assertEquals(200, messageDTO.getStatusCode());
            assertTrue(messageDTO.getSuccess());
            ExpenseDTO dto = messageDTO.getData();
            assertNotNull(dto);
            assertEquals(EXPENSE_ID, dto.getId());
            assertEquals(BUDGET_ID, dto.getBudgetId());
            assertEquals(ASSIGNMENT_ID, dto.getAssignmentId());
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

    private static BudgetDTO generateRequestBudget() {
        BudgetDTO dto = new BudgetDTO();
        dto.setEstimatedCost(ESTIMATED_COST);
        dto.setFiscalYear(REQUEST_FISCAL_YEAR);
        dto.setComdevCost(COMDEV_COST);
        return dto;
    }

    private static ExpenseDTO generateExpenseDTO() {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setComdevFraction(EXPENSE_FRACTION_COMDEV);
        dto.setUnitCost(EXPENSE_COST_PER_UNIT);
        dto.setComdevCost(EXPENSE_COST_COMDEV);
        dto.setWeekCount(EXPENSE_WEEKS);
        dto.setComment(EXPENSE_COMMENT);
        dto.setUnitCount(EXPENSE_UNITS);
        return dto;
    }

    private static Expenses generateExpense(Budget budget, Long id) {
        Expenses expenses = new Expenses();
        expenses.setExpensesId(id);
        expenses.setAssignmentId(ASSIGNMENT_ID);
        expenses.setAssetTypeId(ASSET_TYPE_ID);
        expenses.setComment(EXPENSE_COMMENT);
        expenses.setCost(EXPENSE_COST);
        expenses.setCostCOMDEV(EXPENSE_COST_COMDEV);
        expenses.setCostPerUnit(EXPENSE_COST_PER_UNIT);
        expenses.setPercentCOMDEV(EXPENSE_PERCENT_COMDEV);
        expenses.setUnits(EXPENSE_UNITS);
        expenses.setWeeks(EXPENSE_WEEKS);
        expenses.setInvoicingTypeOption(EXPENSES_INVOICINGTYPEOPTION);
        expenses.setBudget(budget);
        return expenses;
    }

    private static Budget generateBudget(Long id) {
        Budget budget = new Budget();
        budget.setBudgetId(id);
        budget.setEstimatedBudget(ESTIMATED_BUDGET);
        budget.setCostCOMDEV(COMDEV_COST);
        budget.setProjectId(PROJECT_ID);
        budget.setBudgetVersion(generateBudgetVersion());
        budget.setExpenses(List.of());
        return budget;
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
