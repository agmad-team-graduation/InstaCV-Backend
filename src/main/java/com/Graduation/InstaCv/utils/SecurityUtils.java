package com.Graduation.InstaCv.utils;

import com.Graduation.InstaCv.security.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static UserDetailsImpl getCurrentUserDetails() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            return (UserDetailsImpl) principal;
        }
        throw new IllegalStateException("User is not authenticated or not an instance of UserDetailsImpl");
    }
}
