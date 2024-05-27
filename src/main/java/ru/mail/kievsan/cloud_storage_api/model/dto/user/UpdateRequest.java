package ru.mail.kievsan.cloud_storage_api.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequest { // Пользовательский запрос на изменение user

    static final int max = 100;
    static final int min = 6;

    @Size(min = min, max = max)
    @NotBlank(message = "email не должен быть пустым!")
    @Email
    private String email;

    @Size(min = min, max = max/4)
    @NotBlank(message = "password не должен быть пустым!")
    private String password;
}
