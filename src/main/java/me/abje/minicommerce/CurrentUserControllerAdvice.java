package me.abje.minicommerce;

import me.abje.minicommerce.db.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CurrentUserControllerAdvice {
    @ModelAttribute("user")
    public User getCurrentUser(Authentication authentication) {
        return authentication == null ? null : (User) authentication.getPrincipal();
    }
}
