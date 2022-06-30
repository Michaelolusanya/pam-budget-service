package com.ikea.imc.pam.budget.service.controller;

import com.ikea.imc.pam.budget.service.client.Paths;
import com.ikea.imc.pam.budget.service.client.dto.BudgetAreaDTO;
import com.ikea.imc.pam.budget.service.client.dto.BudgetParentType;
import com.ikea.imc.pam.budget.service.client.dto.Constants;
import com.ikea.imc.pam.budget.service.configuration.BudgetAreaMapper;
import com.ikea.imc.pam.budget.service.controller.dto.ResponseEntityFactory;
import com.ikea.imc.pam.budget.service.repository.model.BudgetArea;
import com.ikea.imc.pam.budget.service.service.BudgetAreaService;
import com.ikea.imc.pam.budget.service.service.entity.BudgetAreaParameters;
import com.ikea.imc.pam.common.dto.ResponseMessageDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Slf4j
@RestController
@RequestMapping(Paths.BUDGET_AREA_V1_ENDPOINT)
@Validated
public class BudgetAreaController {
    
    private final BudgetAreaService budgetAreaService;
    private final BudgetAreaMapper budgetAreaMapper;
    
    public BudgetAreaController(BudgetAreaService budgetAreaService, BudgetAreaMapper budgetAreaMapper) {
        this.budgetAreaService = budgetAreaService;
        this.budgetAreaMapper = budgetAreaMapper;
    }
    
    @Operation(summary = "Get budget area by id")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO<BudgetAreaDTO>> getBudgetArea(@PathVariable Long id) {
        log.debug("Get BudgetArea with id {}", id);
        
        return budgetAreaService.getBudgetArea(id)
            .map(budgetArea -> ResponseEntityFactory.generateResponse(
                HttpStatus.OK,
                budgetAreaMapper.buildBudgetAreaDTO(budgetArea)
            ))
            .orElseGet(() -> {
                log.debug("Could not find budget area with id {}", id);
                return ResponseEntityFactory.generateResponseMessage(
                    HttpStatus.NOT_FOUND,
                    String.format("BudgetArea %d not found", id)
                );
            });
    }
    
    @Operation(summary = "Find budget area by parent type, parent id, and fiscal year")
    @GetMapping
    public ResponseEntity<ResponseMessageDTO<BudgetAreaDTO>> findBudgetArea(
        @RequestParam(Paths.PARAMETER_PARENT_TYPE) BudgetParentType parentType,
        @RequestParam(Paths.PARAMETER_PARENT_ID) @Min(Constants.MINIMUM_ID) Long parentId,
        @RequestParam(Paths.PARAMETER_FISCAL_YEAR) @Min(Constants.MINIMUM_YEAR) @Max(
            Constants.MAXIMUM_YEAR) Integer fiscalYear) {
        BudgetAreaParameters budgetAreaParameters = new BudgetAreaParameters(parentType, parentId, fiscalYear);
        log.debug("Find BudgetArea for {}", budgetAreaParameters);
        
        return budgetAreaService.findBudgetArea(budgetAreaParameters)
            .map(budgetArea -> ResponseEntityFactory.generateResponse(
                HttpStatus.OK,
                budgetAreaMapper.buildBudgetAreaDTO(budgetArea)
            ))
            .orElseGet(() -> {
                log.debug("Could not find budget area for parent {}-{} and fiscal year {}",
                    parentType,
                    parentId,
                    fiscalYear
                );
                
                return ResponseEntityFactory.generateResponseMessage(HttpStatus.NOT_FOUND, String.format(
                    "BudgetArea for parentType %s and parentId %d for fiscalYear %d not found",
                    parentType,
                    parentId,
                    fiscalYear
                ));
            });
    }
    
    @Operation(summary = "Create or update a BudgetArea")
    @PutMapping
    public ResponseEntity<ResponseMessageDTO<BudgetAreaDTO>> putBudgetArea(
        @RequestBody @Valid BudgetAreaDTO budgetAreaDTO) {
        log.debug("Put BudgetArea {}", budgetAreaDTO);
        
        BudgetArea budgetArea = budgetAreaService.putBudgetArea(budgetAreaMapper.buildBudgetArea(budgetAreaDTO));
        return ResponseEntityFactory.generateResponse(HttpStatus.OK, budgetAreaMapper.buildBudgetAreaDTO(budgetArea));
    }
}
