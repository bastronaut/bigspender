package com.bastronaut.bigspender.repositories;


import com.bastronaut.bigspender.models.Label;
import com.bastronaut.bigspender.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface LabelRepository extends JpaRepository<Label, Long> {

    List<Label> findAllByUser(final User user);

    Label save(final Label label);

//    List<Label> saveAll(final List<Label> label);

    long deleteByIdAndUser(final long id, final User user);
}