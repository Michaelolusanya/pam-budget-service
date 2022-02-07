package ikea.imc.pam.budget.service.api.dto;

import java.util.List;

public class ResponseBudgetCategoryDTO {

    private String name;
    private List<ResponseBudgetSubCategoryDTO> categories;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ResponseBudgetSubCategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<ResponseBudgetSubCategoryDTO> categories) {
        this.categories = categories;
    }
}
