package ikea.imc.pam.budget.service.api.dto;

import javax.validation.constraints.*;

public class RequestPartialBudgetDTO {

    @Size(min = 3, max = 200)
    private String name;
    private String status;
    private int salesStartAtYear;
    private byte salesStartAtMonth;
    private Long hfbId;
    private String fiscalYear;
    private Integer cost;
    private Long comdevCost;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSalesStartAtYear() {
        return salesStartAtYear;
    }

    public void setSalesStartAtYear(int salesStartAtYear) {
        this.salesStartAtYear = salesStartAtYear;
    }

    public byte getSalesStartAtMonth() {
        return salesStartAtMonth;
    }

    public void setSalesStartAtMonth(byte salesStartAtMonth) {
        this.salesStartAtMonth = salesStartAtMonth;
    }

    public Long getHfbId() {
        return hfbId;
    }

    public void setHfbId(Long hfbId) {
        this.hfbId = hfbId;
    }

    public String getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(String fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public Long getComdevCost() {
        return comdevCost;
    }

    public void setComdevCost(Long comdevCost) {
        this.comdevCost = comdevCost;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }
}
