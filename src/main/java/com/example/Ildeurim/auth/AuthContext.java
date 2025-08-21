package com.example.Ildeurim.auth;
import com.example.Ildeurim.auth.CustomPrincipal;
import com.example.Ildeurim.commons.enums.UserType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class AuthContext {
    private AuthContext() {}

    public static Optional<CustomPrincipal> principal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return Optional.empty();
        Object p = auth.getPrincipal();
        return (p instanceof CustomPrincipal cp) ? Optional.of(cp) : Optional.empty();
    }

    public static Optional<Long> userId() {
        return principal().map(CustomPrincipal::userId);
    }

    public static Optional<UserType> userType() {
        return principal().map(CustomPrincipal::userType);
    }

    public static Optional<String> phone() {
        return principal().map(CustomPrincipal::phone);
    }
}
