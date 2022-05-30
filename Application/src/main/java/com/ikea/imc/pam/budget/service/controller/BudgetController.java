package com.ikea.imc.pam.budget.service.controller;

import com.ikea.imc.pam.budget.service.client.Paths;
import com.ikea.imc.pam.budget.service.client.dto.BudgetDTO;
import com.ikea.imc.pam.budget.service.client.dto.ExpenseBatchDTO;
import com.ikea.imc.pam.budget.service.client.dto.ExpenseDTO;
import com.ikea.imc.pam.budget.service.client.dto.PatchBudgetDTO;
import com.ikea.imc.pam.budget.service.service.entity.BudgetContent;
import com.ikea.imc.pam.common.dto.ResponseMessageDTO;


import com.ikea.imc.pam.budget.service.configuration.BudgetMapper;
import com.ikea.imc.pam.budget.service.controller.dto.ResponseEntityFactory;
import com.ikea.imc.pam.budget.service.repository.model.Budget;
import com.ikea.imc.pam.budget.service.repository.model.Expenses;
import com.ikea.imc.pam.budget.service.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(Paths.BUDGET_V1_ENDPOINT)
public class BudgetController {

    private final BudgetService budgetService;
    private final BudgetMapper budgetMapper;

    public BudgetController(BudgetService budgetService, BudgetMapper budgetMapper) {
        this.budgetService = budgetService;
        this.budgetMapper = budgetMapper;
    }

    @Operation(summary = "Get budget by id")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO<BudgetDTO>> getBudget(@PathVariable Long id) {

        return budgetService
                .getById(id)
                .map(
                        budget ->
                                ResponseEntityFactory.generateResponse(
                                        HttpStatus.OK, budgetMapper.buildBudgetDTO(budget)))
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
                        .map(budgetMapper::buildBudgetDTO)
                        .toList());
    }

    @Operation(summary = "Delete budget by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO<BudgetDTO>> deleteBudget(@PathVariable Long id) {
        return budgetService
                .deleteById(id)
                .map(
                        budget ->
                                ResponseEntityFactory.generateResponse(
                                        HttpStatus.OK, budgetMapper.buildBudgetDTO(budget)))
                .orElseGet(() -> ResponseEntityFactory.generateResponse(HttpStatus.NO_CONTENT));
    }

    @Operation(summary = "Create a new budget")
    @PostMapping
    public ResponseEntity<ResponseMessageDTO<BudgetDTO>> createBudget(@Valid @RequestBody BudgetDTO dto) {

        BudgetContent budget = budgetService.createBudget(budgetMapper.buildBudgetAreaParameters(dto), budgetMapper.buildBudget(dto));

        return ResponseEntityFactory.generateResponse(HttpStatus.CREATED, budgetMapper.buildBudgetDTO(budget));
    }

    @Operation(summary = "Update budget by Id")
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO<BudgetDTO>> updateBudget(
            @PathVariable Long id, @RequestBody @Valid PatchBudgetDTO dto) {
        Budget budget = budgetMapper.buildBudget(dto);
        return budgetService
                .patchBudget(id, dto.getFiscalYear(), budget)
                .map(
                        updatedBudget ->
                                ResponseEntityFactory.generateResponse(
                                        HttpStatus.OK, budgetMapper.buildBudgetDTO(updatedBudget)))
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
        Optional<BudgetContent> optionalBudget = budgetService.getById(id);
        if (optionalBudget.isEmpty()) {
            log.warn("Could not create expenses, could not find budget with id {}", id);
            return getBudgetNotFoundResponse(id);
        }

        Expenses expenses = budgetService.createExpenses(optionalBudget.get().budget(), budgetMapper.buildExpense(dto));
        ExpenseDTO expenseDTO = budgetMapper.buildExpenseDTO(expenses);

        return ResponseEntityFactory.generateResponse(HttpStatus.CREATED, expenseDTO);
    }

    @Operation(summary = "Update a set of budget expenses by budgetId")
    @PatchMapping("/{id}/expenses")
    public ResponseEntity<ResponseMessageDTO<List<ExpenseDTO>>> updateExpense(
            @PathVariable Long id, @Valid @RequestBody ExpenseBatchDTO dto) {

        Optional<BudgetContent> optionalBudget = budgetService.getById(id);
        if (optionalBudget.isEmpty()) {
            log.warn("Could not update expenses, could not find budget with id {}", id);
            return getBudgetNotFoundResponse(id);
        }

        List<Expenses> expenses = dto.getData().stream().map(budgetMapper::buildExpense).toList();

        BudgetContent budget = optionalBudget.get();
        return ResponseEntityFactory.generateResponse(
                HttpStatus.OK,
                budgetService.patchExpenses(budget.budget(), expenses).stream()
                        .map(budgetMapper::buildExpenseDTO)
                        .toList());
    }

    private <T> ResponseEntity<ResponseMessageDTO<T>> getBudgetNotFoundResponse(Long id) {
        return ResponseEntityFactory.generateResponseMessage(
                HttpStatus.NOT_FOUND, String.format("Budget %d not found", id));
    }
}
