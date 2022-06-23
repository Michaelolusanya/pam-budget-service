package com.ikea.imc.pam.budget.service.repository;

import com.ikea.imc.pam.common.service.UserService;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    
    private final UserService userService;
    
    public AuditorAwareImpl(UserService userService) {
        this.userService = userService;
    }
    
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(userService.getCurrentUserId());
    }
}
