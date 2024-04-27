package ru.mail.kievsan.cloud_storage_api.controller;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
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
@EnableMethodSecurity
@Slf4j
@RequestMapping("/api/v1/list")
public class FileListController {

    private final FileListService service;

//    @GetMapping("/")
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PermitAll
    @GetMapping
    ResponseEntity<List<FileListResponse>> getFileList(@RequestHeader("auth-token") String authToken,
                                                       @RequestParam("limit") Integer limit) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("  Start File list controller:  limit = {},  user:  {}, {}", limit, auth.getName(), auth.getAuthorities());
        return ResponseEntity.ok(service.getFileList(authToken, limit));
    }
}
