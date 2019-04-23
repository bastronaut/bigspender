package com.bastronaut.bigspender.repositories;

import com.bastronaut.bigspender.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Transaction save(Transaction transaction);

    List<Transaction> findAllByUserId(int userid);

//    Optional<Transaction> findbyUserIdAndId(int userid, long transactionid);

//    long deleteByUserIdAndTransactionId(int userid, long transactionid);

//    long remove(Transaction transaction);

    void deleteAll(Iterable<? extends Transaction> entities);

    long deleteInBulkByUserId(int userid);

    long countByUserId(int userid);
}
