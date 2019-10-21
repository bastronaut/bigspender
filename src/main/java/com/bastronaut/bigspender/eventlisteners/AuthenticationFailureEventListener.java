package com.bastronaut.bigspender.eventlisteners;

import com.bastronaut.bigspender.services.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Event listener to avoid brute force attacks on logins
 *
 * Design:
 * - Store the number of login attempts per username in db
 * - Store a timestamp of the latest failed login attempt
 *
 * Upon login:
 * - Retrieve the number of login attempts for user
 * - Retrieve the timestamp of failed login attempt
 *      - if the number of login attempts > MAX_ALLOWED_LOGIN_ATTEMPTS, &&
 *        if the most recent failed login attempt is less than 24 hours ago
 *          - Refuse the login
 *
 * Upon successful login:
 * - Delete the stored login attempts
 *
 * Upon unsuccessful login:
 * - Increment attempt counter by 1
 * - Update the most recent failed login attempt
 *
 */
@Component
public class AuthenticationFailureEventListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        final Authentication authentication = event.getAuthentication();
        if (authentication != null) {
            final Object principal = authentication.getPrincipal();
            final String username = principal.toString();
            loginAttemptService.unsuccessfulLogin(username);
        }
    }
}
