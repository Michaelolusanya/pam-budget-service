package com.ikea.imc.pam.budget.service.repository.model;

import java.time.LocalDate;
import javax.persistence.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
