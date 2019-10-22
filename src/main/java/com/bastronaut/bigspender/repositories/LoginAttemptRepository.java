package com.bastronaut.bigspender.repositories;

import com.bastronaut.bigspender.models.LoginAttempt;
import com.bastronaut.bigspender.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, String> {

    Optional<LoginAttempt> findById(String username);

    @Modifying
    @Query(value = "delete from LoginAttempt where username = ?1")
    void deleteByUsername(String username);

}
