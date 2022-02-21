package ikea.imc.pam.budget.service.repository.model;

import java.util.Objects;

abstract class AbstractEntity {

    protected static <T> void setNotNullValue(GetterMethod<T> getterMethod, SetterMethod<T> setterMethod) {
        if (getterMethod.get() != null) {
            setterMethod.set(getterMethod.get());
        }
    }

    protected static boolean isEqual(Getter<?>... compareValues) {
        for (Getter<?> compareValue : compareValues) {
            if (!isValuesEqual(compareValue)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isValuesEqual(Getter<?> compare) {
        return Objects.equals(compare.left.get(), compare.right.get());
    }

    protected static class Getter<T> {

        private final GetterMethod<T> left;
        private final GetterMethod<T> right;

        static <T> Getter<T> of(GetterMethod<T> left, GetterMethod<T> right) {
            return new Getter<>(left, right);
        }

        private Getter(GetterMethod<T> left, GetterMethod<T> right) {
            this.left = left;
            this.right = right;
        }
    }

    interface GetterMethod<T> {
        T get();
    }

    interface SetterMethod<T> {
        void set(T value);
    }
}
