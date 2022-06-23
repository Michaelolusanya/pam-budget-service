package com.ikea.imc.pam.budget.service.service;

import com.ikea.imc.pam.budget.service.repository.model.BudgetArea;
import com.ikea.imc.pam.budget.service.service.entity.BudgetAreaParameters;

import java.util.Optional;

public interface BudgetAreaService {
    
    Optional<BudgetArea> getBudgetArea(Long budgetAreaId);
    
    Optional<BudgetArea> findBudgetArea(BudgetAreaParameters budgetAreaParameters);
    
    BudgetArea putBudgetArea(BudgetArea budgetArea);
}
