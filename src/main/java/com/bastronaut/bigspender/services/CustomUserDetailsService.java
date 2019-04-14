package com.bastronaut.bigspender.services;

import com.bastronaut.bigspender.config.SecurityConfiguration;
import com.bastronaut.bigspender.config.SecurityUtil;
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


    @Autowired
    private UserRepository userRepository;

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
            final String encodedPassword = SecurityUtil.encode(user.getPassword());
            return userRepository.save(new User(user.getEmail(), user.getName(), encodedPassword));
        }
        throw new RegistrationException("User already exists: " + user.getEmail());
    }


    private boolean isValidRegistration(final User user) {
        final Optional<User> maybeUser = userRepository.findByEmail(user.getEmail());
        return !maybeUser.isPresent();
    }

}
