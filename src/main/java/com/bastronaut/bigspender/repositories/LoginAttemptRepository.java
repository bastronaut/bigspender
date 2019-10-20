package com.bastronaut.bigspender.repositories;

import com.bastronaut.bigspender.models.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, String> {

    Optional<LoginAttempt> findById(String username);
    void deleteById(String username);

}
