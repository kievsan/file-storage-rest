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
public class AuthErrResponse implements AuthResponse { // Ответ: сообщение о неудачной аутентификации

    @JsonProperty("message")
    private String message;

    @JsonProperty("id")
    private Integer id;
}
