package com.ikea.imc.pam.budget.service.repository.model;

import com.ikea.imc.pam.budget.service.repository.model.utils.Status;

import java.util.List;
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
@Builder(toBuilder = true)
@NamedQuery(
        name = "Budget.getBudgetByProjectId",
        query = "select distinct b from Budget b "
                + "left join fetch b.expenses e "
                + "join fetch b.budgetVersion bv "
                + "join fetch bv.budgetArea ba "
                + "where b.projectId in :projectIds AND b.status <> 'ARCHIVED'")
@NamedQuery(
        name = "Budget.getBudgetByFiscalYear",
        query = "select distinct b from Budget b "
                + "left join fetch b.expenses e "
                + "join fetch b.budgetVersion bv "
                + "join fetch bv.budgetArea ba "
                + "where ba.fiscalYear in :fiscalYears AND b.status <> 'ARCHIVED'")
@NamedQuery(
        name = "Budget.getBudgetByProjectIdAndFiscalYear",
        query = "select distinct b from Budget b "
                + "left join fetch b.expenses e "
                + "join fetch b.budgetVersion bv "
                + "join fetch bv.budgetArea ba "
                + "where b.projectId in :projectIds AND ba.fiscalYear in :fiscalYears "
                + "AND b.status <> 'ARCHIVED'")
@NamedQuery(
        name = "Budget.getAllActive",
        query = "select distinct b from Budget b "
                + "left join fetch b.expenses e "
                + "join fetch b.budgetVersion bv "
                + "join fetch bv.budgetArea ba "
                + "where b.status <> 'ARCHIVED'")
public class Budget extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long budgetId;

    private Long projectId;

    private Long estimatedBudget;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "budgetVersionId")
    private BudgetVersion budgetVersion;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL)
    private List<Expenses> expenses;

    public static Budget merge(Budget fromBudget, Budget toBudget) {

        BudgetBuilder mergedBudget = fromBudget.toBuilder();

        setNotNullValue(toBudget::getProjectId, mergedBudget::projectId);
        setNotNullValue(toBudget::getEstimatedBudget, mergedBudget::estimatedBudget);
        setNotNullValue(toBudget::getStatus, mergedBudget::status);

        return mergeLastUpdated(fromBudget, mergedBudget.build());
    }

    public boolean isEqual(Budget compareTo) {

        if (compareTo == null) {
            return false;
        }

        return isEqual(
                Getter.of(this::getProjectId, compareTo::getProjectId),
                Getter.of(this::getEstimatedBudget, compareTo::getEstimatedBudget),
                Getter.of(this::getStatus, compareTo::getStatus));
    }
}
