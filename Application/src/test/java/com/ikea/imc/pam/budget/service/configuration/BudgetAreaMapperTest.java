package com.ikea.imc.pam.budget.service.configuration;

import com.ikea.imc.pam.budget.service.client.dto.BudgetAreaDTO;
import com.ikea.imc.pam.budget.service.client.dto.BudgetParentType;
import com.ikea.imc.pam.budget.service.repository.model.BudgetArea;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BudgetAreaMapperTest {
    
    private static final Long BUDGET_AREA_ID = 123L;
    private static final BudgetParentType PARENT_TYPE = BudgetParentType.BUSINESS_AREA;
    private static final Long PARENT_ID = 3456L;
    private static final Integer FISCAL_YEAR = 2000;
    private static final Long COST_LIMIT = 200_300L;
    
    @InjectMocks
    BudgetAreaMapper mapper;
    
    @Test
    void buildBudgetArea() {
        
        // Given
        BudgetAreaDTO dto = generateBudgetAreaDTO();
        
        // When
        BudgetArea budgetArea = mapper.buildBudgetArea(dto);
        
        // Then
        assertEquals(BUDGET_AREA_ID, budgetArea.getBudgetAreaId());
        assertEquals(PARENT_TYPE, budgetArea.getParentType());
        assertEquals(PARENT_ID, budgetArea.getParentId());
        assertEquals(FISCAL_YEAR, budgetArea.getFiscalYear());
        assertEquals(COST_LIMIT, budgetArea.getCostLimit());
        
    }
    
    @Test
    void buildBudgetAreaDTO() {
        
        // Given
        BudgetArea budgetArea = generateBudgetArea();
        
        // When
        BudgetAreaDTO dto = mapper.buildBudgetAreaDTO(budgetArea);
        
        // Then
        assertEquals(BUDGET_AREA_ID, dto.budgetAreaId());
        assertEquals(PARENT_TYPE, dto.parentType());
        assertEquals(PARENT_ID, dto.parentId());
        assertEquals(FISCAL_YEAR, dto.fiscalYear());
        assertEquals(COST_LIMIT, dto.costLimit());
    }
    
    private static BudgetAreaDTO generateBudgetAreaDTO() {
        return BudgetAreaDTO.builder()
            .budgetAreaId(BUDGET_AREA_ID)
            .parentType(PARENT_TYPE)
            .parentId(PARENT_ID)
            .fiscalYear(FISCAL_YEAR)
            .costLimit(COST_LIMIT)
            .build();
    }
    
    private static BudgetArea generateBudgetArea() {
        return BudgetArea.builder()
            .budgetAreaId(BUDGET_AREA_ID)
            .parentType(PARENT_TYPE)
            .parentId(PARENT_ID)
            .fiscalYear(FISCAL_YEAR)
            .costLimit(COST_LIMIT)
            .build();
    }
}
