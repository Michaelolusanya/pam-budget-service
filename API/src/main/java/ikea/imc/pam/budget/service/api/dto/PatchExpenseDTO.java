package ikea.imc.pam.budget.service.api.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatchExpenseDTO {

    @NotNull
    @Min(Constants.MINIMUM_ID)
    private Long id;

    @Min(Constants.MINIMUM_FRACTION)
    @Max(Constants.MAXIMUM_FRACTION)
    private Double comdevFraction;

    @Min(Constants.MINIMUM_COST)
    private Double comdevCost;

    @Min(Constants.MINIMUM_COST)
    private Integer unitCost;

    @Min(Constants.MINIMUM_COUNT)
    private Short unitCount;

    @Min(Constants.MINIMUM_COUNT)
    private Byte weekCount;

    private String comment;
}
