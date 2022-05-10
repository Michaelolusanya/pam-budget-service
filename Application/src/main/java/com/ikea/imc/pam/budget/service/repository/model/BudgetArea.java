package com.ikea.imc.pam.budget.service.repository.model;


import com.ikea.imc.pam.budget.service.client.dto.BudgetParentType;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@NamedQuery(
        name = "BudgetArea.findBudgetAreaByParentAndFiscalYear",
        query = "select b from BudgetArea b where b.parentType = :parentType AND b.parentId = :parentId AND b.fiscalYear = :fiscalYear")
public class BudgetArea extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long budgetAreaId;

    private BudgetParentType parentType;

    private Long parentId;

    private Long costLimit;

    private Integer fiscalYear;
}
