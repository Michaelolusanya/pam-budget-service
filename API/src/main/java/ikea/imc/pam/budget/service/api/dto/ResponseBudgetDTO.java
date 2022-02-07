package ikea.imc.pam.budget.service.api.dto;

import java.util.List;

public class ResponseBudgetDTO extends RequestBudgetDTO {

    private Long id;
    private Long comdevCost;
    private List<ResponseBudgetCategoryDTO> categories;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getComdevCost() {
        return comdevCost;
    }

    public void setComdevCost(Long comdevCost) {
        this.comdevCost = comdevCost;
    }

    public List<ResponseBudgetCategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<ResponseBudgetCategoryDTO> categories) {
        this.categories = categories;
    }
}
