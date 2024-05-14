package ru.mail.kievsan.cloud_storage_api.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mail.kievsan.cloud_storage_api.exception.UnauthorizedUserException;
import ru.mail.kievsan.cloud_storage_api.model.dto.auth.SignUpRequest;
import ru.mail.kievsan.cloud_storage_api.model.dto.auth.SignUpResponse;
import ru.mail.kievsan.cloud_storage_api.security.SecuritySettings;
import ru.mail.kievsan.cloud_storage_api.service.UserService;
import ru.mail.kievsan.cloud_storage_api.util.UserProvider;

@RestController
@RequiredArgsConstructor
@RequestMapping(SecuritySettings.USER_URI)
public class UserController {

    private final UserService userService;
    private final UserProvider userProvider;

    @PermitAll
    @PostMapping
    public ResponseEntity<SignUpResponse> register(@RequestBody SignUpRequest request,
                                                   HttpServletRequest httpRequest) {
        try {
            var token = httpRequest.getHeader("auth-token");
            return ResponseEntity.ok(userService.register(request,
                    userProvider.trueUser(token, "Start User controller", "User registration WARNING")));
        } catch (UnauthorizedUserException ex) {
            return ResponseEntity.ok(userService.register(request, null));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<SignUpResponse> getUser(@PathVariable Long id,
                                                  @RequestHeader(name = "auth-token") String authToken) {
        return ResponseEntity.ok(userService.getUserById(id,
                userProvider.trueUser(authToken,"Start User controller", "Get User error")));
    }

    @GetMapping
    public ResponseEntity<SignUpResponse> getOwner(@RequestHeader(name = "auth-token") String authToken) {
        return ResponseEntity.ok(userService.getCurrentUser(userProvider.trueUser(authToken,
                "Start User controller", "Get owner error")));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delUser(@PathVariable Long id,
                                                  @RequestHeader(name = "auth-token") String authToken) {
        userService.delUserById(id,
                userProvider.trueUser(authToken,"Start User controller", "Del User error"));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> delOwner(@RequestHeader(name = "auth-token") String authToken) {
        return ResponseEntity.ok(userService.getCurrentUser(userProvider.trueUser(authToken,
                "Start User controller", "Del owner error")));
    }
}
