package com.ikea.imc.pam.budget.service.service.entity;

import com.ikea.imc.pam.budget.service.client.dto.BudgetParentType;

public record BudgetAreaParameters(BudgetParentType parentType, Long parentId, Integer fiscalYear) {}
