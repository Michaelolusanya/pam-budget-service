package com.ikea.imc.pam.budget.service.tests;

import static com.ikea.imc.pam.budget.service.client.dto.Constants.MAXIMUM_COST;
import static com.ikea.imc.pam.budget.service.client.dto.Constants.MAXIMUM_YEAR;
import static com.ikea.imc.pam.budget.service.client.dto.Constants.MINIMUM_COST;
import static com.ikea.imc.pam.budget.service.client.dto.Constants.MINIMUM_COUNT;
import static com.ikea.imc.pam.budget.service.client.dto.Constants.MINIMUM_FRACTION;
import static com.ikea.imc.pam.budget.service.client.dto.Constants.MINIMUM_ID;
import static com.ikea.imc.pam.budget.service.client.dto.Constants.MINIMUM_YEAR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ikea.imc.pam.budget.service.client.BudgetClient;
import com.ikea.imc.pam.budget.service.client.dto.*;
import com.ikea.imc.pam.budget.service.AbstractBaseTest;
import java.util.List;
import java.util.Optional;

import com.ikea.imc.pam.common.dto.ErrorDTO;
import com.ikea.imc.pam.common.dto.ResponseMessageDTO;
import com.ikea.imc.pam.common.exception.ClientRequestException;
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
        ClientRequestException exception =
                assertThrows(ClientRequestException.class, () -> budgetClient.getBudget(testData.projectId));

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
                        .internalCost(MINIMUM_COST + 1.0)
                        .build();

        var res = budgetClient.updateBudget(budgetId, patchBudgetDTO);

        // THEN (Response from budget-service with updated data for the budget)
        assertNotNull(res);
        assertEquals(testData.projectId, res.getProjectId());
        assertEquals(MAXIMUM_YEAR, res.getFiscalYear());
        assertEquals(MINIMUM_COST + 1.0, res.getInternalCost());
    }

    @Test
    void updateMissingBudget() {
        // GIVEN (Budget doesn't exist in budget-service)
        PatchBudgetDTO patchBudgetDTO =
                PatchBudgetDTO.builder()
                        .fiscalYear(MAXIMUM_YEAR)
                        .estimatedCost(MAXIMUM_COST)
                        .internalCost(MINIMUM_COST + 1.0)
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
                PatchExpenseDTO.builder().id(expenseId).internalFraction(MINIMUM_FRACTION + 1.0).build();

        var res = budgetClient.updateExpense(budgetId, List.of(patchExpenseDTO));

        // THEN (Response from budget-service with updated data for the budget's expenses)
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(MINIMUM_FRACTION + 1.0, res.get(0).getInternalFraction());
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
                            ClientRequestException.class,
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
                            ClientRequestException.class,
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
                            .internalFraction(-1D)
                            .internalCost(-1D)
                            .unitCost(-1)
                            .unitCount((short) -1)
                            .build();

            // WHEN (REST call to budget-service to update expenses for a budget)
            var exception =
                    assertThrows(
                            ClientRequestException.class,
                            () -> budgetClient.createExpense(budgetId, expenseToBeCreated));

            // THEN (Response from budget-service with updated data for the budget's expenses)
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            ResponseMessageDTO<?> body = exception.getBody();
            assertNotNull(body);
            assertEquals(400, body.getStatusCode());
            List<ErrorDTO> errors = body.getErrors();
            assertEquals(5, errors.size());
            assertTrue(errors.stream().anyMatch(error -> error.getPointer().equals("priceItemId")));
            assertTrue(errors.stream().anyMatch(error -> error.getPointer().equals("internalFraction")));
            assertTrue(errors.stream().anyMatch(error -> error.getPointer().equals("internalCost")));
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
            assertEquals(0.0, expense.getInternalFraction());
            assertEquals(0.0, expense.getInternalCost());
            assertEquals(0, expense.getUnitCost());
            assertEquals((short) MINIMUM_COUNT, expense.getUnitCount());
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
                PatchExpenseDTO.builder().id(expenseId1).internalFraction(MINIMUM_FRACTION + 1.0).build();
        PatchExpenseDTO patchExpenseDTO2 = PatchExpenseDTO.builder().id(expenseId2).unitCost(Integer.MAX_VALUE).build();

        var res = budgetClient.updateExpense(budgetId, List.of(patchExpenseDTO1, patchExpenseDTO2));

        // THEN (Response from budget-service with updated data for the budget's expenses)
        assertNotNull(res);
        assertEquals(2, res.size());
        assertEquals(MINIMUM_FRACTION + 1.0, res.get(0).getInternalFraction());
        assertEquals(Integer.MAX_VALUE, res.get(1).getUnitCost());
    }

    @Test
    void updateMissingBudgetWithExpense() {
        // GIVEN (Budget doesn't exist in budget-service)
        PatchExpenseDTO patchExpenseDTO =
                PatchExpenseDTO.builder().id(1L).internalFraction(MINIMUM_FRACTION + 1.0).build();
        List<PatchExpenseDTO> expenses = List.of(patchExpenseDTO);

        // WHEN (REST call to budget-service to update expenses for nonexistent budget)
        ClientRequestException exception =
                assertThrows(ClientRequestException.class, () -> budgetClient.updateExpense(-1L, expenses));

        // Then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Nested
    class BudgetAreaRestEndpointTests extends AbstractBaseTest {

        @Nested
        class GetBudgetAreaTest {

            @Test
            void existingBudgetArea() {

                // Given
                BudgetAreaDTO existingDTO = budgetClient.putBudgetArea(generateMinimalBudgetDTO());

                // When
                Optional<BudgetAreaDTO> optionalBudgetArea = budgetClient.getBudgetArea(existingDTO.budgetAreaId());

                // Then
                assertTrue(optionalBudgetArea.isPresent());
                assertEquals(existingDTO.budgetAreaId(), optionalBudgetArea.get().budgetAreaId());
            }

            @Test
            void notFoundBudgetArea() {

                // Given
                Long budgetAreaId = Long.MAX_VALUE;

                // When
                ClientRequestException requestException = assertThrows(ClientRequestException.class, () -> budgetClient.getBudgetArea(budgetAreaId));

                // Then
                assertEquals(HttpStatus.NOT_FOUND, requestException.getStatusCode());
            }
        }

        @Nested
        class FindBudgetAreaTest {

            @Test
            void existingBudgetArea() {

                // Given
                BudgetAreaDTO existingDTO = budgetClient.putBudgetArea(generateMinimalBudgetDTO());

                // When
                Optional<BudgetAreaDTO> optionalBudgetArea = budgetClient.findBudgetArea(existingDTO.parentType(), existingDTO.parentId(), existingDTO.fiscalYear());

                // Then
                assertTrue(optionalBudgetArea.isPresent());
                assertEquals(existingDTO.budgetAreaId(), optionalBudgetArea.get().budgetAreaId());
            }

            @Test
            void notFoundBudgetArea() {

                // Given
                BudgetParentType parentType = BudgetParentType.DEPARTMENT;
                Long parentId = Long.MAX_VALUE;
                Integer fiscalYear = MAXIMUM_YEAR;

                // When
                ClientRequestException requestException = assertThrows(ClientRequestException.class, () -> budgetClient.findBudgetArea(parentType, parentId, fiscalYear));

                // Then
                assertEquals(HttpStatus.NOT_FOUND, requestException.getStatusCode());
            }

            @Test
            void invalidSearchParameters() {

                // Given
                BudgetParentType parentType = BudgetParentType.DEPARTMENT;
                Long parentId = -1L;
                Integer fiscalYear = 1000;

                // When
                ClientRequestException requestException = assertThrows(ClientRequestException.class, () -> budgetClient.findBudgetArea(parentType, parentId, fiscalYear));

                // Then
                assertEquals(HttpStatus.BAD_REQUEST, requestException.getStatusCode());
                var responseMessage = requestException.getBody();
                assertNotNull(responseMessage);
                assertEquals(2, responseMessage.getErrors().size());
                assertTrue(responseMessage.getErrors().stream().anyMatch(error -> "fiscalYear".equals(error.getPointer())));
                assertTrue(responseMessage.getErrors().stream().anyMatch(error -> "parentId".equals(error.getPointer())));
            }
        }

        @Nested
        class PutBudgetAreaTest {

            @Test
            void newBudgetArea() {

                // Given
                BudgetAreaDTO toCreateDto = BudgetAreaDTO
                        .builder()
                        .parentId(1234L)
                        .parentType(BudgetParentType.DEPARTMENT)
                        .fiscalYear(2000)
                        .costLimit(MINIMUM_COST)
                        .build();

                // When
                BudgetAreaDTO dto = budgetClient.putBudgetArea(toCreateDto);

                // Then
                assertEquals(1234L, dto.parentId());
                assertEquals(BudgetParentType.DEPARTMENT, dto.parentType());
                assertEquals(2000, dto.fiscalYear());
                assertEquals(MINIMUM_COST, dto.costLimit());
            }

            @Test
            void updateBudgetArea() {

                // Given
                BudgetAreaDTO createdDTO = budgetClient.putBudgetArea(generateMinimalBudgetDTO());
                BudgetAreaDTO updatedDTO = createdDTO.toBuilder().costLimit(300000L).build();

                // When
                BudgetAreaDTO dto = budgetClient.putBudgetArea(updatedDTO);

                // Then
                assertEquals(MINIMUM_ID, dto.parentId());
                assertEquals(MINIMUM_YEAR, dto.fiscalYear());
                assertEquals(300000L, dto.costLimit());
            }

            @Test
            void invalidBudgetArea() {

                // Given
                BudgetAreaDTO dto = BudgetAreaDTO
                        .builder()
                        .parentType(null)
                        .parentId(-1L)
                        .fiscalYear(-1)
                        .costLimit(-1L)
                        .build();

                // When
                ClientRequestException exception = assertThrows(ClientRequestException.class, () -> budgetClient.putBudgetArea(dto));

                // Then
                assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
                var responseMessage = exception.getBody();
                assertNotNull(responseMessage);
                assertEquals(4, responseMessage.getErrors().size());
                assertTrue(responseMessage.getErrors().stream().anyMatch(error -> "parentType".equals(error.getPointer())));
                assertTrue(responseMessage.getErrors().stream().anyMatch(error -> "parentId".equals(error.getPointer())));
                assertTrue(responseMessage.getErrors().stream().anyMatch(error -> "fiscalYear".equals(error.getPointer())));
                assertTrue(responseMessage.getErrors().stream().anyMatch(error -> "costLimit".equals(error.getPointer())));
            }
        }

        private static BudgetAreaDTO generateMinimalBudgetDTO() {
            return BudgetAreaDTO
                    .builder()
                    .parentId(MINIMUM_ID)
                    .parentType(BudgetParentType.BUSINESS_AREA)
                    .fiscalYear(MINIMUM_YEAR)
                    .costLimit(MINIMUM_COST)
                    .build();
        }
    }

    private BudgetDTO.BudgetDTOBuilder minimalBudgetBuilder(Long projectId, Integer fiscalYear) {
        return BudgetDTO
                .builder()
                .parentType(BudgetParentType.BUSINESS_AREA)
                .parentId(MINIMUM_ID)
                .projectId(projectId)
                .fiscalYear(fiscalYear)
                .expenses(List.of());
    }

    private ExpenseDTO minimalExpense() {
        return minimalExpenseBuilder(MINIMUM_ID, 0.0, 0.0, 0, (short) MINIMUM_COUNT).build();
    }

    private ExpenseDTO.ExpenseDTOBuilder minimalExpenseBuilder(
            Long priceItemId,
            Double internalFraction,
            Double internalCost,
            Integer unitCost,
            Short unitCount) {
        return ExpenseDTO.builder()
                .priceItemId(priceItemId)
                .internalFraction(internalFraction)
                .internalCost(internalCost)
                .unitCost(unitCost)
                .unitCount(unitCount);
    }
}
