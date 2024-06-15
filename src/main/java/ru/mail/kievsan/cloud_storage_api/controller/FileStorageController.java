package ru.mail.kievsan.cloud_storage_api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.mail.kievsan.cloud_storage_api.model.dto.file.EditFileNameRequest;
import ru.mail.kievsan.cloud_storage_api.model.entity.File;
import ru.mail.kievsan.cloud_storage_api.security.ISecuritySettings;
import ru.mail.kievsan.cloud_storage_api.service.FileStorageService;
import ru.mail.kievsan.cloud_storage_api.util.UserProvider;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(ISecuritySettings.FILE_URI)
@Slf4j
public class FileStorageController {

    private final FileStorageService service;
    private final UserProvider provider;

    private final String logTitle = "Start File controller";

    @PostMapping()
    public ResponseEntity<?> uploadFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam("filename") String filename,
                                        @RequestBody MultipartFile file) {
        service.uploadFile(filename, file, provider.trueUser(authToken,
                "%s, upload file '%s'".formatted(logTitle, filename), "Upload file error", log::error));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<?> editFileName(@RequestHeader("auth-token") String authToken,
                                          @RequestParam("filename") String filename,
                                          @RequestBody EditFileNameRequest request) {
        service.editFileName(filename, request.getFilename(),
                provider.trueUser(authToken,
                        "%s, edit file name '%s' -> '%s'".formatted(logTitle, filename, request.getFilename()),
                        "Edit file name error", log::error));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteFile(@RequestHeader("auth-token") String authToken,
                                            @RequestParam("filename") String filename) {
        service.deleteFile(filename, provider.trueUser(authToken,
                "%s, delete file '%s'".formatted(logTitle, filename), "Delete file error", log::error));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> downloadFile(@RequestHeader("auth-token") String authToken,
                                          @RequestParam("filename") String filename) {
        File file = service.downloadFile(filename, provider.trueUser(authToken,
                "----------download resource----------\n %s, download file '%s'".formatted(logTitle, filename),
                "Download file error", log::error));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file.getContent());
    }
}
