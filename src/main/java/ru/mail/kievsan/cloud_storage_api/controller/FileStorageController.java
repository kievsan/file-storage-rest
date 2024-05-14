package ru.mail.kievsan.cloud_storage_api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;
import ru.mail.kievsan.cloud_storage_api.model.dto.err.ErrResponse;
import ru.mail.kievsan.cloud_storage_api.model.dto.file.EditFileNameRequest;
import ru.mail.kievsan.cloud_storage_api.model.entity.File;
import ru.mail.kievsan.cloud_storage_api.security.SecuritySettings;
import ru.mail.kievsan.cloud_storage_api.service.FileStorageService;
import ru.mail.kievsan.cloud_storage_api.util.UserProvider;

import java.util.concurrent.ForkJoinPool;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(SecuritySettings.FILE_URI)
@Slf4j
public class FileStorageController {

    private final FileStorageService service;
    private final UserProvider provider;

    private final String header = "Start File controller";

    @PostMapping()
    public ResponseEntity<?> uploadFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam("filename") String filename,
                                        @RequestBody MultipartFile file) {
        service.uploadFile(filename, file, provider.trueUser(authToken,
                "%s, upload file '%s'".formatted(header, filename), "Upload file error"));
        return ResponseEntity.ok(HttpStatus.OK);
    }

//    @PostMapping
//    public DeferredResult<ResponseEntity<?>> uploadFile(@RequestHeader("auth-token") String authToken,
//                                                        @RequestParam("filename") String filename,
//                                                        @RequestBody MultipartFile file) {
//        DeferredResult<ResponseEntity<?>> deferredResult = setDeferredResult(filename, "Upload");
//        ForkJoinPool.commonPool().submit(() -> {
//            service.uploadFile(filename, file, provider.trueUser(authToken,
//                    String.format("%s, upload file '%s'", header, filename),"Upload file error"));
//            deferredResult.setResult(ResponseEntity.ok(HttpStatus.OK)
//            );
//        });
//        provider.logg(" Upload file '" + filename + "' deferred...");
//        return deferredResult;
//    }

    @PutMapping()
    public ResponseEntity<?> editFileName(@RequestHeader("auth-token") String authToken,
                                          @RequestParam("filename") String filename,
                                          @RequestBody EditFileNameRequest request) {
        service.editFileName(filename, request.getFilename(), provider.trueUser(authToken,
                "%s, edit file name '%s' -> '%s'".formatted(header, filename, request.getFilename()),
                "Edit file name error"));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteFileName(@RequestHeader("auth-token") String authToken,
                                          @RequestParam("filename") String filename) {
        service.deleteFile(filename, provider.trueUser(authToken,
                "%s, delete file '%s'".formatted(header, filename), "Delete file error"));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> downloadFile(@RequestHeader("auth-token") String authToken,
                                          @RequestParam("filename") String filename) {
        File file = service.downloadFile(filename, provider.trueUser(authToken,
                "----------download resource----------\n %s, download file '%s'".formatted(header, filename),
                "Download file error"));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file.getContent());
    }

//    @GetMapping(produces = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public DeferredResult<ResponseEntity<?>> downloadFile(@RequestHeader("auth-token") String authToken,
//                                                          @RequestParam("filename") String filename) {
//        DeferredResult<ResponseEntity<?>> deferredResult = setDeferredResult(filename, "Download");
//        ForkJoinPool.commonPool().submit(() -> {
//            File file = service.downloadFile(filename, provider.trueUser(authToken,
//                        String.format("----------download resource----------\n %s, download file '%s'", header, filename),
//                        "Download file error"));
//            deferredResult.setResult(ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
//                    .body(file.getContent()));
//        });
//        return deferredResult;
//    }
//
//    private DeferredResult<ResponseEntity<?>> setDeferredResult(String filename, String serviceTitle) {
//        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>(5000L);
//        deferredResult.onCompletion(() -> provider.logg(" " + serviceTitle + " file '" + filename + "' complete"));
//        deferredResult.onTimeout(() -> {
//            String errMsg = serviceTitle + " file '" + filename + "' timed out";
//            provider.logg(errMsg);
//            deferredResult.setErrorResult(
//                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                            .body(new ErrResponse("Request timeout occurred. " + errMsg, 0))
//            );
//        });
//        provider.logg(" " + serviceTitle + " file '" + filename + "', deferred result was created");
//        return deferredResult;
//    }
}
