package ru.mail.kievsan.cloud_storage_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.kievsan.cloud_storage_api.model.dto.file_list.FileListResponse;
import ru.mail.kievsan.cloud_storage_api.service.FileListService;

import java.util.List;

@CrossOrigin(
        origins = "${origins.clients}",
        allowCredentials = "true"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/list")
public class FileListController {

    private final FileListService service;

    @GetMapping
    ResponseEntity<List<FileListResponse>> getFileList(@RequestHeader("auth-token") String authToken,
                                                       @RequestParam("limit") Integer limit) {
        return ResponseEntity.ok(service.getFileList(authToken, limit));
    }
}
