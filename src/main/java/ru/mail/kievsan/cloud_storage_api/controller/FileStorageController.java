package ru.mail.kievsan.cloud_storage_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.mail.kievsan.cloud_storage_api.model.dto.file.EditFileNameRequest;
import ru.mail.kievsan.cloud_storage_api.model.entity.File;
import ru.mail.kievsan.cloud_storage_api.service.FileStorageService;
import ru.mail.kievsan.cloud_storage_api.util.UserProvider;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/file")
public class FileStorageController {

    private final FileStorageService service;
    private final UserProvider provider;

    private final String header = "Start File controller";

    @PostMapping()
    public ResponseEntity<?> uploadFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam("filename") String filename,
                                        @RequestBody MultipartFile file) {
        service.uploadFile(filename, file, provider.trueUser(authToken,
                String.format("%s, upload file '%s'", header, filename),
                "Upload file error"));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<?> editFileName(@RequestHeader("auth-token") String authToken,
                                          @RequestParam("filename") String filename,
                                          @RequestBody EditFileNameRequest request) {
        service.editFileName(filename, request.getName(), provider.trueUser(authToken,
                String.format("%s, edit file name '%s' -> '%s'", header, filename, request.getName()),
                "Edit file name error"));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteFileName(@RequestHeader("auth-token") String authToken,
                                          @RequestParam("filename") String filename) {
        service.deleteFile(filename, provider.trueUser(authToken,
                String.format("%s, delete file '%s'", header, filename),
                "Delete file error"));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> downloadFile(@RequestHeader("auth-token") String authToken,
                                          @RequestParam("filename") String filename) {
        File file = service.downloadFile(filename, provider.trueUser(authToken,
                String.format("----------download resource----------\n %s, download file '%s'", header, filename),
                "Download file error"));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file.getContent());
    }
}
