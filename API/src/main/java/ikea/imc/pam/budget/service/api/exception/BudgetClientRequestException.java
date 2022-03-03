package ikea.imc.pam.budget.service.api.exception;

import ikea.imc.pam.budget.service.api.dto.ResponseMessageDTO;
import org.springframework.http.HttpStatus;

public class BudgetClientRequestException extends RuntimeException {

    private final ResponseMessageDTO<?> responseBody;

    public BudgetClientRequestException(ResponseMessageDTO<?> responseBody) {
        super(responseBody.getMessage());
        this.responseBody = responseBody;
    }

    public HttpStatus getStatusCode() {
        return HttpStatus.valueOf(responseBody.getStatusCode());
    }

    public ResponseMessageDTO<?> getBody() {
        return responseBody;
    }
}
