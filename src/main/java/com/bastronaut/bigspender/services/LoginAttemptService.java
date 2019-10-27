package com.bastronaut.bigspender.services;


import com.bastronaut.bigspender.models.LoginAttempt;
import com.bastronaut.bigspender.repositories.LoginAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.bastronaut.bigspender.utils.ApplicationConstants.MINUTES_ACCOUNT_LOCKED;

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

    @Transactional
    public void successfulLogin(final String username) {
        loginAttemptRepository.deleteByUsername(username);
    }

    public void unsuccessfulLogin(final String username) {
        final Optional<LoginAttempt> maybeLogin = loginAttemptRepository.findById(username);
        if (maybeLogin.isPresent()) {
            final LoginAttempt attempt = maybeLogin.get();
            attempt.setAttempts(attempt.getAttempts() + 1);
            attempt.setMostRecentAttempt(LocalDateTime.now());
            loginAttemptRepository.save(attempt);
        } else {
            final LoginAttempt attempt = new LoginAttempt(username, 1, LocalDateTime.now());
            loginAttemptRepository.save(attempt);
        }
    }

    /**
     * Gets the login attempts for a username.
     *
     * @param username
     * @return Returns the Login Attempts if the most recent login attempt was less than the time allowed in which
     * you can perform the login attempts, stored in MINUTES_ACCOUNT_LOCKED. Returns 0 if no recent logins were
     * performed
     *
     */
    public int getLoginAttemptsSinceTimeout(final String username) {
        final Optional<LoginAttempt> maybeLogin = loginAttemptRepository.findById(username);
        if (maybeLogin.isPresent()) {
            final LoginAttempt attempt = maybeLogin.get();
            final LocalDateTime mostRecentAttempt = attempt.getMostRecentAttempt();
            final LocalDateTime now = LocalDateTime.now();
            final LocalDateTime comparisonDateTime = LocalDateTime.from(mostRecentAttempt);
            final long minutes = comparisonDateTime.until(now, ChronoUnit.MINUTES);
            return minutes > MINUTES_ACCOUNT_LOCKED ? 0 : attempt.getAttempts();
        }
        return 0;
    }
}
