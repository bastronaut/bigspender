package com.bastronaut.bigspender.models;



import lombok.Data;
import lombok.Getter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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

    @OneToMany(targetEntity = Transaction.class, cascade = {CascadeType.ALL})
    private final List<Transaction> transactions;

    @Column(nullable = false, name = "import_date")
    private final LocalDate importDate;

    @Column(nullable = false, name = "import_count")
    private final int importCount;

    @OneToOne(targetEntity = User.class)
    private final User user;

    public TransactionImport(List<Transaction> transactions, User user) {
        this.transactions = transactions;
        this.importDate = LocalDate.now();
        this.importCount = transactions.size();
        this.user = user;
    }
}
