package com.bastronaut.bigspender.dto.in;

import com.bastronaut.bigspender.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@AllArgsConstructor
public class UserRegistrationDTO {

    @NotNull
    private final String name;
    @NotNull
    @Email
    private final String email;
    @NotNull
    @Size(min=8)
    private final String password;
}
