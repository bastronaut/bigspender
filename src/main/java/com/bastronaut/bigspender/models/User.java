package com.bastronaut.bigspender.models;



import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;


    public User(String email, String firstName, String password) {
        this.email = email;
        this.firstName = firstName;
        this.password = password;
    }




}
