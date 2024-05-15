package ru.mail.kievsan.cloud_storage_api.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.constraints.Pattern;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.mail.kievsan.cloud_storage_api.model.dto.file_list.FileListResponse;
import ru.mail.kievsan.cloud_storage_api.service.FileListService;
import ru.mail.kievsan.cloud_storage_api.util.UserProvider;

import java.util.List;

@CrossOrigin(methods = {RequestMethod.GET})
@RestController
@Validated
@RequiredArgsConstructor
@EnableMethodSecurity
@Slf4j
@RequestMapping("/api/v1/list")
public class FileListController {

    private final FileListService service;
    private final UserProvider provider;

    @PermitAll
    @GetMapping(headers = "auth-token")
    ResponseEntity<List<FileListResponse>> getFileList(@RequestHeader(name = "auth-token")
                                                       @NonNull @Pattern(regexp = "^Bearer .+$") String authToken,
                                                       @RequestParam("limit") Integer limit) {
        return ResponseEntity.ok(service.getFileList(limit,
                provider.trueUser(authToken,"Start File list controller", "Get file list error", log::error)));
    }
}
