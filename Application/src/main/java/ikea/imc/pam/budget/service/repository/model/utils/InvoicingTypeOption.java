package ikea.imc.pam.budget.service.repository.model.utils;

public enum InvoicingTypeOption {
    FIXED_PRICE("Fixed Price"),
    HOURLY_PRICE("Hourly Price"),
    QUOTATION_BASED("Quotation Based");

    private String description; // TODO could be name instead but that has already a meaning for an enum

    InvoicingTypeOption(String invoicingTypeOption) {
        setDescription(invoicingTypeOption);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
