package com.ikea.imc.pam.budget.service.controller;

import com.ikea.imc.pam.budget.service.client.Paths;
import com.ikea.imc.pam.budget.service.client.dto.*;
import com.ikea.imc.pam.budget.service.configuration.BudgetMapper;
import com.ikea.imc.pam.budget.service.controller.dto.ResponseEntityFactory;
import com.ikea.imc.pam.budget.service.repository.model.Budget;
import com.ikea.imc.pam.budget.service.repository.model.Expenses;
import com.ikea.imc.pam.budget.service.service.BudgetService;
import com.ikea.imc.pam.budget.service.service.entity.BudgetContent;
import com.ikea.imc.pam.common.dto.ResponseMessageDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

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
        log.debug("Get budget with id {}", id);
        
        return budgetService.getById(id)
            .map(budget -> ResponseEntityFactory.generateResponse(HttpStatus.OK, budgetMapper.buildBudgetDTO(budget)))
            .orElseGet(() -> {
                log.warn("Could not find budget with id {}", id);
                return getBudgetNotFoundResponse(id);
            });
    }
    
    @Operation(summary = "List budgets by filtering project id and fiscal year")
    @GetMapping("/")
    public ResponseEntity<ResponseMessageDTO<List<BudgetDTO>>> findBudgets(
        @RequestParam(required = false, name = "projectIds") List<Long> projectIds,
        @RequestParam(required = false, name = "fiscalYears") List<Integer> fiscalYears) {
        log.debug("Find budgets for projectIds {} and fiscalYears {}", projectIds, fiscalYears);
        
        return ResponseEntityFactory.generateResponse(HttpStatus.OK,
            budgetService.listBudgets(projectIds, fiscalYears).stream().map(budgetMapper::buildBudgetDTO).toList()
        );
    }
    
    @Operation(summary = "Delete budget by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO<BudgetDTO>> deleteBudget(@PathVariable Long id) {
        log.debug("Delete budget with id {}", id);
        
        return budgetService.deleteById(id)
            .map(budget -> ResponseEntityFactory.generateResponse(HttpStatus.OK, budgetMapper.buildBudgetDTO(budget)))
            .orElseGet(() -> ResponseEntityFactory.generateResponse(HttpStatus.NO_CONTENT));
    }
    
    @Operation(summary = "Create a new budget")
    @PostMapping
    public ResponseEntity<ResponseMessageDTO<BudgetDTO>> createBudget(@Valid @RequestBody BudgetDTO dto) {
        log.debug("Creating budget {}", dto);
        
        BudgetContent budget =
            budgetService.createBudget(budgetMapper.buildBudgetAreaParameters(dto), budgetMapper.buildBudget(dto));
        
        return ResponseEntityFactory.generateResponse(HttpStatus.CREATED, budgetMapper.buildBudgetDTO(budget));
    }
    
    @Operation(summary = "Update budget by Id")
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO<BudgetDTO>> updateBudget(
        @PathVariable Long id, @RequestBody @Valid PatchBudgetDTO dto) {
        log.debug("Patch budget with patch {}", dto);
        
        Budget budget = budgetMapper.buildBudget(dto);
        return budgetService.patchBudget(id, dto.getFiscalYear(), budget)
            .map(updatedBudget -> ResponseEntityFactory.generateResponse(HttpStatus.OK,
                budgetMapper.buildBudgetDTO(updatedBudget)
            ))
            .orElseGet(() -> {
                log.warn("Could not update budget with id {}", id);
                return getBudgetNotFoundResponse(id);
            });
    }
    
    @Operation(summary = "Create an expense for budget with id {id}")
    @PostMapping("/{id}/expenses")
    public ResponseEntity<ResponseMessageDTO<ExpenseDTO>> createExpense(
        @PathVariable Long id, @Valid @RequestBody ExpenseDTO dto) {
        log.debug("Creating expense for budget {} with data {}", id, dto);
        
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
        log.debug("Updating expense for budget {} with data {}", id, dto);
        
        Optional<BudgetContent> optionalBudget = budgetService.getById(id);
        if (optionalBudget.isEmpty()) {
            log.warn("Could not update expenses, could not find budget with id {}", id);
            return getBudgetNotFoundResponse(id);
        }
        
        List<Expenses> expenses = dto.getData().stream().map(budgetMapper::buildExpense).toList();
        
        BudgetContent budget = optionalBudget.get();
        return ResponseEntityFactory.generateResponse(HttpStatus.OK,
            budgetService.patchExpenses(budget.budget(), expenses).stream().map(budgetMapper::buildExpenseDTO).toList()
        );
    }
    
    @Operation(summary = "Delete a set of budget expenses by expenseId")
    @DeleteMapping("/{id}/expenses")
    public ResponseEntity<ResponseMessageDTO<List<ExpenseDTO>>> deleteExpenses(
        @PathVariable Long id, @Valid @RequestBody DeleteExpensesDTO dto) {
        log.debug("Deleting expenses for budget {} with ids {}", id, dto.ids());
        
        Optional<BudgetContent> optionalBudget = budgetService.getById(id);
        if (optionalBudget.isEmpty()) {
            log.warn("Could not delete expenses, could not find budget with id {}", id);
            return getBudgetNotFoundResponse(id);
        }
        
        BudgetContent budget = optionalBudget.get();
        return ResponseEntityFactory.generateResponse(HttpStatus.OK,
            budgetService.deleteExpenses(budget.budget(), dto).stream().map(budgetMapper::buildExpenseDTO).toList()
        );
    }
    
    private <T> ResponseEntity<ResponseMessageDTO<T>> getBudgetNotFoundResponse(Long id) {
        return ResponseEntityFactory.generateResponseMessage(HttpStatus.NOT_FOUND,
            String.format("Budget %d not found", id)
        );
    }
}
