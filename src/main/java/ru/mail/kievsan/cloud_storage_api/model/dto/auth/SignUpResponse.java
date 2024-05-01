package ru.mail.kievsan.cloud_storage_api.model.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mail.kievsan.cloud_storage_api.model.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponse { // Ответ: новый user

    private Long id;
    private String nickname;
    private String email;
    private Role role;
}
