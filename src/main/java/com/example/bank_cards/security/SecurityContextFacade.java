package com.example.bank_cards.security;

import com.example.bank_cards.entity.UserEntity;
import com.example.bank_cards.enums.Roles;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityContextFacade {

    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserEntity)) {
            throw new AccessDeniedException("Пользователь не аутентифицирован");
        }
        return ((UserEntity) authentication.getPrincipal()).getId();
    }

    public Roles getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities().isEmpty()) {
            throw new AccessDeniedException("Пользователь не аутентифицирован");
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(Roles::valueOf)
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("Роль пользователя не найдена"));
    }
}
