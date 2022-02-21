package ikea.imc.pam.budget.service.api.client;

import ikea.imc.pam.budget.service.api.Paths;
import ikea.imc.pam.budget.service.api.dto.*;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class BudgetClientV1 implements BudgetClient {
    private static final Logger log = LoggerFactory.getLogger(BudgetClientV1.class);
    private final WebClient webClient;
    private final String budgetServiceEndpoint;

    public BudgetClientV1(
            @Value("${ikea.imc.pam.budget.service.url:}") String budgetServiceBaseUrl, WebClient webClient) {
        this.budgetServiceEndpoint = budgetServiceBaseUrl + Paths.BUDGET_V1_ENDPOINT;
        this.webClient = webClient;
    }

    @Override
    public Optional<BudgetDTO> getBudget(Long id) {
        return Optional.of(execute(HttpMethod.GET, "" + id));
    }

    @Override
    public List<BudgetDTO> findBudgets(List<Long> hfbIds, List<Integer> fiscalYears) {
        String contextUrl =
                Paths.buildContextUrl(
                        Paths.buildRequestParameter("hfbIds", hfbIds),
                        Paths.buildRequestParameter("fiscalYears", fiscalYears));

        return execute(HttpMethod.GET, contextUrl);
    }

    @Override
    public BudgetDTO deleteBudget(Long id) {
        return execute(HttpMethod.DELETE, "" + id);
    }

    @Override
    public BudgetDTO createBudget(@Valid BudgetDTO requestBudgetDTO) {
        return execute(HttpMethod.POST, "", requestBudgetDTO);
    }

    @Override
    public BudgetDTO updateBudget(Long id, @Valid PatchBudgetDTO requestBudgetDTO) {
        return execute(HttpMethod.PATCH, "" + id, requestBudgetDTO);
    }

    @Override
    public List<ExpenseDTO> updateExpense(Long budgetId, @Valid List<PatchExpenseDTO> requestPartialExpenseDTO) {
        String url = budgetId + "expenses";
        return execute(HttpMethod.PATCH, url, ExpenseBatchDTO.builder().data(requestPartialExpenseDTO).build());
    }

    private <T> T execute(HttpMethod operation, String contextUrl) {
        return execute(operation, contextUrl, "");
    }

    private <T> T execute(HttpMethod operation, String contextUrl, Object body) {
        String url = budgetServiceEndpoint + contextUrl;

        log.debug("Calling endpoint {}", url);
        ResponseMessageDTO<T> wrapper =
                webClient
                        .method(operation)
                        .uri(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(body)
                        .retrieve()
                        .bodyToMono(ResponseMessageDTO.class)
                        .block();

        assert wrapper != null;
        T result = wrapper.getData();
        log.debug("Result from call {}", result);

        return result;
    }
}
