package ikea.imc.pam.budget.service.repository.model;

import ikea.imc.pam.budget.service.repository.model.utils.InvoicingTypeOption;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private Double costCOMDEV;

    private Integer costPerUnit;

    private Byte percentCOMDEV;

    private Short units;

    private Byte weeks;

    @Enumerated(EnumType.STRING)
    private InvoicingTypeOption invoicingTypeOption;

    @ManyToOne
    @JoinColumn(name = "budgetId")
    private Budget budget;

    public String toInvoicingTypeName() {
        return invoicingTypeOption != null ? invoicingTypeOption.getDescription() : null;
    }

    public static Expenses merge(Expenses masterExpenses, Expenses mergeExpenses) {

        ExpensesBuilder mergedBuilder = masterExpenses.toBuilder();

        setNotNullValue(mergeExpenses::getPriceItemId, mergedBuilder::priceItemId);
        setNotNullValue(mergeExpenses::getComment, mergedBuilder::comment);
        setNotNullValue(mergeExpenses::getCost, mergedBuilder::cost);
        setNotNullValue(mergeExpenses::getCostCOMDEV, mergedBuilder::costCOMDEV);
        setNotNullValue(mergeExpenses::getCostPerUnit, mergedBuilder::costPerUnit);
        setNotNullValue(mergeExpenses::getPercentCOMDEV, mergedBuilder::percentCOMDEV);
        setNotNullValue(mergeExpenses::getUnits, mergedBuilder::units);
        setNotNullValue(mergeExpenses::getWeeks, mergedBuilder::weeks);
        setNotNullValue(mergeExpenses::getInvoicingTypeOption, mergedBuilder::invoicingTypeOption);

        return mergedBuilder.build();
    }

    public boolean isEqual(Expenses compareTo) {

        if (compareTo == null) return false;

        return isEqual(
                Getter.of(this::getPriceItemId, compareTo::getPriceItemId),
                Getter.of(this::getComment, compareTo::getComment),
                Getter.of(this::getCost, compareTo::getCost),
                Getter.of(this::getCostPerUnit, compareTo::getCostPerUnit),
                Getter.of(this::getPercentCOMDEV, compareTo::getPercentCOMDEV),
                Getter.of(this::getUnits, compareTo::getUnits),
                Getter.of(this::getWeeks, compareTo::getWeeks),
                Getter.of(this::getInvoicingTypeOption, compareTo::getInvoicingTypeOption));
    }
}
