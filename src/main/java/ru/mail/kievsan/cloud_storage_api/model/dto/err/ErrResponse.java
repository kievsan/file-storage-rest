package ru.mail.kievsan.cloud_storage_api.model.dto.err;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrResponse{

    @JsonProperty("message")
    private String message;

    @JsonProperty("id")
    private Integer id;

    @Override
    public String toString() {
        return message + " (id=" + id + ")";
    }
}
