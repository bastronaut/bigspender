package com.bastronaut.bigspender.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
public class LoginAttempt {

    @Id
    String username;
    int attempts;
}
