package ikea.imc.pam.budget.service.api.dto;

import java.util.List;

public class ResponseBudgetSubCategoryDTO {

    private String name;
    private List<ResponseExpenseDTO> expenses;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ResponseExpenseDTO> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<ResponseExpenseDTO> expenses) {
        this.expenses = expenses;
    }
}
