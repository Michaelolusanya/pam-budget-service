package ikea.imc.pam.budget.service.api;

import java.util.List;
import java.util.stream.Collectors;

public class Paths {

    public static final String BUDGET_V1_ENDPOINT = "v1/budgets/";

    private Paths() {}

    public static String buildUrl(String url, String... parameters) {
        if (parameters == null || parameters.length == 0) {
            return url;
        }

        StringBuilder builder = new StringBuilder();
        for (String parameter : parameters) {
            if (!parameter.isEmpty()) {
                if (builder.length() > 0) {
                    builder.append("&");
                }
                builder.append(parameter);
            }
        }

        return url + (builder.length() > 0 ? "?" + builder : "");
    }

    public static String buildRequestParameter(String parameterName, List<?> parameterValues) {
        if (parameterValues == null || parameterValues.isEmpty()) {
            return "";
        }

        return parameterValues.stream().map(Object::toString).collect(Collectors.joining(",", parameterName + "=", ""));
    }
}
