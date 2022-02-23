package ikea.imc.pam.budget.service.repository.model;

import java.time.LocalDate;
import javax.persistence.*;
import lombok.*;

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

    private String budgetVersionName;

    private int fiscalYear;

    private LocalDate budgetVersionDate;
}
