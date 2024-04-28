package ru.mail.kievsan.cloud_storage_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.mail.kievsan.cloud_storage_api.model.dto.file.EditFileNameRequest;
import ru.mail.kievsan.cloud_storage_api.service.FileStorageService;
import ru.mail.kievsan.cloud_storage_api.util.ControllerStarter;

@CrossOrigin(
        origins = "${origins.clients}",
        allowCredentials = "true"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/file")
public class FileStorageController {

    private final FileStorageService service;
    private final ControllerStarter starter;

    private final String header = "Start File controller";

    @PostMapping()
    public ResponseEntity<?> uploadFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam("filename") String filename,
                                        @RequestBody MultipartFile file) {
        starter.startLog(String.format("%s, upload file '%s'", header, filename));
        service.uploadFile(filename, file, starter.validate(authToken, "Upload file error"));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<?> editFileName(@RequestHeader("auth-token") String authToken,
                                          @RequestParam("filename") String filename,
                                          @RequestBody EditFileNameRequest request) {
        starter.startLog(String.format("%s, edit file name '%s' -> '%s'", header, filename, request.getFilename()));
        service.editFileName(filename, request.getFilename(), starter.validate(authToken, "Edit file name error"));
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
