package com.bastronaut.bigspender.models;



import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Getter
    private String firstName;

    @Getter
    private String email;

    private String password;


    public User(String email, String firstName, String password) {
        this.email = email;
        this.firstName = firstName;
        this.password = password;
    }


}
