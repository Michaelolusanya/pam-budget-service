package ikea.imc.pam.budget.service.api.dto;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class BudgetDTO {

    private Long id;

    @NotNull
    @Min(Constants.MINIMUM_ID)
    private Long projectId;

    @NotNull
    @Min(Constants.MINIMUM_YEAR)
    @Max(Constants.MAXIMUM_YEAR)
    private Integer fiscalYear;

    @Min(Constants.MINIMUM_COST)
    @Max(Constants.MAXIMUM_COST)
    protected Long estimatedCost;

    @Min(Constants.MINIMUM_COST)
    private Double comdevCost;

    @NotNull @Valid private List<ExpenseDTO> expenses;
}
