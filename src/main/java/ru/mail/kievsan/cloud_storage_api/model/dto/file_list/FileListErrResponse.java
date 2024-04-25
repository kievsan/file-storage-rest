package ru.mail.kievsan.cloud_storage_api.model.dto.file_list;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileListErrResponse implements FileListResponse { // Ответ: сообщение о неудачном запросе Списка файлов

    @JsonProperty("message")
    private String message;

    @JsonProperty("id")
    private Integer id;
}
