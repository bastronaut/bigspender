package com.bastronaut.bigspender.repositories;

import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Transaction save(Transaction transaction);

    List<Transaction> findAllByUserId(int userid);

    long deleteByUser(final User user);

    long deleteByIdAndUser(final long id, final User user);

    void deleteAll(Iterable<? extends Transaction> entities);

    long countByUserId(int userid);
}
