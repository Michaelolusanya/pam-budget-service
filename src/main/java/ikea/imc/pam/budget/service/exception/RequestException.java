package ikea.imc.pam.budget.service.exception;

abstract public class RequestException extends RuntimeException {
    RequestException(String message) {
        super(message);
    }
}
