package com.bastronaut.bigspender.models;



import com.bastronaut.bigspender.config.SecurityConfiguration;
import com.bastronaut.bigspender.dto.UserRegistrationDTO;
import com.bastronaut.bigspender.dto.UserUpdateDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    @Setter
    private int id;

    @Column(nullable = false)
    @Setter
    private String name;

    @Column(nullable = false)
    @Setter
    private String email;

    @Column(nullable = false)
    @Setter
    private String password;

    public User() {}

    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public static User fromUserRegistrationDTO(UserRegistrationDTO userRegistrationDTO) {
        return new User(userRegistrationDTO.getEmail(),
                userRegistrationDTO.getName(), userRegistrationDTO.getPassword());
    }

    public static User fromUserUpdateDTO(UserUpdateDTO userUpdateDTO) {
        return new User(userUpdateDTO.getEmail(), userUpdateDTO.getName(), userUpdateDTO.getPassword());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("User"));
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
