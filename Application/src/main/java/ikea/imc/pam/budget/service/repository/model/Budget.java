package ikea.imc.pam.budget.service.repository.model;

import java.util.List;
import javax.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@NamedQuery(name = "Budget.getBudgetByProjectId", query = "select b from Budget b where b.projectId in :projectIds")
@NamedQuery(
        name = "Budget.getBudgetByFiscalYear",
        query = "select b from Budget b join fetch b.budgetVersion bv " + "where bv.fiscalYear in :fiscalYears")
@NamedQuery(
        name = "Budget.getBudgetByProjectIdAndFiscalYear",
        query =
                "select b from Budget b join fetch b.budgetVersion bv "
                        + "where b.projectId in :projectIds AND bv.fiscalYear in :fiscalYears")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long budgetId;

    private Long projectId;

    private Long estimatedBudget;

    private Double costCOMDEV;

    @ManyToOne
    @JoinColumn(name = "budgetVersionId")
    private BudgetVersion budgetVersion;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL)
    private List<Expenses> expenses;
}
