package com.bastronaut.bigspender.config;

import com.bastronaut.bigspender.exceptions.LoginAttemptException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A custom AuthenticationEntryPoint can be used to set necessary response headers, content-type, and so on
 * before sending the response back to the client.
 */
@Component
public class AuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
            throws IOException, ServletException {
        // Handle too many login attempts separately
        if (authEx.getCause() instanceof LoginAttemptException) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendError(HttpStatus.FORBIDDEN.value(), authEx.getMessage());
        }
        response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // RealmName appears in the login window (Firefox).
        setRealmName("bigspender");
        super.afterPropertiesSet();
    }
}
