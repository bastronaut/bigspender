package com.bastronaut.bigspender.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class SecurityUtil {

    private static PasswordEncoder encoder = new BCryptPasswordEncoder(10);

    public static PasswordEncoder getEncoder() {
        return encoder;
    }

    public static String encode(String charSequence) {
        return encoder.encode(charSequence);
    }
}
