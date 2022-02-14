package ikea.imc.pam.budget.service.repository.model;

import java.time.LocalDate;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class BudgetVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long budgetVersionId;

    private String budgetVersionName;

    private int fiscalYear;

    private LocalDate budgetVersionDate;
}
