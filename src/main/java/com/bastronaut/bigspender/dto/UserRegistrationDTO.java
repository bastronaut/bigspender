package com.bastronaut.bigspender.dto;

import com.bastronaut.bigspender.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserRegistrationDTO {
    private final String firstName;
    private final String email;
    private final String password;
    List<MultipartFile> file;
}
