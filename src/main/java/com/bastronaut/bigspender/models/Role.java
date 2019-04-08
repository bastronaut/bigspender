package com.bastronaut.bigspender.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "role")
@AllArgsConstructor
@Getter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final int id;

    private final String name;
}
