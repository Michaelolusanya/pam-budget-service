package ikea.imc.pam.budget.service.service;

public interface SecurityContextService {

    String getUserId();

    String getUserName();

    String getToken();
}
