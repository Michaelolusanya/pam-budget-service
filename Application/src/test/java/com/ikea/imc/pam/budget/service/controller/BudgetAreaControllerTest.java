package com.ikea.imc.pam.budget.service.controller;

import com.ikea.imc.pam.budget.service.client.dto.BudgetAreaDTO;
import com.ikea.imc.pam.budget.service.client.dto.BudgetParentType;
import com.ikea.imc.pam.budget.service.configuration.BudgetAreaMapper;
import com.ikea.imc.pam.budget.service.repository.model.BudgetArea;
import com.ikea.imc.pam.budget.service.service.BudgetAreaService;
import com.ikea.imc.pam.budget.service.service.entity.BudgetAreaParameters;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BudgetAreaControllerTest {
    
    private static final Long BUDGET_AREA_ID = 123L;
    private static final BudgetParentType PARENT_TYPE = BudgetParentType.BUSINESS_AREA;
    private static final Long PARENT_ID = 3456L;
    private static final Integer FISCAL_YEAR = 2000;
    private static final Long COST_LIMIT = 200_300L;
    
    @Mock
    BudgetAreaMapper budgetAreaMapper;
    
    @Mock
    BudgetAreaService budgetAreaService;
    
    @InjectMocks
    BudgetAreaController controller;
    
    @Nested
    class GetBudgetAreaTest {
        
        @Test
        void foundBudgetArea() {
            
            // Given
            BudgetArea budgetArea = generateBudgetArea();
            when(budgetAreaService.getBudgetArea(BUDGET_AREA_ID)).thenReturn(Optional.of(budgetArea));
            when(budgetAreaMapper.buildBudgetAreaDTO(budgetArea)).thenReturn(generateBudgetAreaDTO());
            
            // When
            var response = controller.getBudgetArea(BUDGET_AREA_ID);
            
            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            var responseMessage = response.getBody();
            assertNotNull(responseMessage);
            assertEquals(200, responseMessage.getStatusCode());
            var dto = responseMessage.getData();
            assertNotNull(dto);
            assertEquals(BUDGET_AREA_ID, dto.budgetAreaId());
        }
        
        @Test
        void notFound() {
            
            // Given
            when(budgetAreaService.getBudgetArea(BUDGET_AREA_ID)).thenReturn(Optional.empty());
            
            // When
            var response = controller.getBudgetArea(BUDGET_AREA_ID);
            
            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            var responseMessage = response.getBody();
            assertNotNull(responseMessage);
            assertEquals(404, responseMessage.getStatusCode());
            assertEquals("BudgetArea 123 not found", responseMessage.getMessage());
        }
    }
    
    @Nested
    class FindBudgetAreaTest {
        
        @Test
        void foundBudgetArea() {
            
            // Given
            BudgetArea budgetArea = generateBudgetArea();
            BudgetAreaParameters parameters = new BudgetAreaParameters(PARENT_TYPE, PARENT_ID, FISCAL_YEAR);
            when(budgetAreaService.findBudgetArea(parameters)).thenReturn(Optional.of(budgetArea));
            when(budgetAreaMapper.buildBudgetAreaDTO(budgetArea)).thenReturn(generateBudgetAreaDTO());
            
            // When
            var response = controller.findBudgetArea(PARENT_TYPE, PARENT_ID, FISCAL_YEAR);
            
            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            var responseMessage = response.getBody();
            assertNotNull(responseMessage);
            assertEquals(200, responseMessage.getStatusCode());
            var dto = responseMessage.getData();
            assertNotNull(dto);
            assertEquals(BUDGET_AREA_ID, dto.budgetAreaId());
        }
        
        @Test
        void notFound() {
            
            // Given
            BudgetAreaParameters parameters = new BudgetAreaParameters(PARENT_TYPE, PARENT_ID, FISCAL_YEAR);
            when(budgetAreaService.findBudgetArea(parameters)).thenReturn(Optional.empty());
            
            // When
            var response = controller.findBudgetArea(PARENT_TYPE, PARENT_ID, FISCAL_YEAR);
            
            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            var responseMessage = response.getBody();
            assertNotNull(responseMessage);
            assertEquals(404, responseMessage.getStatusCode());
            assertNotNull(responseMessage.getMessage());
        }
    }
    
    @Nested
    class PutBudgetAreaTest {
        
        @Test
        void updatedBudgetArea() {
            
            // Given
            BudgetAreaDTO incomingDTO = generateBudgetAreaDTO();
            BudgetArea incomingBudgetArea = generateBudgetArea();
            incomingBudgetArea.setBudgetAreaId(null);
            BudgetArea outgoingBudgetArea = generateBudgetArea();
            
            when(budgetAreaMapper.buildBudgetArea(incomingDTO)).thenReturn(incomingBudgetArea);
            when(budgetAreaMapper.buildBudgetAreaDTO(outgoingBudgetArea)).thenReturn(generateBudgetAreaDTO());
            when(budgetAreaService.putBudgetArea(incomingBudgetArea)).thenReturn(outgoingBudgetArea);
            
            // When
            var response = controller.putBudgetArea(incomingDTO);
            
            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            var responseMessage = response.getBody();
            assertNotNull(responseMessage);
            assertEquals(200, responseMessage.getStatusCode());
            var dto = responseMessage.getData();
            assertNotNull(dto);
            assertEquals(BUDGET_AREA_ID, dto.budgetAreaId());
        }
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
