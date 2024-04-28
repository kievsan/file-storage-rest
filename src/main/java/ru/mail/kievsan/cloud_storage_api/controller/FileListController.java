package ru.mail.kievsan.cloud_storage_api.controller;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;
import ru.mail.kievsan.cloud_storage_api.model.dto.file_list.FileListResponse;
import ru.mail.kievsan.cloud_storage_api.service.FileListService;
import ru.mail.kievsan.cloud_storage_api.util.ControllerStarter;

import java.util.List;

@CrossOrigin(
        origins = "${origins.clients}",
        allowCredentials = "true"
)
@RestController
@RequiredArgsConstructor
@EnableMethodSecurity
@RequestMapping("/api/v1/list")
public class FileListController {

    private final FileListService service;
    private final ControllerStarter starter;

//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PermitAll
    @GetMapping
    ResponseEntity<List<FileListResponse>> getFileList(@RequestHeader("auth-token") String authToken,
                                                       @RequestParam("limit") Integer limit) {
        starter.startLog("Start File list controller");
        return ResponseEntity.ok(service.getFileList(limit, starter.validate(authToken, "Get file list error")));
    }
}
