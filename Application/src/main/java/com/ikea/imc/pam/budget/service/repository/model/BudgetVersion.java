package com.ikea.imc.pam.budget.service.repository.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class BudgetVersion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long budgetVersionId;
    
    @ManyToOne
    @JoinColumn(name = "budgetAreaId")
    private BudgetArea budgetArea;
    
    private String budgetVersionName;
    
    private LocalDate budgetVersionDate;
}
