package ikea.imc.pam.budget.service.repository.model.utils;

public enum InvoicingTypeOption {
    FIXED_PRICE("Fixed Price"),
    HOURLY_PRICE("Hourly Price"),
    QUOTATION_BASED("Quotation Based");

    private String description; // TODO could be name instead but that has already a meaning for an enum

    InvoicingTypeOption(String invoicingTypeOption) {
        setDescription(invoicingTypeOption);
    }

    public static InvoicingTypeOption get(String description) {
        if (FIXED_PRICE.getDescription().equals(description)) {
            return FIXED_PRICE;
        }

        if (HOURLY_PRICE.getDescription().equals(description)) {
            return HOURLY_PRICE;
        }

        if (QUOTATION_BASED.getDescription().equals(description)) {
            return QUOTATION_BASED;
        }
        return null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
