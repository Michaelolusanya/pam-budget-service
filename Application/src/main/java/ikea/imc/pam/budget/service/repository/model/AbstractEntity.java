package ikea.imc.pam.budget.service.repository.model;

import java.util.Date;
import java.util.Objects;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
abstract class AbstractEntity {

    @LastModifiedBy private String lastUpdatedById;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

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

    public String getLastUpdatedById() {
        return lastUpdatedById;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    interface GetterMethod<T> {
        T get();
    }

    interface SetterMethod<T> {
        void set(T value);
    }
}
