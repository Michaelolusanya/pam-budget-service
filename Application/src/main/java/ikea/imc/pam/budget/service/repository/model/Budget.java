package ikea.imc.pam.budget.service.repository.model;

import ikea.imc.pam.budget.service.repository.model.utils.Status;
import java.util.List;
import java.util.Objects;
import javax.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
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
public class Budget {

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

    public boolean isEqual(Budget compareTo) {

        if (compareTo == null) {
            return false;
        }

        if (!Objects.equals(this.getProjectId(), compareTo.getProjectId())) {
            return false;
        }

        if (!Objects.equals(this.getEstimatedBudget(), compareTo.getEstimatedBudget())) {
            return false;
        }

        if (!Objects.equals(this.getStatus(), compareTo.getStatus())) {
            return false;
        }

        return true;
    }
}
