package ru.mail.kievsan.cloud_storage_api.model.dto.file;

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
public class EditFileNameRequest { // Запрос на изменение имени файла

    static final int max = 100;

    @Size(min = 1, max = max, message = "filename: до " + max + " символов")
    @NotBlank(message = "filename не должен быть пустым!")
    private String filename;
}
