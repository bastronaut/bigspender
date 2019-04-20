package com.bastronaut.bigspender.repositories;

import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.TransactionImport;
import com.bastronaut.bigspender.models.User;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Transaction save(Transaction transaction);

    TransactionImport save(TransactionImport transactionImport);

    List<Transaction> findAllByUserId(int userid);

    Optional<Transaction> findbyUserIdAndId(int userid, long transactionid);

    long deleteByUserIdAndId(int userid, long transactionid);

    long remove(Transaction transaction);

    void deleteAll(Iterable<? extends Transaction> entities);

    long deleteInBulkByUserId(int userid);

    long countByUserId(int userid);
}
