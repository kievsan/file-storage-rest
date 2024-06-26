package ru.mail.kievsan.cloud_storage_api.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mail.kievsan.cloud_storage_api.model.Role;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponse { // Ответ: новый user

    private Long id;
    private String nickname;
    private String email;
    private Role role;

    public SignUpResponse(User user) {
        id = user.getId();
        nickname = user.getNickname();
        email = user.getEmail();
        role = user.getRole();
    }
}
