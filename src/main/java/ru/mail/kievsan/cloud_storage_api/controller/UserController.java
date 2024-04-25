package ru.mail.kievsan.cloud_storage_api.controller;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.mail.kievsan.cloud_storage_api.model.dto.auth.AuthResponse;
import ru.mail.kievsan.cloud_storage_api.model.dto.auth.SignUpRequest;
import ru.mail.kievsan.cloud_storage_api.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/reg")
    @PermitAll
    public ResponseEntity<AuthResponse> register(@RequestHeader("auth-token") String authToken,
                                                 @RequestBody SignUpRequest request) {
        return userService.register(authToken, request);
    }
}
