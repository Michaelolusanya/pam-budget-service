package com.ikea.imc.pam.budget.service.configuration;

import com.ikea.imc.pam.budget.service.client.dto.BudgetAreaDTO;
import com.ikea.imc.pam.budget.service.repository.model.BudgetArea;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier()
public class BudgetAreaMapper {
    
    public BudgetArea buildBudgetArea(BudgetAreaDTO dto) {
        return BudgetArea.builder()
            .budgetAreaId(dto.budgetAreaId())
            .parentType(dto.parentType())
            .parentId(dto.parentId())
            .costLimit(dto.costLimit())
            .fiscalYear(dto.fiscalYear())
            .build();
    }
    
    public BudgetAreaDTO buildBudgetAreaDTO(BudgetArea budgetArea) {
        return BudgetAreaDTO.builder()
            .budgetAreaId(budgetArea.getBudgetAreaId())
            .parentType(budgetArea.getParentType())
            .parentId(budgetArea.getParentId())
            .fiscalYear(budgetArea.getFiscalYear())
            .costLimit(budgetArea.getCostLimit())
            .build();
    }
}
