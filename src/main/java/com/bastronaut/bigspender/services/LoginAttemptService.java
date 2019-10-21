package com.bastronaut.bigspender.services;


import com.bastronaut.bigspender.models.LoginAttempt;
import com.bastronaut.bigspender.repositories.LoginAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service to return login attempts for a user.
 * Possible alternative implementation: don't return int but LoginAttempt object, have consumer decide what to do
 * with the LoginAttempt object
 */
@Service
public class LoginAttemptService {

    final private LoginAttemptRepository loginAttemptRepository;

    public LoginAttemptService(@Autowired LoginAttemptRepository loginAttemptRepository) {
        this.loginAttemptRepository = loginAttemptRepository;
    }

    // TODO prevent deletion if unsuccesfullogin attempt does not exist yet for user
    public void successfulLogin(final String username) {
        loginAttemptRepository.deleteById(username);
    }

    public void unsuccessfulLogin(final String username) {
        final Optional<LoginAttempt> maybeLogin = loginAttemptRepository.findById(username);
        if (maybeLogin.isPresent()) {
            final LoginAttempt attempt = maybeLogin.get();
            attempt.setAttempts(attempt.getAttempts() + 1);
            loginAttemptRepository.save(attempt);
            // TODO add timestamp
        } else {
            final LoginAttempt attempt = new LoginAttempt(username, 1);
            loginAttemptRepository.save(attempt);
        }
    }

    public int getLoginAttempts(final String username) {
        final Optional<LoginAttempt> maybeLogin = loginAttemptRepository.findById(username);
        // TODO add timestamp check here
        return maybeLogin.map(LoginAttempt::getAttempts).orElse(0);
    }
}
