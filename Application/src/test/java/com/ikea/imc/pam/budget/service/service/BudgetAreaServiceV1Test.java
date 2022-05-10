package com.ikea.imc.pam.budget.service.service;

import com.ikea.imc.pam.budget.service.client.dto.BudgetParentType;
import com.ikea.imc.pam.budget.service.repository.BudgetAreaRepository;
import com.ikea.imc.pam.budget.service.repository.model.BudgetArea;
import com.ikea.imc.pam.budget.service.service.entity.BudgetAreaParameters;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BudgetAreaServiceV1Test {

    private static final Long BUDGET_AREA_ID = 1L;
    private static final BudgetParentType PARENT_TYPE = BudgetParentType.BUSINESS_AREA;
    private static final Long PARENT_ID = 20L;
    private static final Long COST_LIMIT = 100_000L;
    private static final Integer FISCAL_YEAR = 2000;

    @Mock
    private BudgetAreaRepository budgetAreaRepository;

    @InjectMocks
    private BudgetAreaServiceV1 service;

    @Nested
    class GetBudgetAreaTest {

        @Test
        void found() {

            // Given
            when(budgetAreaRepository.findById(BUDGET_AREA_ID)).thenReturn(Optional.of(generateBudgetArea()));

            // When
            Optional<BudgetArea> budgetArea = service.getBudgetArea(BUDGET_AREA_ID);

            // Then
            assertTrue(budgetArea.isPresent());
            assertEquals(BUDGET_AREA_ID, budgetArea.get().getBudgetAreaId());
        }

        @Test
        void notFound() {

            // Given
            when(budgetAreaRepository.findById(BUDGET_AREA_ID)).thenReturn(Optional.empty());

            // When
            Optional<BudgetArea> budgetArea = service.getBudgetArea(BUDGET_AREA_ID);

            // Then
            assertTrue(budgetArea.isEmpty());
        }

    }

    @Nested
    class FindBudgetAreaTest {

        @Test
        void found() {

            // Given
            BudgetAreaParameters budgetAreaParameters = new BudgetAreaParameters(PARENT_TYPE, PARENT_ID, FISCAL_YEAR);
            when(budgetAreaRepository.findBudgetAreaByParentAndFiscalYear(PARENT_TYPE, PARENT_ID, FISCAL_YEAR))
                    .thenReturn(Optional.of(generateBudgetArea()));

            // When
            Optional<BudgetArea> budgetArea = service.findBudgetArea(budgetAreaParameters);

            // Then
            assertTrue(budgetArea.isPresent());
            assertEquals(BUDGET_AREA_ID, budgetArea.get().getBudgetAreaId());
        }

        @Test
        void notFound() {

            // Given
            BudgetAreaParameters budgetAreaParameters = new BudgetAreaParameters(PARENT_TYPE, PARENT_ID, FISCAL_YEAR);
            when(budgetAreaRepository.findBudgetAreaByParentAndFiscalYear(PARENT_TYPE, PARENT_ID, FISCAL_YEAR))
                    .thenReturn(Optional.empty());

            // When
            Optional<BudgetArea> budgetArea = service.findBudgetArea(budgetAreaParameters);

            // Then
            assertTrue(budgetArea.isEmpty());
        }
    }

    @Nested
    class PutBudgetAreaTest {

        @Captor
        private ArgumentCaptor<BudgetArea> budgetAreaCaptor;

        @Test
        void noneExistent() {

            // Given
            BudgetArea toAddBudget = generateBudgetArea();
            toAddBudget.setBudgetAreaId(null);
            when(budgetAreaRepository.findBudgetAreaByParentAndFiscalYear(PARENT_TYPE, PARENT_ID, FISCAL_YEAR))
                    .thenReturn(Optional.empty());
            when(budgetAreaRepository.saveAndFlush(toAddBudget)).thenReturn(generateBudgetArea());

            // When
            BudgetArea addedBudgetArea = service.putBudgetArea(toAddBudget);

            // Then
            assertEquals(BUDGET_AREA_ID, addedBudgetArea.getBudgetAreaId());
        }

        @Test
        void alreadyExists() {

            // Given
            BudgetArea existingBudgetArea = generateBudgetArea();
            existingBudgetArea.setCostLimit(null);

            BudgetArea toUpdateBudget = generateBudgetArea();
            toUpdateBudget.setBudgetAreaId(null);

            when(budgetAreaRepository.findBudgetAreaByParentAndFiscalYear(PARENT_TYPE, PARENT_ID, FISCAL_YEAR))
                    .thenReturn(Optional.of(existingBudgetArea));
            when(budgetAreaRepository.saveAndFlush(budgetAreaCaptor.capture())).thenReturn(generateBudgetArea());

            // When
            BudgetArea addedBudgetArea = service.putBudgetArea(toUpdateBudget);

            // Then
            assertEquals(BUDGET_AREA_ID, addedBudgetArea.getBudgetAreaId());

            BudgetArea savedBudgetArea = budgetAreaCaptor.getValue();
            assertEquals(BUDGET_AREA_ID, savedBudgetArea.getBudgetAreaId());
            assertEquals(COST_LIMIT, savedBudgetArea.getCostLimit());
        }

    }

    private static BudgetArea generateBudgetArea() {
        return BudgetArea
                .builder()
                .budgetAreaId(BUDGET_AREA_ID)
                .parentType(PARENT_TYPE)
                .parentId(PARENT_ID)
                .costLimit(COST_LIMIT)
                .fiscalYear(FISCAL_YEAR)
                .build();
    }
}
