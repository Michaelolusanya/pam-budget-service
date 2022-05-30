package com.ikea.imc.pam.budget.service.service.entity;

import com.ikea.imc.pam.budget.service.client.dto.BudgetParentType;
import com.ikea.imc.pam.budget.service.repository.model.AbstractEntity;
import com.ikea.imc.pam.budget.service.repository.model.Budget;
import com.ikea.imc.pam.budget.service.repository.model.BudgetArea;
import com.ikea.imc.pam.budget.service.repository.model.Expenses;

import java.time.Instant;

public record BudgetContent(Budget budget) {

    public Long getBudgetId() {
        return budget.getBudgetId();
    }

    public BudgetParentType getParentType() {
        BudgetArea budgetArea = getBudgetArea();
        return budgetArea != null ? budgetArea.getParentType() : null;
    }

    public Long getParentId() {
        BudgetArea budgetArea = getBudgetArea();
        return budgetArea != null ? budgetArea.getParentId() : null;
    }

    public Integer getFiscalYear() {
        BudgetArea budgetArea = getBudgetArea();
        return budgetArea != null ? budgetArea.getFiscalYear() : null;
    }

    public String getLastUpdatedById() {
        return getLastUpdatedEntity().getLastUpdatedById();
    }

    public Instant getLastUpdatedAt() {
        return getLastUpdatedEntity().getLastUpdated();
    }

    private BudgetArea getBudgetArea() {
        return budget.getBudgetVersion() != null ? budget.getBudgetVersion().getBudgetArea() : null;
    }

    private AbstractEntity getLastUpdatedEntity() {

        AbstractEntity lastUpdatedEntity = budget;
        for (Expenses expense : budget.getExpenses()) {
            if (expense.getLastUpdated().compareTo(lastUpdatedEntity.getLastUpdated()) > 0) {
                lastUpdatedEntity = expense;
            }
        }

        return lastUpdatedEntity;
    }
}
