package ru.mail.kievsan.cloud_storage_api.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.kievsan.cloud_storage_api.model.dto.auth.*;
import ru.mail.kievsan.cloud_storage_api.service.AuthService;
import ru.mail.kievsan.cloud_storage_api.util.UserProvider;

@CrossOrigin(methods = {RequestMethod.POST})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService service;
    private final UserProvider provider;

    @PostMapping("/login")
    @PermitAll
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping ("/logout")
    @PermitAll
    public ResponseEntity<String> logout(@RequestHeader("auth-token") String authToken,
                                         HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(service.logout(request, response, provider.trueUser(authToken,
                        "Start Auth controller", "Logout error")));
    }

}
