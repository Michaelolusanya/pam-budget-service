package ikea.imc.pam.budget.service.repository.model;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long budgetId;

    private long projectId;

    private long estimatedBudget;

    private BigDecimal costCOMDEV;

    @ManyToOne
    @JoinColumn(name = "budgetVersionId")
    private BudgetVersion budgetVersion;
}
