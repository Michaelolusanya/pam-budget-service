package ikea.imc.pam.budget.service.api.dto;

import java.util.List;
import javax.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BudgetDTO {

    private Long id;

    @Min(1)
    private Long projectId;

    @NotBlank
    @Min(1900)
    @Max(9999)
    private Integer fiscalYear;

    @Min(0)
    private Long estimatedCost;

    private Double comdevCost;
    private List<ExpenseDTO> expenses;
}
