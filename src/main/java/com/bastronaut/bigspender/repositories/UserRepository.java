package com.bastronaut.bigspender.repositories;

import com.bastronaut.bigspender.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    User save(User user);

    Optional<User> findById(int id);



}
