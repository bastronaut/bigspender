package com.bastronaut.bigspender.repositories;


import com.bastronaut.bigspender.models.Label;
import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;


public interface LabelRepository extends JpaRepository<Label, Long> {

    List<Label> findAllByUser(final User user);
    List<Label> findByIdInAndUser(final List<Long> ids, final User user);
    Label findByIdAndUser(final long id, final User user);

    Label save(final Label label);

    //  Property Expressions to find a transaction in a list of transactions
    Set<Label> findByTransactions_idAndUser(final long id, final User user);

    Label deleteByIdAndUser(final long id, final User user);

    List<Label> deleteByIdInAndUser(final List<Long> ids, final User user);
}
