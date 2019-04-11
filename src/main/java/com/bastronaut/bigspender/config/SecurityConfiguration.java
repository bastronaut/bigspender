package com.bastronaut.bigspender.config;


import com.bastronaut.bigspender.repositories.UserRepository;
import com.bastronaut.bigspender.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.bastronaut.bigspender.utils.ApplicationConstants.USERS_ENDPOINT;


//    INSERT INTO users (id, email, name, password) VALUES (2, 'test@email.com', 'testy', '$2a$11$B3E9YAm5FTG8mwccNjMBAunJt3WXSnC2sPy.PlPCv4p8gjoQ/wlQa') # pw = testor

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
@Configuration()
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService userDetailsService;
//
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(encoder());

    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        PasswordEncoder encodah = new BCryptPasswordEncoder(11);
        System.out.println(encodah.encode("testor"));

        http
                .authorizeRequests()
                .antMatchers("/**").authenticated()
                .antMatchers("/login").permitAll()
                .antMatchers(HttpMethod.POST, USERS_ENDPOINT).permitAll() // sign up
                .and()
                .formLogin().permitAll()
                .and()
                .csrf().disable();
    }


    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(11);
    }




}
