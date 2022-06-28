package com.ikea.imc.pam.budget.service.service;

import com.ikea.imc.pam.budget.service.client.dto.BudgetParentType;
import com.ikea.imc.pam.budget.service.exception.NotFoundException;
import com.ikea.imc.pam.budget.service.repository.BudgetRepository;
import com.ikea.imc.pam.budget.service.repository.BudgetVersionRepository;
import com.ikea.imc.pam.budget.service.repository.ExpensesRepository;
import com.ikea.imc.pam.budget.service.repository.model.Budget;
import com.ikea.imc.pam.budget.service.repository.model.BudgetArea;
import com.ikea.imc.pam.budget.service.repository.model.BudgetVersion;
import com.ikea.imc.pam.budget.service.repository.model.Expenses;
import com.ikea.imc.pam.budget.service.repository.model.utils.InvoicingTypeOption;
import com.ikea.imc.pam.budget.service.repository.model.utils.Status;
import com.ikea.imc.pam.budget.service.service.entity.BudgetAreaParameters;
import com.ikea.imc.pam.budget.service.service.entity.BudgetContent;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BudgetServiceV1Test {
    
    private static final Long BUDGET_ID = 1L;
    private static final Long BUDGET_ID_2 = 2L;
    private static final Long BUDGET_VERSION_ID = 3L;
    private static final Long EXPENSE_ID = 5L;
    private static final Long EXPENSE_ID_2 = 6L;
    private static final Long BUDGET_AREA_ID = 123L;
    private static final Long BUDGET_AREA_ID_2 = 124L;
    private static final BudgetParentType BUDGET_AREA_PARENT_TYPE = BudgetParentType.BUSINESS_AREA;
    private static final Long BUDGET_AREA_PARENT_ID = 1234L;
    
    private static final String BUDGET_VERSION_NAME = "budname";
    private static final LocalDate BUDGET_VERSION_DATE = LocalDate.of(2020, 3, 1);
    private static final Integer FISCAL_YEAR = 2020;
    private static final Integer FISCAL_YEAR_2 = 2222;
    
    private static final Long ESTIMATED_BUDGET = 100_000L;
    private static final Long ESTIMATED_BUDGET_2 = 200_000L;
    private static final double INTERNAL_COST = 50_000d;
    private static final double INTERNAL_COST_2 = 60_000d;
    private static final Long PROJECT_ID = 2L;
    
    private static final Long PRICE_ITEM_ID = 111L;
    private static final Long PRICE_ITEM_ID_2 = 222L;
    private static final String COMMENT = "comment1";
    private static final String COMMENT_2 = "comment2";
    private static final Integer COST = 123;
    private static final Integer COST_2 = 234;
    private static final Integer COST_PER_UNIT = 23;
    private static final Integer COST_PER_UNIT_2 = 34;
    private static final Byte PERCENT_INTERNAL = 10;
    private static final Byte PERCENT_INTERNAL_2 = 11;
    private static final Double UNITS = 2.0;
    private static final Double UNITS_2 = 4.0;
    private static final InvoicingTypeOption INVOICING_TYPE_OPTION = InvoicingTypeOption.FIXED_PRICE;
    private static final InvoicingTypeOption INVOICING_TYPE_OPTION_2 = InvoicingTypeOption.HOURLY_PRICE;
    
    @Mock
    private BudgetAreaService budgetAreaService;
    
    @Mock
    private BudgetRepository repository;
    
    @Mock
    private BudgetVersionRepository budgetVersionRepository;
    
    @Mock
    private ExpensesRepository expensesRepository;
    
    @InjectMocks
    private BudgetServiceV1 service;
    
    @Nested
    class CreateBudgetTest {
        
        @Captor
        ArgumentCaptor<BudgetArea> budgetAreaCaptor;
        @Captor
        ArgumentCaptor<BudgetVersion> budgetVersionCaptor;
        @Captor
        ArgumentCaptor<Budget> budgetCaptor;
        
        @Test
        void validateSavedBudgetArea() {
            
            // Given
            BudgetAreaParameters budgetAreaParameters = generateBudgetAreaParameters();
            BudgetContent budget = generateBudget(BUDGET_ID);
            when(budgetAreaService.putBudgetArea(budgetAreaCaptor.capture())).thenReturn(generateBudgetArea());
            when(budgetVersionRepository.saveAndFlush(any())).thenReturn(generateBudgetVersion());
            when(repository.saveAndFlush(any())).thenReturn(budget.budget());
            
            // When
            service.createBudget(budgetAreaParameters, budget.budget());
            
            // Then
            assertNotNull(budgetAreaCaptor.getValue());
            BudgetArea budgetArea = budgetAreaCaptor.getValue();
            assertEquals(BUDGET_AREA_PARENT_TYPE, budgetArea.getParentType());
            assertEquals(BUDGET_AREA_PARENT_ID, budgetArea.getParentId());
            assertEquals(FISCAL_YEAR, budgetArea.getFiscalYear());
        }
        
        @Test
        void validateSavedBudgetVersion() {
            
            // Given
            BudgetAreaParameters budgetAreaParameters = generateBudgetAreaParameters();
            BudgetContent budget = generateBudget(BUDGET_ID);
            when(budgetAreaService.putBudgetArea(any())).thenReturn(generateBudgetArea());
            when(budgetVersionRepository.saveAndFlush(budgetVersionCaptor.capture())).thenReturn(generateBudgetVersion());
            when(repository.saveAndFlush(any())).thenReturn(budget.budget());
            
            // When
            service.createBudget(budgetAreaParameters, budget.budget());
            
            // Then
            assertNotNull(budgetVersionCaptor.getValue());
            BudgetVersion version = budgetVersionCaptor.getValue();
            assertNotNull(version.getBudgetArea());
            assertEquals(FISCAL_YEAR.toString(), version.getBudgetVersionName());
            assertEquals(LocalDate.of(FISCAL_YEAR, 1, 1), version.getBudgetVersionDate());
            assertEquals(BUDGET_AREA_ID, version.getBudgetArea().getBudgetAreaId());
        }
        
        @Test
        void validateSavedBudget() {
            
            // Given
            BudgetAreaParameters budgetAreaParameters = generateBudgetAreaParameters();
            when(budgetAreaService.putBudgetArea(any())).thenReturn(generateBudgetArea());
            when(budgetVersionRepository.saveAndFlush(any())).thenReturn(generateBudgetVersion());
            when(repository.saveAndFlush(budgetCaptor.capture())).thenReturn(generateBudget(BUDGET_ID).budget());
            
            // When
            service.createBudget(budgetAreaParameters, generateBudget(null).budget());
            
            // Then
            assertNotNull(budgetCaptor.getValue());
            Budget budget = budgetCaptor.getValue();
            assertNull(budget.getBudgetId());
            assertEquals(PROJECT_ID, budget.getProjectId());
            assertEquals(ESTIMATED_BUDGET, budget.getEstimatedBudget());
            assertEquals(Status.ACTIVE, budget.getStatus());
            assertNotNull(budget.getBudgetVersion());
            assertEquals(BUDGET_VERSION_ID, budget.getBudgetVersion().getBudgetVersionId());
        }
        
        @Test
        void createBudgetWithoutExpenses() {
            
            // Given
            BudgetAreaParameters budgetAreaParameters = generateBudgetAreaParameters();
            when(budgetAreaService.putBudgetArea(any())).thenReturn(generateBudgetArea());
            when(budgetVersionRepository.saveAndFlush(any())).thenReturn(generateBudgetVersion());
            when(repository.saveAndFlush(any())).thenReturn(generateBudget(BUDGET_ID).budget());
            
            // When
            BudgetContent budgetContent = service.createBudget(budgetAreaParameters, generateBudget(null).budget());
            
            // Then
            Budget budget = budgetContent.budget();
            assertEquals(BUDGET_ID, budget.getBudgetId());
            assertEquals(PROJECT_ID, budget.getProjectId());
            assertEquals(ESTIMATED_BUDGET, budget.getEstimatedBudget());
            assertEquals(Status.ACTIVE, budget.getStatus());
            assertNotNull(budget.getBudgetVersion());
            assertEquals(BUDGET_VERSION_ID, budget.getBudgetVersion().getBudgetVersionId());
            assertEquals(0, budget.getExpenses().size());
        }
        
        @Test
        void createBudgetWithExpenses() {
            
            // Given
            BudgetAreaParameters budgetAreaParameters = generateBudgetAreaParameters();
            BudgetContent inputBudget = generateBudget(BUDGET_ID);
            inputBudget.budget()
                .setExpenses(List.of(generateExpenses(null, inputBudget), generateExpenses2(null, inputBudget)));
            BudgetContent outputBudget = generateBudget(BUDGET_ID);
            outputBudget.budget()
                .setExpenses(List.of(
                    generateExpenses(EXPENSE_ID, outputBudget),
                    generateExpenses2(EXPENSE_ID_2, outputBudget)
                ));
            when(budgetAreaService.putBudgetArea(any())).thenReturn(generateBudgetArea());
            when(budgetVersionRepository.saveAndFlush(any())).thenReturn(generateBudgetVersion());
            when(repository.saveAndFlush(any())).thenReturn(outputBudget.budget());
            
            // When
            BudgetContent budgetContent = service.createBudget(budgetAreaParameters, generateBudget(null).budget());
            
            // Then
            Budget budget = budgetContent.budget();
            assertEquals(2, budget.getExpenses().size());
            Expenses expenses = budget.getExpenses().get(0);
            assertEquals(EXPENSE_ID, expenses.getExpensesId());
            assertEquals(EXPENSE_ID_2, budget.getExpenses().get(1).getExpensesId());
            assertEquals(PRICE_ITEM_ID, expenses.getPriceItemId());
            assertEquals(COMMENT, expenses.getComment());
            assertEquals(COST, expenses.getCost());
            assertEquals(COST_PER_UNIT, expenses.getCostPerUnit());
            assertEquals(PERCENT_INTERNAL, expenses.getInternalPercent());
            assertEquals(UNITS, expenses.getUnits());
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
            Optional<BudgetContent> optionalBudget = service.getById(BUDGET_ID);
            
            // Then
            assertTrue(optionalBudget.isEmpty());
        }
        
        @Test
        void oneFound() {
            
            // Given
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(generateBudget(BUDGET_ID).budget()));
            
            // When
            Optional<BudgetContent> optionalBudget = service.getById(BUDGET_ID);
            
            // Then
            assertTrue(optionalBudget.isPresent());
            Budget budget = optionalBudget.get().budget();
            assertEquals(BUDGET_ID, budget.getBudgetId());
            assertEquals(ESTIMATED_BUDGET, budget.getEstimatedBudget());
            assertEquals(PROJECT_ID, budget.getProjectId());
        }
    }
    
    @Nested
    class ListBudgetsTest {
        
        @Test
        void nullInput() {
            
            // Given
            when(repository.getAllActive()).thenReturn(List.of(generateBudget(BUDGET_ID).budget(),
                generateBudget(BUDGET_ID_2).budget()
            ));
            
            // When
            List<BudgetContent> budgets = service.listBudgets(null, null);
            
            // Then
            assertEquals(2, budgets.size());
            assertEquals(BUDGET_ID, budgets.get(0).getBudgetId());
            assertEquals(BUDGET_ID_2, budgets.get(1).getBudgetId());
        }
        
        @Test
        void emptyInput() {
            
            // Given
            when(repository.getAllActive()).thenReturn(List.of(generateBudget(BUDGET_ID).budget(),
                generateBudget(BUDGET_ID_2).budget()
            ));
            
            // When
            List<BudgetContent> budgets = service.listBudgets(List.of(), List.of());
            
            // Then
            assertEquals(2, budgets.size());
            assertEquals(BUDGET_ID, budgets.get(0).getBudgetId());
            assertEquals(BUDGET_ID_2, budgets.get(1).getBudgetId());
        }
        
        @Test
        void searchProjectIdAndNullFiscalYear() {
            
            // Given
            List<Long> projectIds = List.of(PROJECT_ID);
            when(repository.getBudgetByProjectId(projectIds)).thenReturn(List.of(generateBudget(BUDGET_ID).budget(),
                generateBudget(BUDGET_ID_2).budget()
            ));
            
            // When
            List<BudgetContent> budgets = service.listBudgets(projectIds, null);
            
            // Then
            assertEquals(2, budgets.size());
            assertEquals(BUDGET_ID, budgets.get(0).getBudgetId());
            assertEquals(BUDGET_ID_2, budgets.get(1).getBudgetId());
        }
        
        @Test
        void searchFiscalYearAndNullProjectIds() {
            
            // Given
            List<Integer> fiscalYears = List.of(FISCAL_YEAR);
            when(repository.getBudgetByFiscalYear(List.of(FISCAL_YEAR))).thenReturn(List.of(generateBudget(BUDGET_ID).budget(),
                generateBudget(BUDGET_ID_2).budget()
            ));
            
            // When
            List<BudgetContent> budgets = service.listBudgets(null, fiscalYears);
            
            // Then
            assertEquals(2, budgets.size());
            assertEquals(BUDGET_ID, budgets.get(0).getBudgetId());
            assertEquals(BUDGET_ID_2, budgets.get(1).getBudgetId());
        }
        
        @Test
        void searchProjectIdAndEmptyFiscalYear() {
            
            // Given
            List<Long> projectIds = List.of(PROJECT_ID);
            when(repository.getBudgetByProjectId(projectIds)).thenReturn(List.of(generateBudget(BUDGET_ID).budget(),
                generateBudget(BUDGET_ID_2).budget()
            ));
            
            // When
            List<BudgetContent> budgets = service.listBudgets(projectIds, List.of());
            
            // Then
            assertEquals(2, budgets.size());
            assertEquals(BUDGET_ID, budgets.get(0).getBudgetId());
            assertEquals(BUDGET_ID_2, budgets.get(1).getBudgetId());
        }
        
        @Test
        void searchFiscalYearAndEmptyProjectIds() {
            
            // Given
            List<Integer> fiscalYears = List.of(FISCAL_YEAR);
            when(repository.getBudgetByFiscalYear(List.of(FISCAL_YEAR))).thenReturn(List.of(generateBudget(BUDGET_ID).budget(),
                generateBudget(BUDGET_ID_2).budget()
            ));
            
            // When
            List<BudgetContent> budgets = service.listBudgets(List.of(), fiscalYears);
            
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
            when(repository.getBudgetByProjectIdAndFiscalYear(projectIds, List.of(FISCAL_YEAR))).thenReturn(List.of(generateBudget(BUDGET_ID).budget(),
                generateBudget(BUDGET_ID_2).budget()
            ));
            
            // When
            List<BudgetContent> budgets = service.listBudgets(projectIds, fiscalYears);
            
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
            Optional<BudgetContent> optionalBudget = service.deleteById(BUDGET_ID);
            
            // Then
            assertTrue(optionalBudget.isEmpty());
        }
        
        @Test
        void deleteAlreadyDeleted() {
            
            // Given
            BudgetContent budget = generateBudget(BUDGET_ID);
            budget.budget().setStatus(Status.ARCHIVED);
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(budget.budget()));
            
            // When
            Optional<BudgetContent> optionalBudget = service.deleteById(BUDGET_ID);
            
            // Then
            assertTrue(optionalBudget.isEmpty());
        }
        
        @Test
        void deleteAsExpected() {
            
            // Given
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(generateBudget(BUDGET_ID).budget()));
            
            // When
            Optional<BudgetContent> optionalBudget = service.deleteById(BUDGET_ID);
            
            // Then
            assertTrue(optionalBudget.isPresent());
            Budget budget = optionalBudget.get().budget();
            assertEquals(BUDGET_ID, budget.getBudgetId());
            assertEquals(Status.ARCHIVED, budget.getStatus());
        }
    }
    
    @Nested
    class PatchBudgetTest {
        
        @Captor
        ArgumentCaptor<BudgetArea> budgetAreaCapture;
        @Captor
        ArgumentCaptor<BudgetVersion> budgetVersionCapture;
        @Captor
        ArgumentCaptor<Budget> budgetCapture;
        
        @Test
        void budgetNotFound() {
            
            // Given
            BudgetContent inputBudget = generateBudget(BUDGET_ID);
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.empty());
            
            // When
            Optional<BudgetContent> optionalBudget = service.patchBudget(BUDGET_ID, FISCAL_YEAR, inputBudget.budget());
            
            // Then
            assertTrue(optionalBudget.isEmpty());
        }
        
        @Test
        void updatedBudgetIsEmpty() {
            
            // Given
            Budget inputBudget = Budget.builder().build();
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(generateBudget(BUDGET_ID).budget()));
            
            // When
            Optional<BudgetContent> optionalBudget = service.patchBudget(BUDGET_ID, FISCAL_YEAR, inputBudget);
            
            // Then
            assertTrue(optionalBudget.isPresent());
            Budget budget = optionalBudget.get().budget();
            assertEquals(BUDGET_ID, budget.getBudgetId());
            assertEquals(PROJECT_ID, budget.getProjectId());
            assertEquals(Status.ACTIVE, budget.getStatus());
        }
        
        @Test
        void budgetContainsNoUpdate() {
            
            // Given
            Budget inputBudget = generateBudget(BUDGET_ID).budget();
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(inputBudget));
            
            // When
            Optional<BudgetContent> optionalBudget = service.patchBudget(BUDGET_ID, FISCAL_YEAR, inputBudget);
            
            // Then
            assertTrue(optionalBudget.isPresent());
            Budget budget = optionalBudget.get().budget();
            assertEquals(BUDGET_ID, budget.getBudgetId());
            assertEquals(PROJECT_ID, budget.getProjectId());
            assertEquals(Status.ACTIVE, budget.getStatus());
        }
        
        @Test
        void fiscalYearIsNull() {
            
            // Given
            Budget inputBudget = generateBudget(BUDGET_ID).budget();
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(inputBudget));
            
            // When
            Optional<BudgetContent> optionalBudget = service.patchBudget(BUDGET_ID, null, inputBudget);
            
            // Then
            assertTrue(optionalBudget.isPresent());
            Budget budget = optionalBudget.get().budget();
            assertNotNull(budget.getBudgetVersion());
            assertNotNull(budget.getBudgetVersion().getBudgetArea());
            assertEquals(FISCAL_YEAR, budget.getBudgetVersion().getBudgetArea().getFiscalYear());
        }
        
        @Test
        void fiscalYearIsZero() {
            
            // Given
            Budget inputBudget = generateBudget(BUDGET_ID).budget();
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(inputBudget));
            
            // When
            Optional<BudgetContent> optionalBudget = service.patchBudget(BUDGET_ID, 0, inputBudget);
            
            // Then
            assertTrue(optionalBudget.isPresent());
            Budget budget = optionalBudget.get().budget();
            assertNotNull(budget.getBudgetVersion());
            assertNotNull(budget.getBudgetVersion().getBudgetArea());
            assertEquals(FISCAL_YEAR, budget.getBudgetVersion().getBudgetArea().getFiscalYear());
        }
        
        @Test
        void budgetIsChanged() {
            
            // Given
            Budget inputBudget = generateBudget(BUDGET_ID).budget();
            inputBudget.setEstimatedBudget(ESTIMATED_BUDGET_2);
            
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(generateBudget(BUDGET_ID).budget()));
            when(repository.saveAndFlush(budgetCapture.capture())).thenReturn(inputBudget);
            
            // When
            Optional<BudgetContent> optionalBudget = service.patchBudget(BUDGET_ID, FISCAL_YEAR, inputBudget);
            
            // Then
            assertTrue(optionalBudget.isPresent());
            Budget budget = optionalBudget.get().budget();
            assertEquals(BUDGET_ID, budget.getBudgetId());
            assertEquals(PROJECT_ID, budget.getProjectId());
            assertEquals(ESTIMATED_BUDGET_2, budget.getEstimatedBudget());
            assertEquals(Status.ACTIVE, budget.getStatus());
        }
        
        @Test
        void fiscalYearIsChanged() {
            
            // Given
            Budget inputBudget = generateBudget(BUDGET_ID).budget();
            BudgetVersion updatedBudgetVersion = generateBudgetVersion();
            updatedBudgetVersion.setBudgetArea(generateBudgetArea2());
            
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(inputBudget));
            when(budgetAreaService.putBudgetArea(budgetAreaCapture.capture())).thenReturn(generateBudgetArea2());
            when(budgetVersionRepository.saveAndFlush(budgetVersionCapture.capture())).thenReturn(updatedBudgetVersion);
            
            // When
            Optional<BudgetContent> optionalBudget = service.patchBudget(BUDGET_ID, FISCAL_YEAR_2, inputBudget);
            
            // Then
            assertTrue(optionalBudget.isPresent());
            Budget budget = optionalBudget.get().budget();
            assertNotNull(budget.getBudgetVersion());
            assertEquals(FISCAL_YEAR_2, budget.getBudgetVersion().getBudgetArea().getFiscalYear());
            
            // Input
            BudgetArea savedBudgetArea = budgetAreaCapture.getValue();
            assertNull(savedBudgetArea.getBudgetAreaId());
            assertEquals(BUDGET_AREA_PARENT_TYPE, savedBudgetArea.getParentType());
            assertEquals(BUDGET_AREA_PARENT_ID, savedBudgetArea.getParentId());
            assertEquals(FISCAL_YEAR_2, savedBudgetArea.getFiscalYear());
            
            assertEquals(BUDGET_AREA_ID_2, budgetVersionCapture.getValue().getBudgetArea().getBudgetAreaId());
        }
    }
    
    @Nested
    class CreateExpensesTest {
        
        @Captor
        private ArgumentCaptor<Expenses> expensesArgumentCaptor;
        
        @Test
        void budgetIsNull() {
            
            // Given
            Expenses inputExpenses = generateExpenses(null, new BudgetContent(null));
            
            // When
            NotFoundException notFoundException =
                assertThrows(NotFoundException.class, () -> service.createExpenses(null, inputExpenses));
            
            // Then
            assertEquals("Budget 0 not found", notFoundException.getMessage());
        }
        
        @Test
        void budgetIsDeleted() {
            
            // Given
            BudgetContent budgetContent = generateBudget(BUDGET_ID);
            Budget budget = budgetContent.budget();
            budget.setStatus(Status.ARCHIVED);
            Expenses inputExpenses = generateExpenses(null, budgetContent);
            
            // When
            NotFoundException notFoundException =
                assertThrows(NotFoundException.class, () -> service.createExpenses(budget, inputExpenses));
            
            // Then
            assertEquals("Budget 1 not found", notFoundException.getMessage());
        }
        
        @Test
        void createExpenses_SavedValuesValidation() {
            
            // Given
            BudgetContent budget = generateBudget(BUDGET_ID);
            Expenses inputExpenses = generateExpenses(null, budget);
            when(expensesRepository.saveAndFlush(expensesArgumentCaptor.capture())).thenReturn(generateExpenses(
                EXPENSE_ID,
                budget
            ));
            
            // When
            service.createExpenses(budget.budget(), inputExpenses);
            
            // Then
            assertNotNull(expensesArgumentCaptor.getValue());
            Expenses createdExpense = expensesArgumentCaptor.getValue();
            assertEquals(PRICE_ITEM_ID, createdExpense.getPriceItemId());
            assertEquals(COMMENT, createdExpense.getComment());
            assertEquals(COST, createdExpense.getCost());
            assertEquals(COST_PER_UNIT, createdExpense.getCostPerUnit());
            assertEquals(PERCENT_INTERNAL, createdExpense.getInternalPercent());
            assertEquals(UNITS, createdExpense.getUnits());
            assertEquals(INVOICING_TYPE_OPTION, createdExpense.getInvoicingTypeOption());
        }
        
        @Test
        void createExpenses() {
            
            // Given
            BudgetContent budget = generateBudget(BUDGET_ID);
            Expenses inputExpenses = generateExpenses(null, budget);
            when(expensesRepository.saveAndFlush(any())).thenReturn(generateExpenses(EXPENSE_ID, budget));
            
            // When
            Expenses createdExpense = service.createExpenses(budget.budget(), inputExpenses);
            
            // Then
            assertNotNull(createdExpense);
            assertEquals(EXPENSE_ID, createdExpense.getExpensesId());
            assertEquals(PRICE_ITEM_ID, createdExpense.getPriceItemId());
            assertEquals(COMMENT, createdExpense.getComment());
            assertEquals(COST, createdExpense.getCost());
            assertEquals(COST_PER_UNIT, createdExpense.getCostPerUnit());
            assertEquals(PERCENT_INTERNAL, createdExpense.getInternalPercent());
            assertEquals(UNITS, createdExpense.getUnits());
            assertEquals(INVOICING_TYPE_OPTION, createdExpense.getInvoicingTypeOption());
            assertNotNull(createdExpense.getBudget());
            assertEquals(BUDGET_ID, createdExpense.getBudget().getBudgetId());
        }
    }
    
    @Nested
    class PatchExpensesTest {
        
        @Captor
        private ArgumentCaptor<List<Expenses>> expensesListArgumentCaptor;
        
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
            Budget budget = generateBudget(BUDGET_ID).budget();
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
            BudgetContent budgetContent = generateBudget(BUDGET_ID);
            Budget budget = budgetContent.budget();
            budget.setExpenses(List.of());
            List<Expenses> inputExpenses = List.of(generateExpenses(EXPENSE_ID, budgetContent));
            
            
            // When
            NotFoundException exception =
                assertThrows(NotFoundException.class, () -> service.patchExpenses(budget, inputExpenses));
            
            // Then
            assertEquals("Expenses with id 5 not found", exception.getMessage());
        }
        
        @Test
        void updatedExpensesIsEmptyAndBudgetHasNoExpenses() {
            
            // Given
            Budget budget = generateBudget(BUDGET_ID).budget();
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
            BudgetContent budget = generateBudget(BUDGET_ID);
            budget.budget()
                .setExpenses(List.of(generateExpenses(EXPENSE_ID, budget), generateExpenses(EXPENSE_ID_2, budget)));
            List<Expenses> inputExpenses = List.of();
            when(expensesRepository.saveAllAndFlush(expensesListArgumentCaptor.capture())).thenReturn(List.of());
            
            // When
            List<Expenses> expenses = service.patchExpenses(budget.budget(), inputExpenses);
            
            // Then
            assertEquals(2, expenses.size());
            assertEquals(0, expensesListArgumentCaptor.getValue().size());
            assertEquals(EXPENSE_ID, expenses.get(0).getExpensesId());
            assertEquals(EXPENSE_ID_2, expenses.get(1).getExpensesId());
        }
        
        @Test
        void expensesContainsNoUpdate() {
            
            // Given
            BudgetContent budget = generateBudget(BUDGET_ID);
            List<Expenses> inputExpenses =
                List.of(generateExpenses(EXPENSE_ID, budget), generateExpenses(EXPENSE_ID_2, budget));
            budget.budget().setExpenses(inputExpenses);
            when(expensesRepository.saveAllAndFlush(expensesListArgumentCaptor.capture())).thenReturn(List.of());
            
            // When
            List<Expenses> expenses = service.patchExpenses(budget.budget(), inputExpenses);
            
            // Then
            assertEquals(2, expenses.size());
            assertEquals(0, expensesListArgumentCaptor.getValue().size());
            assertEquals(EXPENSE_ID, expenses.get(0).getExpensesId());
            assertEquals(EXPENSE_ID_2, expenses.get(1).getExpensesId());
        }
        
        @Test
        void oneExpenseIsChanged() {
            
            // Given
            BudgetContent budget = generateBudget(BUDGET_ID);
            budget.budget()
                .setExpenses(List.of(generateExpenses(EXPENSE_ID, budget), generateExpenses(EXPENSE_ID_2, budget)));
            List<Expenses> inputExpenses =
                List.of(generateExpenses(EXPENSE_ID, budget), generateExpenses2(EXPENSE_ID_2, budget));
            when(expensesRepository.saveAllAndFlush(expensesListArgumentCaptor.capture())).thenReturn(List.of(
                generateExpenses2(EXPENSE_ID_2, budget)));
            
            // When
            List<Expenses> expenses = service.patchExpenses(budget.budget(), inputExpenses);
            
            // Then
            assertEquals(2, expenses.size());
            assertEquals(1, expensesListArgumentCaptor.getValue().size());
            assertEquals(EXPENSE_ID, expenses.get(0).getExpensesId());
            
            Expenses updatedExpense = expenses.get(1);
            assertEquals(EXPENSE_ID_2, updatedExpense.getExpensesId());
            assertEquals(PRICE_ITEM_ID_2, updatedExpense.getPriceItemId());
            assertEquals(COMMENT_2, updatedExpense.getComment());
            assertEquals(COST_2, updatedExpense.getCost());
            assertEquals(COST_PER_UNIT_2, updatedExpense.getCostPerUnit());
            assertEquals(PERCENT_INTERNAL_2, updatedExpense.getInternalPercent());
            assertEquals(UNITS_2, updatedExpense.getUnits());
            assertEquals(INVOICING_TYPE_OPTION_2, updatedExpense.getInvoicingTypeOption());
        }
        
        @Test
        void oneExpenseIsChangedSaveValidation() {
            
            // Given
            BudgetContent budget = generateBudget(BUDGET_ID);
            budget.budget()
                .setExpenses(List.of(generateExpenses(EXPENSE_ID, budget), generateExpenses(EXPENSE_ID_2, budget)));
            List<Expenses> inputExpenses =
                List.of(generateExpenses(EXPENSE_ID, budget), generateExpenses2(EXPENSE_ID_2, budget));
            when(expensesRepository.saveAllAndFlush(expensesListArgumentCaptor.capture())).thenReturn(List.of(
                generateExpenses2(EXPENSE_ID_2, budget)));
            
            // When
            service.patchExpenses(budget.budget(), inputExpenses);
            
            // Then
            assertEquals(1, expensesListArgumentCaptor.getValue().size());
            Expenses updatedExpense = expensesListArgumentCaptor.getValue().get(0);
            assertEquals(EXPENSE_ID_2, updatedExpense.getExpensesId());
            assertEquals(PRICE_ITEM_ID_2, updatedExpense.getPriceItemId());
            assertEquals(COMMENT_2, updatedExpense.getComment());
            assertEquals(COST_2, updatedExpense.getCost());
            assertEquals(COST_PER_UNIT_2, updatedExpense.getCostPerUnit());
            assertEquals(PERCENT_INTERNAL_2, updatedExpense.getInternalPercent());
            assertEquals(UNITS_2, updatedExpense.getUnits());
            assertEquals(INVOICING_TYPE_OPTION_2, updatedExpense.getInvoicingTypeOption());
        }
        
        @Test
        void allExpensesAreChanged() {
            
            // Given
            BudgetContent budget = generateBudget(BUDGET_ID);
            budget.budget()
                .setExpenses(List.of(generateExpenses(EXPENSE_ID, budget), generateExpenses(EXPENSE_ID_2, budget)));
            List<Expenses> inputExpenses =
                List.of(generateExpenses2(EXPENSE_ID, budget), generateExpenses2(EXPENSE_ID_2, budget));
            when(expensesRepository.saveAllAndFlush(expensesListArgumentCaptor.capture())).thenReturn(inputExpenses);
            
            // When
            List<Expenses> expenses = service.patchExpenses(budget.budget(), inputExpenses);
            
            // Then
            assertEquals(2, expenses.size());
            assertEquals(2, expensesListArgumentCaptor.getValue().size());
            assertEquals(EXPENSE_ID, expenses.get(0).getExpensesId());
            assertEquals(EXPENSE_ID_2, expenses.get(1).getExpensesId());
        }
    }
    
    private static BudgetAreaParameters generateBudgetAreaParameters() {
        return new BudgetAreaParameters(BUDGET_AREA_PARENT_TYPE, BUDGET_AREA_PARENT_ID, FISCAL_YEAR);
    }
    
    private static BudgetContent generateBudget(Long id) {
        return new BudgetContent(Budget.builder()
            .budgetId(id)
            .estimatedBudget(ESTIMATED_BUDGET)
            .projectId(PROJECT_ID)
            .status(Status.ACTIVE)
            .budgetVersion(generateBudgetVersion())
            .expenses(List.of())
            .build());
    }
    
    private static BudgetVersion generateBudgetVersion() {
        return BudgetVersion.builder()
            .budgetVersionId(BUDGET_VERSION_ID)
            .budgetVersionName(BUDGET_VERSION_NAME)
            .budgetVersionDate(BUDGET_VERSION_DATE)
            .budgetArea(generateBudgetArea())
            .build();
    }
    
    private static BudgetArea generateBudgetArea() {
        return BudgetArea.builder()
            .budgetAreaId(BUDGET_AREA_ID)
            .parentType(BUDGET_AREA_PARENT_TYPE)
            .parentId(BUDGET_AREA_PARENT_ID)
            .fiscalYear(FISCAL_YEAR)
            .build();
    }
    
    private static BudgetArea generateBudgetArea2() {
        return BudgetArea.builder()
            .budgetAreaId(BUDGET_AREA_ID_2)
            .parentType(BUDGET_AREA_PARENT_TYPE)
            .parentId(BUDGET_AREA_PARENT_ID)
            .fiscalYear(FISCAL_YEAR_2)
            .build();
    }
    
    private static Expenses generateExpenses(Long id, BudgetContent budget) {
        return Expenses.builder()
            .expensesId(id)
            .priceItemId(PRICE_ITEM_ID)
            .comment(COMMENT)
            .cost(COST)
            .internalCost(INTERNAL_COST)
            .costPerUnit(COST_PER_UNIT)
            .internalPercent(PERCENT_INTERNAL)
            .units(UNITS)
            .invoicingTypeOption(INVOICING_TYPE_OPTION)
            .budget(budget.budget())
            .build();
    }
    
    private static Expenses generateExpenses2(Long id, BudgetContent budget) {
        return Expenses.builder()
            .expensesId(id)
            .priceItemId(PRICE_ITEM_ID_2)
            .comment(COMMENT_2)
            .cost(COST_2)
            .internalCost(INTERNAL_COST_2)
            .costPerUnit(COST_PER_UNIT_2)
            .internalPercent(PERCENT_INTERNAL_2)
            .units(UNITS_2)
            .invoicingTypeOption(INVOICING_TYPE_OPTION_2)
            .budget(budget.budget())
            .build();
    }
}
