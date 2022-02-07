package ikea.imc.pam.budget.service.api.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import ikea.imc.pam.budget.service.api.Paths;
import ikea.imc.pam.budget.service.api.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BudgetClientV1 implements BudgetClient {

    private final String budgetServiceEndpoint;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public BudgetClientV1(@Value("${ikea.imc.pam.budget.service.url}") String budgetServiceBaseUrl,
                          ObjectMapper objectMapper) {
        this.budgetServiceEndpoint = budgetServiceBaseUrl + Paths.BUDGET_V1_ENDPOINT;
        this.webClient = WebClient
                .builder()
                .codecs(configure -> configure.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<ResponseBudgetDTO> getBudget(Long id) {
        String url = budgetServiceEndpoint + id;
        ResponseMessageDTO<?> dto = webClient
                .get()
                .uri(url)
                .retrieve()
                .onStatus(status -> status == HttpStatus.NOT_FOUND, error -> Mono.empty())
                .bodyToMono(ResponseMessageDTO.class)
                .block();

        if (dto != null && dto.getSuccess()) {
            return Optional.of(objectMapper.convertValue(dto.getData(), ResponseBudgetDTO.class));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<ResponseBudgetDTO> findBudgets(List<Long> hfbIds, List<String> fiscalYears) {

        String url = Paths.buildUrl(budgetServiceEndpoint,
                Paths.buildRequestParameter("hfbIds", hfbIds),
                Paths.buildRequestParameter("fiscalYears", fiscalYears));

        ResponseMessageDTO<?> dto = webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(ResponseMessageDTO.class).block();


        if (dto != null && dto.getData() instanceof List) {
            List<?> list = (List<?>) dto.getData();
            return list
                    .stream()
                    .map(current -> objectMapper.convertValue(current, ResponseBudgetDTO.class))
                    .collect(Collectors.toList());
        } else {
            return List.of();
        }
    }

    @Override
    public ResponseBudgetDTO deleteBudget(Long id) {
        String url = budgetServiceEndpoint + id;
        ResponseMessageDTO<?> dto = webClient.delete().uri(url).retrieve().bodyToMono(ResponseMessageDTO.class).block();
        return dto != null ? objectMapper.convertValue(dto.getData(), ResponseBudgetDTO.class) : null;
    }

    @Override
    public ResponseBudgetDTO createBudget(RequestBudgetDTO requestBudgetDTO) {
        ResponseMessageDTO<?> dto = webClient
                .post()
                .uri(budgetServiceEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBudgetDTO)
                .retrieve()
                .bodyToMono(ResponseMessageDTO.class)
                .block();
        return dto != null ? objectMapper.convertValue(dto.getData(), ResponseBudgetDTO.class) : null;
    }

    @Override
    public ResponseBudgetDTO updateBudget(Long id, RequestPartialBudgetDTO requestPartialBudgetDTO) {
        String url = budgetServiceEndpoint + id;
        ResponseMessageDTO<?> dto = webClient
                .patch()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestPartialBudgetDTO)
                .retrieve()
                .bodyToMono(ResponseMessageDTO.class)
                .block();
        return dto != null ? objectMapper.convertValue(dto.getData(), ResponseBudgetDTO.class) : null;
    }

    @Override
    public ResponseExpenseDTO updateExpense(Long budgetId, Long expenseId, RequestPartialExpenseDTO requestPartialExpenseDTO) {
        String url = budgetServiceEndpoint + budgetId + "expenses/" + expenseId;
        ResponseMessageDTO<?> dto = webClient
                .patch()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestPartialExpenseDTO)
                .retrieve()
                .bodyToMono(ResponseMessageDTO.class)
                .block();
        return dto != null ? objectMapper.convertValue(dto.getData(), ResponseExpenseDTO.class) : null;
    }
}
