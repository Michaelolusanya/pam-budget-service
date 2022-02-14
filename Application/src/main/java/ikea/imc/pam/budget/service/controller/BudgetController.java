package ikea.imc.pam.budget.service.controller;

import ikea.imc.pam.budget.service.api.Paths;
import ikea.imc.pam.budget.service.api.dto.*;
import ikea.imc.pam.budget.service.api.dto.ResponseMessageDTO;
import ikea.imc.pam.budget.service.controller.dto.ResponseEntityFactory;
import ikea.imc.pam.budget.service.repository.model.Budget;
import ikea.imc.pam.budget.service.repository.model.Expenses;
import ikea.imc.pam.budget.service.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Paths.BUDGET_V1_ENDPOINT)
public class BudgetController {

    private static final Logger log = LogManager.getLogger(BudgetController.class);
    private final BudgetService budgetService;
    private final ModelMapper modelMapper;

    public BudgetController(BudgetService budgetService, ModelMapper modelMapper) {
        this.budgetService = budgetService;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "Get budget by id")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO<BudgetDTO>> getBudget(@PathVariable Long id) {
        return budgetService
                .getById(id)
                .map(budget -> ResponseEntityFactory.generateResponse(HttpStatus.OK, mapBudget(budget)))
                .orElseGet(
                        () -> {
                            log.warn("Could not find budget with id {}", id);
                            return getBudgetNotFoundResponse(id);
                        });
    }

    @Operation(summary = "List budgets by filtering project id and fiscal year")
    @GetMapping("/")
    public ResponseEntity<ResponseMessageDTO<List<BudgetDTO>>> findBudgets(
            @RequestParam(required = false, name = "projectIds") List<Long> projectIds,
            @RequestParam(required = false, name = "fiscalYears") List<String> fiscalYears) {

        return ResponseEntityFactory.generateResponse(
                HttpStatus.OK,
                budgetService.listBudgets(projectIds, fiscalYears).stream()
                        .map(this::mapBudget)
                        .collect(Collectors.toList()));
    }

    @Operation(summary = "Delete budget by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO<BudgetDTO>> deleteBudget(@PathVariable Long id) {
        return budgetService
                .deleteById(id)
                .map(budget -> ResponseEntityFactory.generateResponse(HttpStatus.OK, mapBudget(budget)))
                .orElseGet(() -> ResponseEntityFactory.generateResponse(HttpStatus.NO_CONTENT));
    }

    @Operation(summary = "Create a new budget")
    @PostMapping
    public ResponseEntity<ResponseMessageDTO<BudgetDTO>> createBudget(
            @Valid @RequestBody BudgetDTO dto) {
        Budget budget = modelMapper.map(dto, Budget.class);
        return ResponseEntityFactory.generateResponse(
                HttpStatus.CREATED, mapBudget(budgetService.createBudget(dto.getFiscalYear(), budget)));
    }

    @Operation(summary = "Update budget by Id")
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO<BudgetDTO>> updateBudget(
            @PathVariable Long id, @RequestBody BudgetDTO dto) {
        Budget budget = modelMapper.map(dto, Budget.class);
        return budgetService
                .patchBudget(id, dto.getFiscalYear(), budget)
                .map(updatedBudget -> ResponseEntityFactory.generateResponse(HttpStatus.OK, mapBudget(updatedBudget)))
                .orElseGet(
                        () -> {
                            log.warn("Could not update budget with id {}", id);
                            return getBudgetNotFoundResponse(id);
                        });
    }

    @Operation(summary = "Update a budget expense by budgetId and the expenseId")
    @PatchMapping("/{id}/expenses/{expenseId}")
    public ResponseEntity<ResponseMessageDTO<ExpenseDTO>> updateExpense(
            @PathVariable Long id, @PathVariable Long expenseId, @Valid @RequestBody ExpenseDTO dto) {
        Expenses expenses = modelMapper.map(dto, Expenses.class);
        Optional<Budget> optionalBudget = budgetService.getById(id);
        if (optionalBudget.isEmpty()) {
            log.warn("Could not update expenses, could not find budget with id {}", id);
            return getBudgetNotFoundResponse(id);
        }
        Budget budget = optionalBudget.get();

        return budgetService
                .patchExpense(budget, expenseId, expenses)
                .map(
                        updateExpense ->
                                ResponseEntityFactory.generateResponse(
                                        HttpStatus.OK, modelMapper.map(updateExpense, ExpenseDTO.class)))
                .orElseGet(
                        () -> {
                            log.warn("Could not update expenses with id {}", id);
                            return ResponseEntityFactory.generateResponseMessage(
                                    HttpStatus.NOT_FOUND, String.format("Expenses %d not found", expenseId));
                        });
    }

    private <T> ResponseEntity<ResponseMessageDTO<T>> getBudgetNotFoundResponse(Long id) {
        return ResponseEntityFactory.generateResponseMessage(
                HttpStatus.NOT_FOUND, String.format("Budget %d not found", id));
    }

    private BudgetDTO mapBudget(Budget budget) {
        BudgetDTO dto = modelMapper.map(budget, BudgetDTO.class);
        dto.setExpenses(
                budget.getExpenses().stream()
                        .map(expenses -> modelMapper.map(expenses, ExpenseDTO.class))
                        .collect(Collectors.toList()));
        return dto;
    }
}
