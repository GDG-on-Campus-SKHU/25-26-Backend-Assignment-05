package org.example.oauth.util;

import org.example.oauth.exception.BadRequestException;
import org.example.oauth.exception.ErrorMessage;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserValidator {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    public static Long requireLogin() {
        Long id = currentUserIDorNull();

        if (id == null) {
            throw new BadRequestException(ErrorMessage.NEED_TO_LOGIN);
        }

        return id;
    }

    public static Long currentUserIDorNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!isEffective(authentication)) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal == null) {
            return null;
        }

        try {
            return Long.parseLong(principal.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean isAdmin() {
        return hasRole(ROLE_ADMIN);
    }

    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return isEffective(authentication) && hasAuthority(authentication, role);
    }

    private static boolean isEffective(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    private static boolean hasAuthority(Authentication authentication, String target) {
        for (GrantedAuthority a : authentication.getAuthorities()) {
            if (target.equals(a.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
