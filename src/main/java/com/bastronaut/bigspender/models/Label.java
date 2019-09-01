package com.bastronaut.bigspender.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import sun.swing.StringUIClientPropertyKey;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;

/**
 * Transactions and Labels have a many to many relationship. Idea is to allow user to create as many custom
 * labels as they like and assign them to transactions, which we will learn for predictive model. A label can
 * have a color for frontend display purposes
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "labels")
public class Label {

    public Label(final String name, final User user, @Nullable final String color) {
        this.user = user;
        this.name = name;
        if (StringUtils.isBlank(color)) {
            this.color =
        }
    }

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "label_id")
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne
    private User user;

    @ManyToMany
    private Transaction transaction;

}
