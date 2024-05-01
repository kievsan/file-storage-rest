package ru.mail.kievsan.cloud_storage_api.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.kievsan.cloud_storage_api.model.dto.auth.*;
import ru.mail.kievsan.cloud_storage_api.service.AuthService;

@Slf4j
@CrossOrigin(origins = {"http://localhost:8080"}, allowCredentials = "true", methods = {RequestMethod.POST}, allowedHeaders = {"*"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @PermitAll
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
//        return ResponseEntity.ok()
//                .header("Access-Control-Allow-Origin", "http://localhost:8080")
//                .header("Vary", "Origin")
//                .body(authService.authenticate(request));
    }

    @PostMapping ("/logout")
    @PermitAll
    public ResponseEntity<String> logout(@RequestHeader("auth-token") String authToken,
                                         HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.logout(authToken, request, response));
    }

}
