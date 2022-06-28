package com.ikea.imc.pam.budget.service.configuration;

import com.ikea.imc.pam.budget.service.client.dto.*;
import com.ikea.imc.pam.budget.service.repository.model.Budget;
import com.ikea.imc.pam.budget.service.repository.model.BudgetArea;
import com.ikea.imc.pam.budget.service.repository.model.BudgetVersion;
import com.ikea.imc.pam.budget.service.repository.model.Expenses;
import com.ikea.imc.pam.budget.service.repository.model.utils.InvoicingTypeOption;
import com.ikea.imc.pam.budget.service.service.entity.BudgetAreaParameters;
import com.ikea.imc.pam.budget.service.service.entity.BudgetContent;
import com.ikea.imc.pam.common.repository.model.UserInformation;
import com.ikea.imc.pam.common.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetMapperTest {
    
    private static final Long BUDGET_ID = 1L;
    private static final Long BUDGET_VERSION_ID = 3L;
    private static final Long BUDGET_AREA_ID = 300L;
    private static final String NOTE = "test note";
    private static final Long PROJECT_ID = 2L;
    private static final Long EXPENSE_ID = 4L, EXPENSE_ID_2 = 112L;
    private static final Long PRICE_ITEM_ID = 5L;
    
    private static final BudgetParentType BUDGET_PARENT_TYPE = BudgetParentType.BUSINESS_AREA;
    
    private static final Long BUDGET_AREA_PARENT_ID = 3456L;
    
    private static final String BUDGET_VERSION_NAME = "budname";
    private static final LocalDate BUDGET_VERSION_DATE = LocalDate.of(2020, 3, 1);
    
    private static final String EXPENSE_COMMENT = "EXPENSE_COMMENT123";
    private static final int EXPENSE_COST = 450;
    private static final double EXPENSE_COST_INTERNAL = 400d;
    private static final int EXPENSE_COST_PER_UNIT = 40;
    private static final byte EXPENSE_PERCENT_INTERNAL = 80;
    private static final double EXPENSE_FRACTION_INTERNAL = EXPENSE_PERCENT_INTERNAL / 100d;
    private static final double EXPENSE_UNITS = 41.0;
    private static final InvoicingTypeOption EXPENSES_INVOICINGTYPEOPTION = InvoicingTypeOption.FIXED_PRICE;
    
    private static final Integer FISCAL_YEAR = 2020;
    private static final Long ESTIMATED_BUDGET = 100_000L;
    private static final Long ESTIMATED_COST = 100_000L;
    
    private static final String LAST_UPDATED_BY_ID = "user-id-1000-010000";
    private static final String LAST_UPDATED_BY_FIRST_NAME = "Last Updaters first";
    private static final String LAST_UPDATED_BY_SURNAME = "surname";
    private static final String LAST_UPDATED_BY_FULL_NAME = LAST_UPDATED_BY_FIRST_NAME + " " + LAST_UPDATED_BY_SURNAME;
    
    // String with date and time with max precision = nano
    private static final String LAST_UPDATED_AT_INPUT_DATE_STRING = "2020-03-03T10:11:12.123456789Z";
    // Instant max precision = nano
    private static final Instant LAST_UPDATED_AT_INPUT_INSTANT_NANO_PRECISION =
        Instant.parse(LAST_UPDATED_AT_INPUT_DATE_STRING);
    
    @Mock
    private UserService userService;
    
    @InjectMocks
    private BudgetMapper budgetMapper;
    
    @Test
    void buildBudgetDTO() {
        
        // Given
        Budget budget = generateBudget();
        budget.setExpenses(List.of(generateExpense(budget, EXPENSE_ID), generateExpense(budget, EXPENSE_ID_2)));
        budget.setNote(NOTE);
        when(userService.getUserInformation(LAST_UPDATED_BY_ID)).thenReturn(generateUserInformation());
        BudgetContent budgetContent = new BudgetContent(budget);
        
        // When
        BudgetDTO dto = budgetMapper.buildBudgetDTO(budgetContent);
        
        // Then
        assertNotNull(dto);
        assertEquals(BUDGET_ID, dto.getId());
        assertEquals(PROJECT_ID, dto.getProjectId());
        assertEquals(FISCAL_YEAR, dto.getFiscalYear());
        assertEquals(ESTIMATED_COST, dto.getEstimatedCost());
        assertEquals(NOTE, dto.getNote());
        assertEquals(LAST_UPDATED_BY_FULL_NAME, dto.getLastUpdatedByName());
        assertEquals(
            LAST_UPDATED_AT_INPUT_INSTANT_NANO_PRECISION.truncatedTo(ChronoUnit.MICROS),
            dto.getLastUpdatedAt()
        );
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
        assertEquals(EXPENSE_FRACTION_INTERNAL, dto.getInternalFraction());
        assertEquals(EXPENSE_COST_INTERNAL, dto.getInternalCost());
        assertEquals(EXPENSE_COST_PER_UNIT, expenses.getCostPerUnit());
        assertEquals(EXPENSE_UNITS, dto.getUnitCount());
        assertEquals(EXPENSE_COMMENT, dto.getComment());
        assertEquals(EXPENSES_INVOICINGTYPEOPTION.getDescription(), dto.getPriceModel());
    }
    
    @Test
    void buildBudgetAreaParameters() {
        
        // Given
        BudgetDTO dto = generateBudgetDTO();
        
        // When
        BudgetAreaParameters budgetAreaParameters = budgetMapper.buildBudgetAreaParameters(dto);
        
        // Then
        assertEquals(BUDGET_PARENT_TYPE, budgetAreaParameters.parentType());
        assertEquals(BUDGET_AREA_PARENT_ID, budgetAreaParameters.parentId());
        assertEquals(FISCAL_YEAR, budgetAreaParameters.fiscalYear());
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
        assertEquals(NOTE, budget.getNote());
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
        assertEquals(NOTE, budget.getNote());
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
        assertEquals(EXPENSE_COST_INTERNAL, expenses.getInternalCost());
        assertEquals(EXPENSE_COST_PER_UNIT, expenses.getCostPerUnit());
        assertEquals(EXPENSE_PERCENT_INTERNAL, expenses.getInternalPercent());
        assertEquals(EXPENSE_UNITS, expenses.getUnits());
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
        assertEquals(EXPENSE_COST_INTERNAL, expenses.getInternalCost());
        assertEquals(EXPENSE_COST_PER_UNIT, expenses.getCostPerUnit());
        assertEquals(EXPENSE_PERCENT_INTERNAL, expenses.getInternalPercent());
        assertEquals(EXPENSE_UNITS, expenses.getUnits());
    }
    
    private static BudgetArea generateBudgetArea() {
        return BudgetArea.builder()
            .budgetAreaId(BUDGET_AREA_ID)
            .parentType(BUDGET_PARENT_TYPE)
            .parentId(BUDGET_AREA_PARENT_ID)
            .fiscalYear(FISCAL_YEAR)
            .build();
    }
    
    private static BudgetVersion generateBudgetVersion() {
        return BudgetVersion.builder()
            .budgetVersionId(BUDGET_VERSION_ID)
            .budgetVersionName(BUDGET_VERSION_NAME)
            .budgetVersionDate(BUDGET_VERSION_DATE)
            .budgetArea(generateBudgetArea())
            .build();
    }
    
    private static Budget generateBudget() {
        Budget budget = Budget.builder()
            .budgetId(BUDGET_ID)
            .estimatedBudget(ESTIMATED_BUDGET)
            .projectId(PROJECT_ID)
            .note(NOTE)
            .budgetVersion(generateBudgetVersion())
            .expenses(List.of())
            .build();
        
        ReflectionTestUtils.setField(budget, "lastUpdatedById", LAST_UPDATED_BY_ID);
        ReflectionTestUtils.setField(
            budget,
            "lastUpdated",
            LAST_UPDATED_AT_INPUT_INSTANT_NANO_PRECISION.truncatedTo(ChronoUnit.MICROS)
        );
        
        return budget;
    }
    
    private static Expenses generateExpense(Budget budget, Long id) {
        Expenses expenses = Expenses.builder()
            .expensesId(id)
            .priceItemId(PRICE_ITEM_ID)
            .comment(EXPENSE_COMMENT)
            .cost(EXPENSE_COST)
            .internalCost(EXPENSE_COST_INTERNAL)
            .costPerUnit(EXPENSE_COST_PER_UNIT)
            .internalPercent(EXPENSE_PERCENT_INTERNAL)
            .units(EXPENSE_UNITS)
            .invoicingTypeOption(EXPENSES_INVOICINGTYPEOPTION)
            .budget(budget)
            .build();
        
        ReflectionTestUtils.setField(expenses, "lastUpdatedById", LAST_UPDATED_BY_ID);
        ReflectionTestUtils.setField(
            expenses,
            "lastUpdated",
            LAST_UPDATED_AT_INPUT_INSTANT_NANO_PRECISION.truncatedTo(ChronoUnit.MICROS)
        );
        
        return expenses;
    }
    
    private static BudgetDTO generateBudgetDTO() {
        return BudgetDTO.builder()
            .id(BUDGET_ID)
            .parentType(BUDGET_PARENT_TYPE)
            .parentId(BUDGET_AREA_PARENT_ID)
            .projectId(PROJECT_ID)
            .fiscalYear(FISCAL_YEAR)
            .estimatedCost(ESTIMATED_COST)
            .note(NOTE)
            .lastUpdatedAt(LAST_UPDATED_AT_INPUT_INSTANT_NANO_PRECISION.truncatedTo(ChronoUnit.MICROS))
            .lastUpdatedByName(LAST_UPDATED_BY_FULL_NAME)
            .expenses(List.of())
            .build();
    }
    
    private static ExpenseDTO generateExpenseDTO() {
        return ExpenseDTO.builder()
            .id(EXPENSE_ID)
            .priceItemId(PRICE_ITEM_ID)
            .budgetId(BUDGET_ID)
            .internalFraction(EXPENSE_FRACTION_INTERNAL)
            .internalCost(EXPENSE_COST_INTERNAL)
            .unitCost(EXPENSE_COST_PER_UNIT)
            .unitCount(EXPENSE_UNITS)
            .comment(EXPENSE_COMMENT)
            .priceModel(EXPENSES_INVOICINGTYPEOPTION.getDescription())
            .build();
    }
    
    private static PatchBudgetDTO generatePatchBudgetDTO() {
        return PatchBudgetDTO.builder().estimatedCost(ESTIMATED_COST).fiscalYear(FISCAL_YEAR).note(NOTE).build();
    }
    
    private static PatchExpenseDTO generatePatchExpenseDTO() {
        return PatchExpenseDTO.builder()
            .id(EXPENSE_ID)
            .internalFraction(EXPENSE_FRACTION_INTERNAL)
            .unitCost(EXPENSE_COST_PER_UNIT)
            .internalCost(EXPENSE_COST_INTERNAL)
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
