package ikea.imc.pam.budget.service.controller;

import ikea.imc.pam.budget.service.api.Paths;
import ikea.imc.pam.budget.service.api.dto.*;
import ikea.imc.pam.budget.service.api.dto.ResponseMessageDTO;
import ikea.imc.pam.budget.service.controller.dto.ResponseEntityFactory;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Paths.BUDGET_V1_ENDPOINT)
public class BudgetController {

    @Operation(summary = "Get budget by id")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO<ResponseBudgetDTO>> getBudget(@PathVariable Long id) {
        return ResponseEntityFactory.generateResponse(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Get budgets by filters")
    @GetMapping("/")
    public ResponseEntity<ResponseMessageDTO<List<ResponseBudgetDTO>>> findBudgets(
            @RequestParam(required = false, name = "hfbIds") List<Long> hfbIds,
            @RequestParam(required = false, name = "fiscalYears") List<String> fiscalYears) {
        return ResponseEntityFactory.generateResponse(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Delete budget by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO<ResponseBudgetDTO>> deleteBudget(@PathVariable Long id) {
        return ResponseEntityFactory.generateResponse(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Create a new budget")
    @PostMapping
    public ResponseEntity<ResponseMessageDTO<ResponseBudgetDTO>> createBudget(
            @Valid @RequestBody RequestBudgetDTO dto) {
        return ResponseEntityFactory.generateResponse(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Update budget by Id")
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO<ResponseBudgetDTO>> updateBudget(
            @PathVariable Long id, @Valid @RequestBody RequestPartialBudgetDTO dto) {
        return ResponseEntityFactory.generateResponse(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Update a budget expense by budgetId and the expenseId")
    @PatchMapping("/{id}/expenses/{expenseId}")
    public ResponseEntity<ResponseMessageDTO<ResponseExpenseDTO>> updateExpense(
            @PathVariable Long id, @PathVariable Long expenseId, @Valid @RequestBody RequestPartialExpenseDTO dto) {
        return ResponseEntityFactory.generateResponse(HttpStatus.NOT_IMPLEMENTED);
    }
}
