package ikea.imc.pam.budget.service.repository.model;

import ikea.imc.pam.budget.service.repository.model.utils.InvoicingTypeOption;
import javax.persistence.*;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Expenses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expensesId;

    private Long assignmentId;

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

    public String getInvoicingTypeName() {
        return invoicingTypeOption != null ? invoicingTypeOption.getDescription() : null;
    }
}
