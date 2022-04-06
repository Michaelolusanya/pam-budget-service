package ikea.imc.pam.budget.service.repository.model;

import ikea.imc.pam.budget.service.repository.model.utils.Status;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@NamedQuery(
        name = "Budget.getBudgetByProjectId",
        query = "select b from Budget b where b.projectId in :projectIds AND b.status <> 'ARCHIVED'")
@NamedQuery(
        name = "Budget.getBudgetByFiscalYear",
        query =
                "select b from Budget b join fetch b.budgetVersion bv "
                        + "where bv.fiscalYear in :fiscalYears AND b.status <> 'ARCHIVED'")
@NamedQuery(
        name = "Budget.getBudgetByProjectIdAndFiscalYear",
        query =
                "select b from Budget b join fetch b.budgetVersion bv "
                        + "where b.projectId in :projectIds AND bv.fiscalYear in :fiscalYears "
                        + "AND b.status <> 'ARCHIVED'")
@NamedQuery(name = "Budget.getAllActive", query = "select b from Budget b where b.status <> 'ARCHIVED'")
public class Budget extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long budgetId;

    private Long projectId;

    private Long estimatedBudget;

    private Double costCOMDEV;

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
        setNotNullValue(toBudget::getCostCOMDEV, mergedBudget::costCOMDEV);
        setNotNullValue(toBudget::getStatus, mergedBudget::status);

        return mergedBudget.build();
    }

    public boolean isEqual(Budget compareTo) {

        if (compareTo == null) {
            return false;
        }

        return isEqual(
                Getter.of(this::getProjectId, compareTo::getProjectId),
                Getter.of(this::getEstimatedBudget, compareTo::getEstimatedBudget),
                Getter.of(this::getCostCOMDEV, compareTo::getCostCOMDEV),
                Getter.of(this::getStatus, compareTo::getStatus));
    }
}
