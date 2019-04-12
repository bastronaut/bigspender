package com.bastronaut.bigspender.services;

import com.bastronaut.bigspender.config.SecurityConfiguration;
import com.bastronaut.bigspender.exceptions.RegistrationException;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private PasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        this.encoder = SecurityConfiguration.getPasswordEncoder();
    }

    @Override
    public User loadUserByUsername(final String username) throws UsernameNotFoundException {

        final Optional<User> user = userRepository.findByEmail(username);
        if (user.isPresent()) {
            return user.get();
        }
        throw new UsernameNotFoundException(String.format("User not found: %s", username));
    }

    public User registerUser(final User user) throws RegistrationException {
        if (isValidRegistration(user)) {
            return userRepository.save(user);
        }
        throw new RegistrationException("User already exists: " + user.getEmail());
    }


    private boolean isValidRegistration(final User user) {
        final Optional<User> maybeUser = userRepository.findByEmail(user.getEmail());
        return !maybeUser.isPresent();
    }

}
