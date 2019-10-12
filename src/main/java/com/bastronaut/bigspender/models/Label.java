package com.bastronaut.bigspender.models;

import com.bastronaut.bigspender.dto.shared.LabelDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.bastronaut.bigspender.utils.ApplicationConstants.DEFAULT_LABELCOLOR;

/**
 * Transactions and Labels have a many to many relationship. Idea is to allow user to create as many custom
 * labels as they like and assign them to transactions, which we will learn for predictive model. A label can
 * have a color for frontend display purposes.
 *
 * Has a custom equals and hashcode to allow comparing labels without its relation to transactions
 */
@Entity
@EqualsAndHashCode
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

    @Getter @Setter
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "label_id")
    private long id;

    @Getter @Setter
    @Column(nullable = false)
    private String name;

    @Getter @Setter
    @Column(nullable = false)
    private String color;

    @Getter @Setter
    @ToString.Exclude
    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(optional = false)
    private User user;

    @Getter
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "labels", cascade = {
            CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Transaction> transactions = new HashSet<>();

    public void setTransactions(final Set<Transaction> transactions) {
        this.transactions = new HashSet<>(transactions);
    }


    public static Label fromLabelDTO(final LabelDTO labelDTO, final User user) {
        return new Label(labelDTO.getName(), user, labelDTO.getColor());
    }

}
