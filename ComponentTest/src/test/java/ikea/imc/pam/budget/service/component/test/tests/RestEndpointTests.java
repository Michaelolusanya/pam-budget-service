package ikea.imc.pam.budget.service.component.test.tests;

import ikea.imc.pam.budget.service.api.client.BudgetClient;
import ikea.imc.pam.budget.service.component.test.AbstractBaseTest;
import java.util.Collections;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RestEndpointTests extends AbstractBaseTest {
    @Autowired BudgetClient budgetClient;

    @Test
    // TODO Delete and replace with proper tests
    @Disabled
    void testMe() {
        budgetClient.findBudgets(Collections.emptyList(), Collections.emptyList());
        budgetClient.getBudget(0L);
    }
}
