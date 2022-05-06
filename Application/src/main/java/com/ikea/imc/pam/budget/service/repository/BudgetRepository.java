package com.ikea.imc.pam.budget.service.repository;

import com.ikea.imc.pam.budget.service.repository.model.Budget;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> getBudgetByProjectId(@Param("projectIds") List<Long> projectIds);

    List<Budget> getBudgetByFiscalYear(@Param("fiscalYears") List<Integer> fiscalYears);

    List<Budget> getBudgetByProjectIdAndFiscalYear(
            @Param("projectIds") List<Long> projectIds, @Param("fiscalYears") List<Integer> fiscalYears);

    List<Budget> getAllActive();
}
