package ru.mail.kievsan.cloud_storage_api.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.kievsan.cloud_storage_api.exception.UnauthorizedUserException;
import ru.mail.kievsan.cloud_storage_api.model.dto.auth.SignUpRequest;
import ru.mail.kievsan.cloud_storage_api.model.dto.auth.SignUpResponse;
import ru.mail.kievsan.cloud_storage_api.service.UserService;
import ru.mail.kievsan.cloud_storage_api.util.AuthTokenValidator;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final AuthTokenValidator validator;

    @PostMapping("/reg")
    @PermitAll
    public ResponseEntity<SignUpResponse> register(@RequestBody SignUpRequest request, HttpServletRequest httpRequest) {
        try {
            var token = httpRequest.getHeader("auth-token");
            return ResponseEntity.ok(userService.register(request,
                    validator.validateJWT(token, "Start User controller", "User registration WARNING")));
        } catch (UnauthorizedUserException ex) {
            return ResponseEntity.ok(userService.register(request, null));
        }
    }
}
