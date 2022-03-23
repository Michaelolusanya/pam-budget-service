package ikea.imc.pam.budget.service.azure.repository.model;

import com.microsoft.graph.models.User;
import ikea.imc.pam.budget.service.repository.model.UserInformation;

public class AADUserInformation implements UserInformation {

    private final User user;

    public AADUserInformation(User user) {
        this.user = user;
    }

    @Override
    public String getId() {
        return user.id;
    }

    @Override
    public String getEmail() {
        return user.mail;
    }

    @Override
    public String getAlias() {
        return user.displayName;
    }

    @Override
    public String getFullName() {
        return getFirstName() + " " + getSurname();
    }

    @Override
    public String getFirstName() {
        return user.givenName;
    }

    @Override
    public String getSurname() {
        return user.surname;
    }
}
