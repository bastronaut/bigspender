package com.bastronaut.bigspender.models;

import com.bastronaut.bigspender.dto.shared.LabelDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import sun.swing.StringUIClientPropertyKey;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

import static com.bastronaut.bigspender.utils.ApplicationConstants.DEFAULT_LABELCOLOR;

/**
 * Transactions and Labels have a many to many relationship. Idea is to allow user to create as many custom
 * labels as they like and assign them to transactions, which we will learn for predictive model. A label can
 * have a color for frontend display purposes
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "labels")
@ToString
public class Label {


    public Label(final String name, final User user) {
        this(name, user, null);
    }

    public Label(final String name, final User user, @Nullable final String color) {
        this.user = user;
        this.name = name;
        if (StringUtils.isBlank(color)) {
            this.color = DEFAULT_LABELCOLOR;
        } else {
            this.color = color;
        }
    }

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "label_id")
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;


    @ToString.Exclude
    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(optional = false)
    private User user;

    @ToString.Exclude
    @ManyToMany(mappedBy = "labels", cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE})
    private List<Transaction> transactions = new ArrayList<>();


    public void addTransaction(final Transaction transaction) {
        this.transactions.add(transaction);
        transaction.getLabels().add(this);
    }

    public void removeTransaction(final Transaction transaction) {
        this.transactions.remove(transaction);
        transaction.getLabels().remove(this);
    }

    @PreRemove
    private void removeTransactionsFromLabels() {
        transactions.forEach(t -> t.getLabels().remove(this));
    }


    public static Label fromLabelDTO(final LabelDTO labelDTO, final User user) {
        return new Label(labelDTO.getName(), user, labelDTO.getColor());
    }

}
