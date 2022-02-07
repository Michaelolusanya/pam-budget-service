package ikea.imc.pam.budget.service.api.dto;

import javax.validation.constraints.*;

public class RequestBudgetDTO {

    @NotBlank
    @Size(min = 3, max = 200)
    private String name;

    private String status;
    @NotNull
    private boolean runningRange;

    private int salesStartAtYear;
    private byte salesStartAtMonth;
    @NotNull
    private Long hfbId;
    @NotBlank
    private String fiscalYear;
    @Min(0)
    private Integer estimatedCost;

    @AssertTrue(message = "salesStartAtYear must be between 1970 and 3000 if runningRange is false")
    private boolean isSalesStartAtYear() {
        return (runningRange && salesStartAtYear == 0) || (!runningRange && salesStartAtYear > 1970 && salesStartAtYear < 3000);
    }

    @AssertTrue(message = "salesStartAtMonth must be one of 1, 3, 6, or 9 if runningRange is false")
    private boolean isSalesStartAtMonth() {
        return (runningRange && salesStartAtMonth == 0)
                || (!runningRange
                && (salesStartAtMonth == 1 || salesStartAtMonth == 3 || salesStartAtMonth == 6 || salesStartAtMonth == 9));
    }

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

    public boolean isRunningRange() {
        return runningRange;
    }

    public void setRunningRange(boolean runningRange) {
        this.runningRange = runningRange;
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

    public Integer getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Integer estimatedCost) {
        this.estimatedCost = estimatedCost;
    }
}
