package ikea.imc.pam.budget.service.repository.model;

/** This is a fallback implementation if authentication is disabled */
public class SimpleUserInformation implements UserInformation {

    public static final String FIRST_NAME = "Anonymous";
    public static final String SURNAME = "User";

    private final String userId;

    public SimpleUserInformation(String userId) {
        this.userId = userId;
    }

    @Override
    public String getId() {
        return userId;
    }

    @Override
    public String getEmail() {
        return "";
    }

    @Override
    public String getAlias() {
        return "";
    }

    @Override
    public String getFullName() {
        return getFirstName() + " " + getSurname();
    }

    @Override
    public String getFirstName() {
        return FIRST_NAME;
    }

    @Override
    public String getSurname() {
        return SURNAME;
    }
}
