package ikea.imc.pam.budget.service.api.client;

import ikea.imc.pam.budget.service.api.Paths;
import ikea.imc.pam.budget.service.api.dto.*;
import ikea.imc.pam.budget.service.api.exception.BudgetClientRequestException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class BudgetClientV1 implements BudgetClient {
    private static final Logger log = LoggerFactory.getLogger(BudgetClientV1.class);
    private final WebClient webClient;
    private final String budgetServiceEndpoint;
    private final String budgetServiceRegistrationId;

    public BudgetClientV1(
            @Value("${ikea.imc.pam.budget.service.url:}") String budgetServiceBaseUrl,
            @Value("${ikea.imc.pam.budget.service.registration.id:pam-budget-service}")
                    String budgetServiceRegistrationId,
            WebClient webClient) {
        this.budgetServiceEndpoint = budgetServiceBaseUrl + Paths.BUDGET_V1_ENDPOINT;
        this.budgetServiceRegistrationId = budgetServiceRegistrationId;
        this.webClient = webClient;
    }

    @Override
    public Optional<BudgetDTO> getBudget(Long id) {
        ParameterizedTypeReference<ResponseMessageDTO<BudgetDTO>> typeReference = new ParameterizedTypeReference<>() {};

        return Optional.of(execute(HttpMethod.GET, "" + id, typeReference));
    }

    @Override
    public List<BudgetDTO> findBudgets(List<Long> projectIds, List<Integer> fiscalYears) {
        ParameterizedTypeReference<ResponseMessageDTO<List<BudgetDTO>>> typeReference =
                new ParameterizedTypeReference<>() {};

        String contextUrl =
                Paths.buildContextUrl(
                        Paths.buildRequestParameter("projectIds", projectIds),
                        Paths.buildRequestParameter("fiscalYears", fiscalYears));

        return execute(HttpMethod.GET, contextUrl, typeReference);
    }

    @Override
    public BudgetDTO deleteBudget(Long id) {
        ParameterizedTypeReference<ResponseMessageDTO<BudgetDTO>> typeReference = new ParameterizedTypeReference<>() {};

        return execute(HttpMethod.DELETE, "" + id, typeReference);
    }

    @Override
    public BudgetDTO createBudget(@Valid BudgetDTO requestBudgetDTO) {
        ParameterizedTypeReference<ResponseMessageDTO<BudgetDTO>> typeReference = new ParameterizedTypeReference<>() {};

        return execute(HttpMethod.POST, "", requestBudgetDTO, typeReference);
    }

    @Override
    public BudgetDTO updateBudget(Long id, @Valid PatchBudgetDTO requestBudgetDTO) {
        ParameterizedTypeReference<ResponseMessageDTO<BudgetDTO>> typeReference = new ParameterizedTypeReference<>() {};

        return execute(HttpMethod.PATCH, "" + id, requestBudgetDTO, typeReference);
    }

    @Override
    public List<ExpenseDTO> updateExpense(Long budgetId, @Valid List<PatchExpenseDTO> requestPartialExpenseDTO) {
        ParameterizedTypeReference<ResponseMessageDTO<List<ExpenseDTO>>> typeReference =
                new ParameterizedTypeReference<>() {};

        return execute(
                HttpMethod.PATCH,
                budgetId + "/expenses",
                ExpenseBatchDTO.builder().data(requestPartialExpenseDTO).build(),
                typeReference);
    }

    private <T> T execute(
            HttpMethod operation, String contextUrl, ParameterizedTypeReference<ResponseMessageDTO<T>> returnType) {
        return execute(operation, contextUrl, "", returnType);
    }

    private <T> T execute(
            HttpMethod operation,
            String contextUrl,
            Object body,
            ParameterizedTypeReference<ResponseMessageDTO<T>> returnType) {
        String url = budgetServiceEndpoint + contextUrl;

        Consumer<Map<String, Object>> clientRegistrationValues =
                ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId(budgetServiceRegistrationId);

        log.debug("Calling endpoint {}", url);
        ResponseMessageDTO<T> wrapper =
                webClient
                        .method(operation)
                        .uri(url)
                        .attributes(clientRegistrationValues)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(body)
                        .retrieve()
                        .onStatus(
                                HttpStatus::is4xxClientError,
                                response -> response.bodyToMono(returnType).map(BudgetClientRequestException::new))
                        .onStatus(
                                HttpStatus::isError,
                                response -> {
                                    throw new RuntimeException("Illegal status code " + response.statusCode());
                                })
                        .bodyToMono(returnType)
                        .block();

        if (wrapper == null) {
            log.debug("No Content(204) response");
            return null;
        }
        return wrapper.getData();
    }
}
