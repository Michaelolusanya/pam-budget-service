package com.ikea.imc.pam.budget.service.repository;

import com.ikea.imc.pam.budget.service.client.dto.BudgetParentType;
import com.ikea.imc.pam.budget.service.repository.model.BudgetArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BudgetAreaRepository extends JpaRepository<BudgetArea, Long> {

    Optional<BudgetArea> findBudgetAreaByParentAndFiscalYear(
            @Param("parentType") BudgetParentType parentType,
            @Param("parentId") Long parentId,
            @Param("fiscalYear") Integer fiscalYear);
}
