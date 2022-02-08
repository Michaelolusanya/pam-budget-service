package ikea.imc.pam.budget.service.api.dto;

public class ResponseExpenseDTO {

    private Long id;
    private Long budgetId;
    private String name;
    private double comdevFraction;
    private int comdevCost;
    private int unitCost;
    private byte weekCount;
    private String comment;
    private String priceModel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public byte getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(byte weekCount) {
        this.weekCount = weekCount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPriceModel() {
        return priceModel;
    }

    public void setPriceModel(String priceModel) {
        this.priceModel = priceModel;
    }
}
