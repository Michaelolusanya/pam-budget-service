package com.ikea.imc.pam.budget.service.repository.model;

import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity {
    
    @LastModifiedBy
    String lastUpdatedById;
    
    @LastModifiedDate
    Instant lastUpdated;
    
    protected static <T extends AbstractEntity> T mergeLastUpdated(T from, T to) {
        to.lastUpdatedById = from.lastUpdatedById;
        to.lastUpdated = from.lastUpdated;
        return to;
    }
    
    protected static <T> void setNotNullValue(Supplier<T> getterMethod, Consumer<T> setterMethod) {
        if (getterMethod.get() != null) {
            setterMethod.accept(getterMethod.get());
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
        
        private final Supplier<T> left;
        private final Supplier<T> right;
        
        static <T> Getter<T> of(Supplier<T> left, Supplier<T> right) {
            return new Getter<>(left, right);
        }
        
        private Getter(Supplier<T> left, Supplier<T> right) {
            this.left = left;
            this.right = right;
        }
    }
    
    public String getLastUpdatedById() {
        return lastUpdatedById;
    }
    
    public Instant getLastUpdated() {
        return lastUpdated;
    }
}
