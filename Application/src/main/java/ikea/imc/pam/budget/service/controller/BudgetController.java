package ikea.imc.pam.budget.service.controller;

import ikea.imc.pam.budget.service.client.Paths;
import ikea.imc.pam.budget.service.client.dto.*;
import ikea.imc.pam.budget.service.client.dto.ResponseMessageDTO;
import ikea.imc.pam.budget.service.configuration.BudgetMapper;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Paths.BUDGET_V1_ENDPOINT)
public class BudgetController {

    private static final Logger log = LogManager.getLogger(BudgetController.class);
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @Operation(summary = "Get budget by id")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO<BudgetDTO>> getBudget(@PathVariable Long id) {

        return budgetService
                .getById(id)
                .map(
                        budget ->
                                ResponseEntityFactory.generateResponse(
                                        HttpStatus.OK, BudgetMapper.buildBudgetDTO(budget)))
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
            @RequestParam(required = false, name = "fiscalYears") List<Integer> fiscalYears) {

        return ResponseEntityFactory.generateResponse(
                HttpStatus.OK,
                budgetService.listBudgets(projectIds, fiscalYears).stream()
                        .map(BudgetMapper::buildBudgetDTO)
                        .collect(Collectors.toList()));
    }

    @Operation(summary = "Delete budget by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO<BudgetDTO>> deleteBudget(@PathVariable Long id) {
        return budgetService
                .deleteById(id)
                .map(
                        budget ->
                                ResponseEntityFactory.generateResponse(
                                        HttpStatus.OK, BudgetMapper.buildBudgetDTO(budget)))
                .orElseGet(() -> ResponseEntityFactory.generateResponse(HttpStatus.NO_CONTENT));
    }

    @Operation(summary = "Create a new budget")
    @PostMapping
    public ResponseEntity<ResponseMessageDTO<BudgetDTO>> createBudget(@Valid @RequestBody BudgetDTO dto) {
        Budget budget = BudgetMapper.buildBudget(dto);
        return ResponseEntityFactory.generateResponse(
                HttpStatus.CREATED,
                BudgetMapper.buildBudgetDTO(budgetService.createBudget(dto.getFiscalYear(), budget)));
    }

    @Operation(summary = "Update budget by Id")
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO<BudgetDTO>> updateBudget(
            @PathVariable Long id, @RequestBody @Valid PatchBudgetDTO dto) {
        Budget budget = BudgetMapper.buildBudget(dto);
        return budgetService
                .patchBudget(id, dto.getFiscalYear(), budget)
                .map(
                        updatedBudget ->
                                ResponseEntityFactory.generateResponse(
                                        HttpStatus.OK, BudgetMapper.buildBudgetDTO(updatedBudget)))
                .orElseGet(
                        () -> {
                            log.warn("Could not update budget with id {}", id);
                            return getBudgetNotFoundResponse(id);
                        });
    }

    @Operation(summary = "Create an expense for budget with id {id}")
    @PostMapping("/{id}/expenses")
    public ResponseEntity<ResponseMessageDTO<ExpenseDTO>> createExpense(
            @PathVariable Long id, @Valid @RequestBody ExpenseDTO dto) {
        Optional<Budget> optionalBudget = budgetService.getById(id);
        if (optionalBudget.isEmpty()) {
            log.warn("Could not create expenses, could not find budget with id {}", id);
            return getBudgetNotFoundResponse(id);
        }
        Budget budget = optionalBudget.get();
        Expenses expenses = BudgetMapper.buildExpense(dto);
        return ResponseEntityFactory.generateResponse(
                HttpStatus.CREATED, BudgetMapper.buildExpenseDTO(budgetService.createExpenses(budget, expenses)));
    }

    @Operation(summary = "Update a set of budget expenses by budgetId")
    @PatchMapping("/{id}/expenses")
    public ResponseEntity<ResponseMessageDTO<List<ExpenseDTO>>> updateExpense(
            @PathVariable Long id, @Valid @RequestBody ExpenseBatchDTO dto) {
        List<Expenses> expenses = dto.getData().stream().map(BudgetMapper::buildExpense).collect(Collectors.toList());
        Optional<Budget> optionalBudget = budgetService.getById(id);
        if (optionalBudget.isEmpty()) {
            log.warn("Could not update expenses, could not find budget with id {}", id);
            return getBudgetNotFoundResponse(id);
        }

        Budget budget = optionalBudget.get();
        return ResponseEntityFactory.generateResponse(
                HttpStatus.OK,
                budgetService.patchExpenses(budget, expenses).stream()
                        .map(BudgetMapper::buildExpenseDTO)
                        .collect(Collectors.toList()));
    }

    private <T> ResponseEntity<ResponseMessageDTO<T>> getBudgetNotFoundResponse(Long id) {
        return ResponseEntityFactory.generateResponseMessage(
                HttpStatus.NOT_FOUND, String.format("Budget %d not found", id));
    }
}
