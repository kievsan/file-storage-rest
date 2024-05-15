package ru.mail.kievsan.cloud_storage_api.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.kievsan.cloud_storage_api.exception.UnauthorizedUserException;
import ru.mail.kievsan.cloud_storage_api.model.dto.user.UpdateRequest;
import ru.mail.kievsan.cloud_storage_api.model.dto.user.SignUpRequest;
import ru.mail.kievsan.cloud_storage_api.model.dto.user.SignUpResponse;
import ru.mail.kievsan.cloud_storage_api.security.JWTUserDetails;
import ru.mail.kievsan.cloud_storage_api.security.SecuritySettings;
import ru.mail.kievsan.cloud_storage_api.service.UserService;
import ru.mail.kievsan.cloud_storage_api.util.UserProvider;

@RestController
@RequiredArgsConstructor
@RequestMapping(SecuritySettings.USER_URI)
public class UserController {

    static final String logTitle = "Start User controller";

    private final UserService service;
    private final UserProvider userProvider;
    private final JWTUserDetails userDetails;

    @PermitAll
    @PostMapping
    public ResponseEntity<SignUpResponse> register(@RequestBody SignUpRequest request,
                                                   HttpServletRequest httpRequest) {
        try {
            var token = httpRequest.getHeader("auth-token");
            return ResponseEntity.ok(service.register(request,
                    userProvider.trueUser(token, logTitle, "User registration WARNING")));
        } catch (UnauthorizedUserException ex) {
            return ResponseEntity.ok(service.register(request, null));
        }
    }

//    @RolesAllowed({"ROLE_ADMIN"})
    @GetMapping("/{id}")
    public ResponseEntity<SignUpResponse> getUser(@PathVariable Long id,
                                                  @RequestHeader(name = "auth-token") String authToken) {
        return ResponseEntity.ok(service.getUserById(id,
                userProvider.trueUser(authToken,logTitle, "Get User error")));
    }

    @GetMapping
    public ResponseEntity<SignUpResponse> getOwner(@RequestHeader(name = "auth-token") String authToken) {
        return ResponseEntity.ok(service.getCurrentUser(
                userProvider.trueUser(authToken, logTitle, "Get owner error")));
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestHeader("auth-token") String authToken,
                                        @RequestBody UpdateRequest request) {
        service.updateUser(request.getEmail(), request.getPassword(), userProvider.trueUser(authToken,
                "%s, update %s".formatted(logTitle, userDetails.presentAuthenticated()), "Update user error"));
        return ResponseEntity.ok(HttpStatus.OK);
    }

//    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delUser(@PathVariable Long id, @RequestHeader(name = "auth-token") String authToken) {
        service.delUserById(id, userProvider.trueUser(authToken,logTitle, "Del User error"));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> delOwner(@RequestHeader(name = "auth-token") String authToken) {
        service.delCurrentUser(userProvider.trueUser(authToken, logTitle, "Del owner error"));
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
