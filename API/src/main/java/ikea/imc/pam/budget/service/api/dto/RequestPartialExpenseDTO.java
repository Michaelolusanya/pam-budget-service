package ikea.imc.pam.budget.service.api.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

public class RequestPartialExpenseDTO {

    @Min(0)
    @Max(1)
    private double comdevFraction;
    @Min(0)
    private int comdevCost;
    private int unitCost;
    @Size(min = 0)
    private String comment;

    public double getComdevFraction() {
        return comdevFraction;
    }

    public void setComdevFraction(double comdevFraction) {
        this.comdevFraction = comdevFraction;
    }

    public int getComdevCost() {
        return comdevCost;
    }

    public void setComdevCost(int comdevCost) {
        this.comdevCost = comdevCost;
    }

    public int getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(int unitCost) {
        this.unitCost = unitCost;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}