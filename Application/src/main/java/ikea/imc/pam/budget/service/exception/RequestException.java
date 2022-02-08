package ikea.imc.pam.budget.service.exception;

public abstract class RequestException extends RuntimeException {
    RequestException(String message) {
        super(message);
    }
}
