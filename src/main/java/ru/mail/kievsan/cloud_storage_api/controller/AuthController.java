package ru.mail.kievsan.cloud_storage_api.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.kievsan.cloud_storage_api.model.dto.auth.*;
import ru.mail.kievsan.cloud_storage_api.service.AuthService;

@CrossOrigin(
        origins = "${origins.clients}",
        allowCredentials = "true"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @PermitAll
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return authService.authenticate(request);
    }

    @PostMapping ("/logout")
    @PermitAll
    public ResponseEntity<String> logout(@RequestHeader("auth-token") String authToken,
            HttpServletRequest request, HttpServletResponse response) {
        return authService.logout(authToken, request, response);
    }

    @GetMapping ("/logout")
    @PermitAll
    public ResponseEntity<String> logout2(@RequestHeader("auth-token") String authToken,
            HttpServletRequest request, HttpServletResponse response) {
        return authService.logout(authToken, request, response);
    }

}
