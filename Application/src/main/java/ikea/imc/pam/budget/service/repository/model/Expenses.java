package ikea.imc.pam.budget.service.repository.model;

import ikea.imc.pam.budget.service.repository.model.utils.InvoicingTypeOption;
import java.math.BigDecimal;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Expenses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long expensesId;

    private long assignmentId;

    private long assetTypeId;

    private String comment;

    private int cost;

    private double costCOMDEV;

    private int costPerUnit;

    private byte percentCOMDEV;

    private short units;

    private byte weeks;

    @Enumerated(EnumType.STRING)
    private InvoicingTypeOption invoicingTypeOption;

    @ManyToOne
    @JoinColumn(name = "budgetId")
    private Budget budget;

    public String getInvoicingTypeName() {
        return invoicingTypeOption != null ? invoicingTypeOption.getDescription() : null;
    }
}
