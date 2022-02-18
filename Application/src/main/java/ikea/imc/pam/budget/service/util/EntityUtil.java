package ikea.imc.pam.budget.service.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EntityUtil {

    private EntityUtil() {}

    public static <T> void merge(T currentObject, T updatedObject)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method[] methods = currentObject.getClass().getDeclaredMethods();

        for (Method fromMethod : methods) {

            if (fromMethod.getDeclaringClass().equals(currentObject.getClass())
                    && fromMethod.getName().startsWith("get")) {

                String fromName = fromMethod.getName();
                String toName = fromName.replaceFirst("get", "set");

                Method toMethod = currentObject.getClass().getMethod(toName, fromMethod.getReturnType());
                Object value = fromMethod.invoke(updatedObject, (Object[]) null);
                if (value != null) {
                    toMethod.invoke(currentObject, value);
                }
            }
        }
    }
}
