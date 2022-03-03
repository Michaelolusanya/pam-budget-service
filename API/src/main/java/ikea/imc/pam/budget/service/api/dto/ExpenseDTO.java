package ikea.imc.pam.budget.service.api.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ExpenseDTO {

    @Min(Constants.MINIMUM_ID)
    private Long id;

    private Long budgetId;

    @NotNull
    @Min(Constants.MINIMUM_ID)
    private Long assetTypeId;

    @NotNull
    @Min(Constants.MINIMUM_FRACTION)
    @Max(Constants.MAXIMUM_FRACTION)
    private Double comdevFraction;

    @NotNull
    @Min(Constants.MINIMUM_COST)
    private Double comdevCost;

    @NotNull
    @Min(Constants.MINIMUM_COST)
    private Integer unitCost;

    @NotNull
    @Min(Constants.MINIMUM_COUNT)
    private Short unitCount;

    @NotNull
    @Min(Constants.MINIMUM_COUNT)
    private Byte weekCount;

    private String comment;
    private String priceModel;
}
