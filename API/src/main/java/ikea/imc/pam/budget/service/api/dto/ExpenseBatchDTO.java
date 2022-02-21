package ikea.imc.pam.budget.service.api.dto;

import java.util.List;
import javax.validation.Valid;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class ExpenseBatchDTO {

    @Valid private List<PatchExpenseDTO> data;
}
