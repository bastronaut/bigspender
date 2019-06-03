package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.models.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;

public class SpringSecurityWebTestConfig {

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        User basicUser = new User("user@company.com", "password");
        return new InMemoryUserDetailsManager(Arrays.asList(basicUser));
    }
}
