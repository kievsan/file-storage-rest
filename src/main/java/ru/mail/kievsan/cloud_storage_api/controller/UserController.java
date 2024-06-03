package ru.mail.kievsan.cloud_storage_api.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.kievsan.cloud_storage_api.exception.UnauthorizedUserException;
import ru.mail.kievsan.cloud_storage_api.model.dto.user.UpdateRequest;
import ru.mail.kievsan.cloud_storage_api.model.dto.user.SignUpRequest;
import ru.mail.kievsan.cloud_storage_api.model.dto.user.SignUpResponse;
import ru.mail.kievsan.cloud_storage_api.security.JwtUserDetails;
import ru.mail.kievsan.cloud_storage_api.security.ISecuritySettings;
import ru.mail.kievsan.cloud_storage_api.service.UserService;
import ru.mail.kievsan.cloud_storage_api.util.UserProvider;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(ISecuritySettings.USER_URI)
public class UserController {

    static final String logTitle = "Start User controller";

    private final UserService service;
    private final UserProvider userProvider;
    private final JwtUserDetails userDetails;

    @PermitAll
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    public ResponseEntity<SignUpResponse> register(@RequestBody SignUpRequest request,
                                                   HttpServletRequest httpRequest) {
        try {
            var token = httpRequest.getHeader("auth-token");
            return ResponseEntity.ok(service.register(request,
                    userProvider.trueUser(token, logTitle, "User registration problem", log::warn)));
        } catch (UnauthorizedUserException ex) {
            return ResponseEntity.ok(service.register(request, null));
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public ResponseEntity<SignUpResponse> updateUser(@RequestHeader("auth-token") String authToken,
                                                     @RequestBody UpdateRequest request) {
        return ResponseEntity.ok(service.updateUser(request,
                userProvider.trueUser(authToken,
                        "%s, update %s".formatted(logTitle, userDetails.presentAuthenticated()),
                        "Update user error", log::error)));
    }

//    @RolesAllowed({"ROLE_ADMIN"})  // блокирует любых пользователей ??????????!!! ПОЧЕМУ с Role.ADMIN тоже блокирует?
//    @Secured({"ROLE_ADMIN"})      // блокирует любых пользователей ??????????!!!
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ResponseEntity<SignUpResponse> getUser(@PathVariable @Positive Long id,
                                                  @RequestHeader(name = "auth-token") String authToken) {
        return ResponseEntity.ok(service.getUserById(id,
                userProvider.trueUser(authToken,logTitle, "Get User error", log::error)));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ResponseEntity<SignUpResponse> getOwner(@RequestHeader(name = "auth-token") String authToken) {
        return ResponseEntity.ok(service.getCurrentUser(
                userProvider.trueUser(authToken, logTitle, "Get owner error", log::error)));
    }

//    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")  // блокирует любых пользователей ??????????!!!
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delUser(@PathVariable @Positive Long id, @RequestHeader(name = "auth-token") String authToken) {
        service.delUserById(id, userProvider.trueUser(authToken,logTitle, "Del User error", log::error));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    public ResponseEntity<?> delOwner(@RequestHeader(name = "auth-token") String authToken) {
        service.delCurrentUser(userProvider.trueUser(authToken, logTitle, "Del owner error", log::error));
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
