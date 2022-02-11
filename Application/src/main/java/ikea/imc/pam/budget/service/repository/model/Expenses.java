package ikea.imc.pam.budget.service.repository.model;


import ikea.imc.pam.budget.service.repository.model.utils.InvoicingTypeOption;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Expenses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long expensesId;

    private long assignmentId;

    private String comment;

    private int cost;

    private BigDecimal costCOMDEV;

    private int costPerUnit;

    private byte percentCOMDEV;

    private short units;

    private byte weeks;

    @Enumerated(EnumType.STRING)
    private InvoicingTypeOption invoicingTypeOption;
}
