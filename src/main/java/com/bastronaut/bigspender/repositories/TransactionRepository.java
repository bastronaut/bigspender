package com.bastronaut.bigspender.repositories;

import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.TransactionImport;
import com.bastronaut.bigspender.models.User;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<User, Integer> {

    Transaction save(Transaction transaction);

    TransactionImport save(TransactionImport transactionImport);

    List<Transaction> save(List<Transaction> transactions);

    Optional<Transaction> findById(long id);

    List<Transaction> findAllByUserId(int userid);
}
