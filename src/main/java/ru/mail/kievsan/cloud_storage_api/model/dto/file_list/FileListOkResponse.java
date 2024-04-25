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
public class FileListOkResponse implements FileListResponse {

    @JsonProperty("filename")
    private String filename;

    @JsonProperty("size")
    private Long size;

}
