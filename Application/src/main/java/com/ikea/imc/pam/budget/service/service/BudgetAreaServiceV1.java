package com.ikea.imc.pam.budget.service.service;

import com.ikea.imc.pam.budget.service.repository.BudgetAreaRepository;
import com.ikea.imc.pam.budget.service.repository.model.BudgetArea;
import com.ikea.imc.pam.budget.service.service.entity.BudgetAreaParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class BudgetAreaServiceV1 implements BudgetAreaService {
    
    private final BudgetAreaRepository budgetAreaRepository;
    
    public BudgetAreaServiceV1(BudgetAreaRepository budgetAreaRepository) {
        this.budgetAreaRepository = budgetAreaRepository;
    }
    
    @Override
    public Optional<BudgetArea> getBudgetArea(Long budgetAreaId) {
        return budgetAreaRepository.findById(budgetAreaId);
    }
    
    @Override
    public Optional<BudgetArea> findBudgetArea(BudgetAreaParameters budgetAreaParameters) {
        return budgetAreaRepository.findBudgetAreaByParentAndFiscalYear(budgetAreaParameters.parentType(),
            budgetAreaParameters.parentId(),
            budgetAreaParameters.fiscalYear()
        );
    }
    
    @Override
    public synchronized BudgetArea putBudgetArea(BudgetArea budgetArea) {
        BudgetAreaParameters budgetAreaParameters =
            new BudgetAreaParameters(budgetArea.getParentType(), budgetArea.getParentId(), budgetArea.getFiscalYear());
        Optional<BudgetArea> currentBudgetAreaOptional = findBudgetArea(budgetAreaParameters);
        
        BudgetArea currentBudgetArea =
            currentBudgetAreaOptional.map(area -> BudgetArea.merge(area, budgetArea)).orElse(budgetArea);
        
        log.debug("Save BudgetArea {}", currentBudgetArea);
        
        return budgetAreaRepository.saveAndFlush(currentBudgetArea);
    }
}
