package ru.mail.kievsan.cloud_storage_api.model.dto.user;

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

    static final int max = 100;
    static final int min = 6;

    @Size(min = min/2, max = max/4)
    @NotBlank(message = "nickname не должен быть пустым!")
    private String nickname;

    @Size(min = min, max = max)
    @NotBlank(message = "email не должен быть пустым!")
    private String email;

    @Size(min = min, max = max/4)
    @NotBlank(message = "password не должен быть пустым!")
    private String password;

    @NotBlank(message = "role не может быть пустой!")
    private Role role;
}
