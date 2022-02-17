package ikea.imc.pam.budget.service.api.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpenseDTO {
    @NotNull
    @Min(1)
    private Long id;

    private Long budgetId;
    private Long assignmentId;
    private Long assetTypeId;
    private String name;

    @Min(0)
    @Max(1)
    private Double comdevFraction;

    @Min(0)
    private Double comdevCost;

    private Integer unitCost;
    private Short unitCount;
    private Byte weekCount;
    private String comment;
    private String priceModel;
}
