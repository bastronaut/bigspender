package com.bastronaut.bigspender.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "login_attempts")
@AllArgsConstructor
@NoArgsConstructor
public class LoginAttempt {

    @Id
    String username;
    int attempts;
    LocalDateTime mostRecentAttempt;
}
