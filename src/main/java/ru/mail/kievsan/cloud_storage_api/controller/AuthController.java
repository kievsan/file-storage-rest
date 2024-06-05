package ru.mail.kievsan.cloud_storage_api.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.kievsan.cloud_storage_api.model.dto.auth.*;
import ru.mail.kievsan.cloud_storage_api.security.JwtUserDetails;
import ru.mail.kievsan.cloud_storage_api.service.AuthService;
import ru.mail.kievsan.cloud_storage_api.util.UserProvider;

import static ru.mail.kievsan.cloud_storage_api.security.ISecuritySettings.*;

@CrossOrigin(methods = {RequestMethod.POST})
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping
public class AuthController {

    private final AuthService service;
    private final JwtUserDetails userDetails;
    private final UserProvider provider;

    @PostMapping(LOGIN_URI)
    @PermitAll
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        log.info(" Start Auth controller:  login  {}", userDetails.presentAuthenticated());
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping (LOGOUT_URI)
    @PermitAll
    public ResponseEntity<String> logout(@RequestHeader("auth-token") String authToken,
                                         HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(service.logout(request, response,
                provider.trueUser(authToken,"Start Auth controller " + request.getRequestURI(), "Logout error", log::error)));
    }

}
