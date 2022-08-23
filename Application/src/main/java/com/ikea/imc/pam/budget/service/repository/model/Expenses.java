package com.ikea.imc.pam.budget.service.repository.model;

import com.ikea.imc.pam.budget.service.repository.model.utils.InvoicingTypeOption;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Expenses extends AbstractEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expensesId;
    
    private Long priceItemId;
    
    private String comment;
    
    private Integer cost;
    
    private Double internalCost;
    
    private Integer costPerUnit;
    
    private Byte internalPercent;
    
    private Double units;
    
    private Byte weeks; //TODO Not in use but retained for future in DB
    
    @Enumerated(EnumType.STRING)
    private InvoicingTypeOption invoicingTypeOption;
    
    @ManyToOne
    @JoinColumn(name = "budgetId")
    private Budget budget;
    
    public String toInvoicingTypeName() {
        return invoicingTypeOption != null ? invoicingTypeOption.getDescription() : null;
    }
    
    public static Expenses merge(Expenses fromExpenses, Expenses toExpenses) {
        
        ExpensesBuilder mergedBuilder = fromExpenses.toBuilder();
        
        setNotNullValue(toExpenses::getPriceItemId, mergedBuilder::priceItemId);
        setNotNullValue(toExpenses::getComment, mergedBuilder::comment);
        setNotNullValue(toExpenses::getCost, mergedBuilder::cost);
        setNotNullValue(toExpenses::getInternalCost, mergedBuilder::internalCost);
        setNotNullValue(toExpenses::getCostPerUnit, mergedBuilder::costPerUnit);
        setNotNullValue(toExpenses::getInternalPercent, mergedBuilder::internalPercent);
        setNotNullValue(toExpenses::getUnits, mergedBuilder::units);
        setNotNullValue(toExpenses::getInvoicingTypeOption, mergedBuilder::invoicingTypeOption);
        
        return mergeLastUpdated(fromExpenses, mergedBuilder.build());
    }
    
    public boolean isEqual(Expenses compareTo) {
    
        if (compareTo == null) {return false;}
        
        return isEqual(
            Getter.of(this::getPriceItemId, compareTo::getPriceItemId),
            Getter.of(this::getComment, compareTo::getComment),
            Getter.of(this::getCost, compareTo::getCost),
            Getter.of(this::getCostPerUnit, compareTo::getCostPerUnit),
            Getter.of(this::getInternalPercent, compareTo::getInternalPercent),
            Getter.of(this::getUnits, compareTo::getUnits),
            Getter.of(this::getInvoicingTypeOption, compareTo::getInvoicingTypeOption)
        );
    }
}
