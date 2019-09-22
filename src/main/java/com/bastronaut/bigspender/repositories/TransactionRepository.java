package com.bastronaut.bigspender.repositories;

import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Transaction save(final Transaction transaction);

    List<Transaction> findAllByUser(final User userid);

    List<Transaction> findByIdInAndUser(final List<Long> ids, User user);

    List<Transaction> deleteByIdInAndUser(final List<Long> ids, User user);

    Optional<Transaction> findByIdAndUser(long id, User user);

    long deleteByUser(final User user);

    long deleteByIdAndUser(final long id, final User user);
}
