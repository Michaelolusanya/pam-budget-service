package ikea.imc.pam.budget.service.api.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatchBudgetDTO {

    @Min(Constants.MINIMUM_YEAR)
    @Max(Constants.MAXIMUM_YEAR)
    private Integer fiscalYear;

    @Min(Constants.MINIMUM_COST)
    @Max(Constants.MAXIMUM_COST)
    protected Long estimatedCost;

    @Min(Constants.MINIMUM_COST)
    private Double comdevCost;
}
