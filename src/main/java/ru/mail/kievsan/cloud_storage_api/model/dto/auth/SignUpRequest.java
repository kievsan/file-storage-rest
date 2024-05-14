package ru.mail.kievsan.cloud_storage_api.model.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mail.kievsan.cloud_storage_api.model.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest { // Запрос на регистрацию

    static final int max = 30;

    @Size(min = 3, max = max, message = "nickname: от 3 до " + max + " символов")
    @NotBlank(message = "nickname не должен быть пустым!")
    private String nickname;

    @Size(min = 6, max = max, message = "email: до " + max + " символов")
    @NotBlank(message = "email не должен быть пустым!")
    private String email;

    @NotBlank(message = "password не должен быть пустым!")
    private String password;

    @Size(min = 6, max = max, message = "Role.name: ROLE_ADMIN, ROLE_USER, ...")
    @NotBlank(message = "role не может быть пустой!")
    private Role role;
}
