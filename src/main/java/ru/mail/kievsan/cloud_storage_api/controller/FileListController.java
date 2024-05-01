package ru.mail.kievsan.cloud_storage_api.controller;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;
import ru.mail.kievsan.cloud_storage_api.model.dto.file_list.FileListResponse;
import ru.mail.kievsan.cloud_storage_api.service.FileListService;
import ru.mail.kievsan.cloud_storage_api.util.AuthTokenValidator;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequiredArgsConstructor
@EnableMethodSecurity
@RequestMapping("/api/v1/list")
public class FileListController {

    private final FileListService service;
    private final AuthTokenValidator validator;

//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PermitAll
    @GetMapping
    ResponseEntity<List<FileListResponse>> getFileList(@RequestHeader("auth-token") String authToken,
                                                       @RequestParam("limit") Integer limit) {
        return ResponseEntity.ok(service.getFileList(limit, validator.validateJWT(authToken,
                "Start File list controller", "Get file list error")));
    }
}
