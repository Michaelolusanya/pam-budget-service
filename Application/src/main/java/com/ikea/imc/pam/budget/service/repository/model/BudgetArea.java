package com.ikea.imc.pam.budget.service.repository.model;


import com.ikea.imc.pam.budget.service.client.dto.BudgetParentType;
import com.ikea.imc.pam.budget.service.service.entity.BudgetAreaParameters;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@ToString
@NamedQuery(name = "BudgetArea.findBudgetAreaByParentAndFiscalYear",
    query = "select b from BudgetArea b where b.parentType = :parentType AND b.parentId = :parentId AND b.fiscalYear " +
        "= :fiscalYear")
public class BudgetArea extends AbstractEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long budgetAreaId;
    
    @Enumerated(EnumType.STRING)
    private BudgetParentType parentType;
    
    private Long parentId;
    
    private Long costLimit;
    
    private Integer fiscalYear;
    
    public static BudgetArea merge(BudgetArea fromBudgetArea, BudgetArea toBudgetArea) {
        
        BudgetAreaBuilder mergedBudgetArea = fromBudgetArea.toBuilder();
        
        setNotNullValue(toBudgetArea::getCostLimit, mergedBudgetArea::costLimit);
        
        return mergeLastUpdated(fromBudgetArea, mergedBudgetArea.build());
    }
    
    public static BudgetArea toBudgetArea(BudgetAreaParameters budgetAreaParameters) {
        return BudgetArea.builder()
            .parentType(budgetAreaParameters.parentType())
            .parentId(budgetAreaParameters.parentId())
            .fiscalYear(budgetAreaParameters.fiscalYear())
            .build();
    }
}
