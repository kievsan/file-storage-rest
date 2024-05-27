package ru.mail.kievsan.cloud_storage_api.model.dto.auth;

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
public class AuthRequest { // Запрос на аутентификацию

    static final int max = 100;

    @Size(min = 3, max = max, message = "login от 3 до " + max + " символов")
    @NotBlank(message = "login не может быть пустым!")
    private String login;

    @Size(min = 3, max = max, message = "password не более " + max + " символов")
    @NotBlank(message = "password не может быть пустым!")
    private String password;
}
