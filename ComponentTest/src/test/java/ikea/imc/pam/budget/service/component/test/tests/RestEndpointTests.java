package ikea.imc.pam.budget.service.component.test.tests;

import static ikea.imc.pam.budget.service.client.dto.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

import ikea.imc.pam.budget.service.client.BudgetClient;
import ikea.imc.pam.budget.service.client.dto.*;
import ikea.imc.pam.budget.service.client.exception.BudgetClientRequestException;
import ikea.imc.pam.budget.service.component.test.AbstractBaseTest;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class RestEndpointTests extends AbstractBaseTest {

    @Autowired BudgetClient budgetClient;

    @Test
    void getExistingBudget() {
        // GIVEN (Budget already exists in budget-service)
        Long budgetId =
                budgetClient.createBudget(minimalBudgetBuilder(testData.projectId, MINIMUM_YEAR).build()).getId();

        // WHEN (REST call to budget-service to get budget)
        var res = budgetClient.getBudget(budgetId).orElseThrow();

        // THEN (Response from budget-service with data for the budget)
        assertNotNull(res);
        assertEquals(testData.projectId, res.getProjectId());
        assertEquals(MINIMUM_YEAR, res.getFiscalYear());
    }

    @Test
    void getExistingBudgetWithExpenses() {
        // GIVEN (Budget already exists in budget-service)
        Long budgetId =
                budgetClient.createBudget(minimalBudgetBuilder(testData.projectId, MINIMUM_YEAR).build()).getId();
        Long expenseId = budgetClient.createExpense(budgetId, minimalExpense()).getId();

        // WHEN (REST call to budget-service to get budget)
        var res = budgetClient.getBudget(budgetId).orElseThrow();

        // THEN (Response from budget-service with data for the budget)
        assertNotNull(res);
        assertEquals(testData.projectId, res.getProjectId());
        assertEquals(MINIMUM_YEAR, res.getFiscalYear());
        assertEquals(1, res.getExpenses().size());
        assertEquals(expenseId, res.getExpenses().get(0).getId());
    }

    @Test
    void getMissingBudget() {
        // GIVEN (Budget doesn't exist in budget-service)

        // WHEN (REST call to budget-service to get nonexistent budget)
        BudgetClientRequestException exception =
                assertThrows(BudgetClientRequestException.class, () -> budgetClient.getBudget(testData.projectId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertNotNull(exception.getBody());
        assertEquals(404, exception.getBody().getStatusCode());
    }

    @Test
    void findBudgetById() {
        // GIVEN (Budget already exists in budget-service)
        budgetClient.createBudget(minimalBudgetBuilder(testData.projectId, MINIMUM_YEAR).build());

        // WHEN (REST call to budget-service to find the budget)
        var res = budgetClient.findBudgets(List.of(testData.projectId), List.of());

        // THEN (Response from budget-service with a list of sole budget)
        assertEquals(1, res.size());
    }

    @Test
    void findMultipleBudgets() {
        // GIVEN (Budgets already exists in budget-service)
        budgetClient.createBudget(minimalBudgetBuilder(testData.projectId, MINIMUM_YEAR).build());
        budgetClient.createBudget(minimalBudgetBuilder(testData.projectId, MINIMUM_YEAR).build());
        budgetClient.createBudget(minimalBudgetBuilder(testData.projectId, MINIMUM_YEAR).build());

        // WHEN (REST call to budget-service to find budgets)
        var res = budgetClient.findBudgets(List.of(testData.projectId), List.of(MINIMUM_YEAR));

        // THEN (Response from budget-service with a list of the budgets)
        assertEquals(3, res.size());
    }

    @Test
    void deleteBudget() {
        // GIVEN (Budget already exists in budget-service)
        Long budgetId =
                budgetClient.createBudget(minimalBudgetBuilder(testData.projectId, MINIMUM_YEAR).build()).getId();

        // WHEN (REST call to budget-service to delete budget)
        var res = budgetClient.deleteBudget(budgetId);

        // THEN (Response from budget-service with data for the deleted budget)
        assertNotNull(res);
        assertEquals(budgetId, res.getId());
    }

    @Test
    void deleteMissingBudget() {
        // GIVEN (Budget doesn't exist in budget-service)

        // WHEN (REST call to budget-service to delete budget)
        var res = budgetClient.deleteBudget(-1L);

        // THEN (Response from budget-service with no data)
        assertNull(res);
    }

    @Test
    void createBudget() {
        // GIVEN (Budget doesn't exist in budget-service)

        // WHEN (REST call to budget-service to create new budget)
        var res = budgetClient.createBudget(minimalBudgetBuilder(testData.projectId, MINIMUM_YEAR).build());

        // THEN (Response from budget-service with data about the created budget)
        assertNotNull(res);
        assertEquals(testData.projectId, res.getProjectId());
        assertEquals(MINIMUM_YEAR, res.getFiscalYear());
        assertTrue(res.getExpenses().isEmpty());
    }

    @Test
    void createBudgetWithExpenses() {
        // GIVEN (Budget with expenses already exists in budget-service)
        BudgetDTO.BudgetDTOBuilder builder = minimalBudgetBuilder(testData.projectId, MINIMUM_YEAR);
        builder.expenses(List.of(minimalExpense(), minimalExpense()));

        // WHEN (REST call to budget-service to create new budget)
        var res = budgetClient.createBudget(builder.build());

        // THEN (Response from budget-service with data about the created budget)
        assertNotNull(res);
        assertEquals(testData.projectId, res.getProjectId());
        assertEquals(MINIMUM_YEAR, res.getFiscalYear());
        assertEquals(2, res.getExpenses().size());
    }

    @Test
    void updateBudget() {
        // GIVEN (Budget already exists in budget-service)
        Long budgetId =
                budgetClient.createBudget(minimalBudgetBuilder(testData.projectId, MINIMUM_YEAR).build()).getId();

        // WHEN (REST call to budget-service to update budget)
        PatchBudgetDTO patchBudgetDTO =
                PatchBudgetDTO.builder()
                        .fiscalYear(MAXIMUM_YEAR)
                        .estimatedCost(MAXIMUM_COST)
                        .comdevCost(MINIMUM_COST + 1.0)
                        .build();

        var res = budgetClient.updateBudget(budgetId, patchBudgetDTO);

        // THEN (Response from budget-service with updated data for the budget)
        assertNotNull(res);
        assertEquals(testData.projectId, res.getProjectId());
        assertEquals(MAXIMUM_YEAR, res.getFiscalYear());
        assertEquals(MINIMUM_COST + 1.0, res.getComdevCost());
    }

    @Test
    void updateMissingBudget() {
        // GIVEN (Budget doesn't exist in budget-service)
        PatchBudgetDTO patchBudgetDTO =
                PatchBudgetDTO.builder()
                        .fiscalYear(MAXIMUM_YEAR)
                        .estimatedCost(MAXIMUM_COST)
                        .comdevCost(MINIMUM_COST + 1.0)
                        .build();

        // WHEN (REST call to budget-service to get nonexistent budget)
        assertThrows(RuntimeException.class, () -> budgetClient.updateBudget(-1L, patchBudgetDTO));
    }

    @Test
    void updateBudgetWithExistingExpense() {
        // GIVEN (Budget with expenses already exists in budget-service)
        BudgetDTO.BudgetDTOBuilder builder = minimalBudgetBuilder(testData.projectId, MINIMUM_YEAR);
        builder.expenses(List.of(minimalExpense()));

        var createdBudget = budgetClient.createBudget(builder.build());
        Long expenseId = createdBudget.getExpenses().get(0).getId();
        Long budgetId = createdBudget.getId();

        // WHEN (REST call to budget-service to update expenses for a budget)
        PatchExpenseDTO patchExpenseDTO =
                PatchExpenseDTO.builder().id(expenseId).comdevFraction(MINIMUM_FRACTION + 1.0).build();

        var res = budgetClient.updateExpense(budgetId, List.of(patchExpenseDTO));

        // THEN (Response from budget-service with updated data for the budget's expenses)
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(MINIMUM_FRACTION + 1.0, res.get(0).getComdevFraction());
    }

    @Nested
    class CreateExpensesTest {

        @Test
        void createExpenseWithMissingBudget() {

            // GIVEN (Budget with expenses already exists in budget-service)
            ExpenseDTO expenseToBeCreated = minimalExpense();

            // WHEN (REST call to budget-service to update expenses for a budget)
            var exception =
                    assertThrows(
                            BudgetClientRequestException.class,
                            () -> budgetClient.createExpense(0L, expenseToBeCreated));

            // THEN (Response from budget-service with updated data for the budget's expenses)
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertNotNull(exception.getBody());
            assertEquals(404, exception.getBody().getStatusCode());
            assertEquals("Budget 0 not found", exception.getMessage());
            assertEquals("Budget 0 not found", exception.getBody().getMessage());
        }

        @Test
        void createExpenseWithDeletedBudget() {

            // GIVEN (Budget with expenses already exists in budget-service)
            var budget = budgetClient.createBudget(minimalBudgetBuilder(testData.projectId, MINIMUM_YEAR).build());
            Long budgetId = budget.getId();
            budgetClient.deleteBudget(budgetId);
            ExpenseDTO expenseToBeCreated = minimalExpense();

            // WHEN (REST call to budget-service to update expenses for a budget)
            var exception =
                    assertThrows(
                            BudgetClientRequestException.class,
                            () -> budgetClient.createExpense(budgetId, expenseToBeCreated));

            // THEN (Response from budget-service with updated data for the budget's expenses)
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertNotNull(exception.getBody());
            assertEquals(404, exception.getBody().getStatusCode());
            assertEquals("Budget " + budget.getId() + " not found", exception.getMessage());
            assertEquals(exception.getMessage(), exception.getBody().getMessage());
        }

        @Test
        void createExpenseWithInvalidValues() {

            // GIVEN (Budget with expenses already exists in budget-service)
            var budget = budgetClient.createBudget(minimalBudgetBuilder(testData.projectId, MINIMUM_YEAR).build());
            Long budgetId = budget.getId();
            ExpenseDTO expenseToBeCreated =
                    ExpenseDTO.builder()
                            .priceItemId(0L)
                            .comdevFraction(-1D)
                            .comdevCost(-1D)
                            .unitCost(-1)
                            .unitCount((short) -1)
                            .build();

            // WHEN (REST call to budget-service to update expenses for a budget)
            var exception =
                    assertThrows(
                            BudgetClientRequestException.class,
                            () -> budgetClient.createExpense(budgetId, expenseToBeCreated));

            // THEN (Response from budget-service with updated data for the budget's expenses)
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            ResponseMessageDTO<?> body = exception.getBody();
            assertNotNull(body);
            assertEquals(400, body.getStatusCode());
            List<ErrorDTO> errors = body.getErrors();
            assertEquals(5, errors.size());
            assertTrue(errors.stream().anyMatch(error -> error.getPointer().equals("priceItemId")));
            assertTrue(errors.stream().anyMatch(error -> error.getPointer().equals("comdevFraction")));
            assertTrue(errors.stream().anyMatch(error -> error.getPointer().equals("comdevCost")));
            assertTrue(errors.stream().anyMatch(error -> error.getPointer().equals("unitCost")));
            assertTrue(errors.stream().anyMatch(error -> error.getPointer().equals("unitCount")));
        }

        @Test
        void createExpense() {

            // GIVEN (Budget with expenses already exists in budget-service)
            var budget = budgetClient.createBudget(minimalBudgetBuilder(testData.projectId, MINIMUM_YEAR).build());
            Long budgetId = budget.getId();
            ExpenseDTO expenseToBeCreated = minimalExpense();

            // WHEN (REST call to budget-service to update expenses for a budget)
            var expense = budgetClient.createExpense(budgetId, expenseToBeCreated);

            // THEN (Response from budget-service with updated data for the budget's expenses)
            assertNotNull(expense);
            assertNotNull(expense.getId());
            assertEquals(budgetId, expense.getBudgetId());
            assertEquals(0.0, expense.getComdevFraction());
            assertEquals(0.0, expense.getComdevCost());
            assertEquals(0, expense.getUnitCost());
            assertEquals((short) MINIMUM_COUNT, expense.getUnitCount());
            assertEquals((byte) MINIMUM_COUNT, expense.getWeekCount());
        }

        @Test
        void createExpenseWithBudgetThatHasExpenses() {

            // GIVEN (Budget with expenses already exists in budget-service)
            var budget = budgetClient.createBudget(minimalBudgetBuilder(testData.projectId, MINIMUM_YEAR).build());
            Long budgetId = budget.getId();
            ExpenseDTO expenseToBeCreated = minimalExpense();
            var firstExpense = budgetClient.createExpense(budgetId, expenseToBeCreated);

            // WHEN (REST call to budget-service to update expenses for a budget)
            var secondExpense = budgetClient.createExpense(budgetId, expenseToBeCreated);

            // THEN (Response from budget-service with updated data for the budget's expenses)
            assertNotNull(firstExpense);
            assertNotNull(secondExpense);
            assertNotEquals(firstExpense.getId(), secondExpense.getId());
            assertEquals(budgetId, firstExpense.getBudgetId());
            assertEquals(budgetId, secondExpense.getBudgetId());
        }
    }

    @Test
    void updateBudgetWithMultipleExpenses() {
        // GIVEN (Budget with expenses already exists in budget-service)
        BudgetDTO.BudgetDTOBuilder builder = minimalBudgetBuilder(testData.projectId, MINIMUM_YEAR);
        builder.expenses(List.of(minimalExpense(), minimalExpense()));

        var createdBudget = budgetClient.createBudget(builder.build());
        Long expenseId1 = createdBudget.getExpenses().get(0).getId();
        Long expenseId2 = createdBudget.getExpenses().get(1).getId();
        Long budgetId = createdBudget.getId();

        // WHEN (REST call to budget-service to update expenses for a budget)
        PatchExpenseDTO patchExpenseDTO1 =
                PatchExpenseDTO.builder().id(expenseId1).comdevFraction(MINIMUM_FRACTION + 1.0).build();
        PatchExpenseDTO patchExpenseDTO2 = PatchExpenseDTO.builder().id(expenseId2).unitCost(Integer.MAX_VALUE).build();

        var res = budgetClient.updateExpense(budgetId, List.of(patchExpenseDTO1, patchExpenseDTO2));

        // THEN (Response from budget-service with updated data for the budget's expenses)
        assertNotNull(res);
        assertEquals(2, res.size());
        assertEquals(MINIMUM_FRACTION + 1.0, res.get(0).getComdevFraction());
        assertEquals(Integer.MAX_VALUE, res.get(1).getUnitCost());
    }

    @Test
    void updateMissingBudgetWithExpense() {
        // GIVEN (Budget doesn't exist in budget-service)
        PatchExpenseDTO patchExpenseDTO =
                PatchExpenseDTO.builder().id(1L).comdevFraction(MINIMUM_FRACTION + 1.0).build();
        List<PatchExpenseDTO> expenses = List.of(patchExpenseDTO);

        // WHEN (REST call to budget-service to update expenses for nonexistent budget)
        BudgetClientRequestException exception =
                assertThrows(BudgetClientRequestException.class, () -> budgetClient.updateExpense(-1L, expenses));

        // Then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    private BudgetDTO.BudgetDTOBuilder minimalBudgetBuilder(Long projectId, Integer fiscalYear) {
        return BudgetDTO.builder().projectId(projectId).fiscalYear(fiscalYear).expenses(List.of());
    }

    private ExpenseDTO minimalExpense() {
        return minimalExpenseBuilder(MINIMUM_ID, 0.0, 0.0, 0, (short) MINIMUM_COUNT, (byte) MINIMUM_COUNT).build();
    }

    private ExpenseDTO.ExpenseDTOBuilder minimalExpenseBuilder(
            Long priceItemId,
            Double comdevFraction,
            Double comdevCost,
            Integer unitCost,
            Short unitCount,
            Byte weekCount) {
        return ExpenseDTO.builder()
                .priceItemId(priceItemId)
                .comdevFraction(comdevFraction)
                .comdevCost(comdevCost)
                .unitCost(unitCost)
                .unitCount(unitCount)
                .weekCount(weekCount);
    }
}
