package com.bastronaut.bigspender.repositories;

import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.TransactionImport;
import com.bastronaut.bigspender.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionImportRepository extends JpaRepository<TransactionImport, Long> {

//    TransactionImport save(TransactionImport transactionImport);

    List<TransactionImport> findAllByUser(User user);

    TransactionImport findById(long id);
}
