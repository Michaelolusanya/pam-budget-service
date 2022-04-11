package ikea.imc.pam.budget.service.repository.model;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

    private String budgetVersionName;

    private int fiscalYear;

    private LocalDate budgetVersionDate;
}
