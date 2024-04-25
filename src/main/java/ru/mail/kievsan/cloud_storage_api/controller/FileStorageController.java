package ru.mail.kievsan.cloud_storage_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/file")
public class FileStorageController {

    @GetMapping("/")
    public ResponseEntity<String> hi(Authentication authentication) {
        return ResponseEntity.ok("Hi, " + authentication.getName() + " !!!  " + authentication.getAuthorities());
    }
}
