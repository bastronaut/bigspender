package com.bastronaut.bigspender.models;



import lombok.Data;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table
@Data
public class TransactionImport {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToMany(targetEntity = Transaction.class)
    private final List<Transaction> transactions;
    private final LocalDate importDate;
    private final int importCount;

    public TransactionImport(List<Transaction> transactions) {
        this.transactions = transactions;
        this.importDate = LocalDate.now();
        this.importCount = transactions.size();
    }
}
