package ru.mail.kievsan.cloud_storage_api.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse { // Ответ: токен доступа

    @JsonProperty("auth-token")
    private String authToken;
}
