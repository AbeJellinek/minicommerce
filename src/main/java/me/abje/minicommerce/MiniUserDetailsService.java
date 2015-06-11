package me.abje.minicommerce;

import me.abje.minicommerce.db.User;
import me.abje.minicommerce.db.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MiniUserDetailsService implements UserDetailsService {
    private final UserRepository userService;

    @Autowired
    public MiniUserDetailsService(UserRepository userService) {
        this.userService = userService;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with username %s was not found", username)));
    }
}
