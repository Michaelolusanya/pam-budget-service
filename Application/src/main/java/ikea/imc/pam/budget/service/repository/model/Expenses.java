package ikea.imc.pam.budget.service.repository.model;

import ikea.imc.pam.budget.service.repository.model.utils.InvoicingTypeOption;
import javax.persistence.*;
import lombok.*;

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

    private Long assetTypeId;

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

        setNotNullValue(mergeExpenses::getAssetTypeId, mergedBuilder::assetTypeId);
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
                Getter.of(this::getAssetTypeId, compareTo::getAssetTypeId),
                Getter.of(this::getComment, compareTo::getComment),
                Getter.of(this::getCost, compareTo::getCost),
                Getter.of(this::getCostPerUnit, compareTo::getCostPerUnit),
                Getter.of(this::getPercentCOMDEV, compareTo::getPercentCOMDEV),
                Getter.of(this::getUnits, compareTo::getUnits),
                Getter.of(this::getWeeks, compareTo::getWeeks),
                Getter.of(this::getInvoicingTypeOption, compareTo::getInvoicingTypeOption));
    }
}
