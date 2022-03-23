package ikea.imc.pam.budget.service.service;

import ikea.imc.pam.budget.service.repository.model.UserInformation;

public interface UserService {

    UserInformation getUserInformation(String userId);
}
