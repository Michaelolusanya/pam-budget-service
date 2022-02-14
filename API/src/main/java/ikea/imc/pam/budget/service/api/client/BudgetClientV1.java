package ikea.imc.pam.budget.service.api.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import ikea.imc.pam.budget.service.api.Paths;
import ikea.imc.pam.budget.service.api.dto.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class BudgetClientV1 implements BudgetClient {

    private final String budgetServiceEndpoint;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public BudgetClientV1(
            @Value("${ikea.imc.pam.budget.service.url}") String budgetServiceBaseUrl,
            ObjectMapper objectMapper,
            WebClient webClient) {
        this.budgetServiceEndpoint = budgetServiceBaseUrl + Paths.BUDGET_V1_ENDPOINT;
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<BudgetDTO> getBudget(Long id) {
        String url = budgetServiceEndpoint + id;
        ResponseMessageDTO<?> dto =
                webClient
                        .get()
                        .uri(url)
                        .retrieve()
                        .onStatus(status -> status == HttpStatus.NOT_FOUND, error -> Mono.empty())
                        .bodyToMono(ResponseMessageDTO.class)
                        .block();

        if (dto != null && dto.getSuccess()) {
            return Optional.of(objectMapper.convertValue(dto.getData(), BudgetDTO.class));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<BudgetDTO> findBudgets(List<Long> hfbIds, List<String> fiscalYears) {

        String url =
                Paths.buildUrl(
                        budgetServiceEndpoint,
                        Paths.buildRequestParameter("hfbIds", hfbIds),
                        Paths.buildRequestParameter("fiscalYears", fiscalYears));

        ResponseMessageDTO<?> dto = webClient.get().uri(url).retrieve().bodyToMono(ResponseMessageDTO.class).block();

        if (dto != null && dto.getData() instanceof List) {
            List<?> list = (List<?>) dto.getData();
            return list.stream()
                    .map(current -> objectMapper.convertValue(current, BudgetDTO.class))
                    .collect(Collectors.toList());
        } else {
            return List.of();
        }
    }

    @Override
    public BudgetDTO deleteBudget(Long id) {
        String url = budgetServiceEndpoint + id;
        ResponseMessageDTO<?> dto = webClient.delete().uri(url).retrieve().bodyToMono(ResponseMessageDTO.class).block();
        return dto != null ? objectMapper.convertValue(dto.getData(), BudgetDTO.class) : null;
    }

    @Override
    public BudgetDTO createBudget(BudgetDTO requestBudgetDTO) {
        ResponseMessageDTO<?> dto =
                webClient
                        .post()
                        .uri(budgetServiceEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(requestBudgetDTO)
                        .retrieve()
                        .bodyToMono(ResponseMessageDTO.class)
                        .block();
        return dto != null ? objectMapper.convertValue(dto.getData(), BudgetDTO.class) : null;
    }

    @Override
    public BudgetDTO updateBudget(Long id, BudgetDTO requestBudgetDTO) {
        String url = budgetServiceEndpoint + id;
        ResponseMessageDTO<?> dto =
                webClient
                        .patch()
                        .uri(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(requestBudgetDTO)
                        .retrieve()
                        .bodyToMono(ResponseMessageDTO.class)
                        .block();
        return dto != null ? objectMapper.convertValue(dto.getData(), BudgetDTO.class) : null;
    }

    @Override
    public ExpenseDTO updateExpense(Long budgetId, Long expenseId, ExpenseDTO requestPartialExpenseDTO) {
        String url = budgetServiceEndpoint + budgetId + "expenses/" + expenseId;
        ResponseMessageDTO<?> dto =
                webClient
                        .patch()
                        .uri(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(requestPartialExpenseDTO)
                        .retrieve()
                        .bodyToMono(ResponseMessageDTO.class)
                        .block();
        return dto != null ? objectMapper.convertValue(dto.getData(), ExpenseDTO.class) : null;
    }
}
