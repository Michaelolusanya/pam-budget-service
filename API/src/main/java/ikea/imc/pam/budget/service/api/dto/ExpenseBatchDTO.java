package ikea.imc.pam.budget.service.api.dto;

import java.util.List;
import javax.validation.Valid;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpenseBatchDTO {

    @Valid private List<ExpenseDTO> data;
}
