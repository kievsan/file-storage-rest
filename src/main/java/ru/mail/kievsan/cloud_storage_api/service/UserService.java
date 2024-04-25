package ru.mail.kievsan.cloud_storage_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.kievsan.cloud_storage_api.model.Role;
import ru.mail.kievsan.cloud_storage_api.model.dto.auth.*;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.repository.UserJPARepo;
import ru.mail.kievsan.cloud_storage_api.security.JwtProvider;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserJPARepo userRepo;
    private final PasswordEncoder encoder;
    private final JwtProvider provider;

    Function<User, SignUpResponse> OK_RESPONSE = user-> SignUpResponse.builder()
            .id(user.getId())
            .nickname(user.getNickname())
            .role(user.getRole())
            .build();
    Function<String, AuthResponse> BAD_RESPONSE = msg-> AuthErrResponse.builder().message(msg).id(0).build();
//
    @Transactional
    public ResponseEntity<AuthResponse> register(String token, SignUpRequest request) {
        Predicate<User> USER_IS_ADMIN = user-> !(user == null || token.isEmpty())
                && provider.isTokenValid(token, user) && user.isAccountNonLocked()
                && user.getRole() == Role.ADMIN;
        Predicate<User> USER_IS_SUPER_ADMIN = user-> USER_IS_ADMIN.test(user)
                && Objects.equals(user.getNickname(), "starter");

        User owner = token.isEmpty() ? null : userRepo.findByEmail(provider.extractUsername(token)).orElse(null);

        ResponseEntity<AuthResponse> response;
        String msg = String.format("User '%s'", request.getEmail());
        String errMsg = "was not signup";

        var newUser = User.builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .role(USER_IS_SUPER_ADMIN.test(owner) ? request.getRole() : Role.USER)
                .enabled(true)
                .build();
        try {
            userRepo.save(newUser);
            var user = userRepo.findByEmail(newUser.getEmail()).orElseThrow();
            msg += String.format("(%s) signup. Id=%s", user.getNickname(), user.getId());
            response = ResponseEntity.ok(OK_RESPONSE.apply(newUser));
        } catch (RuntimeException ex) {
            msg += String.format(" %s: %s", errMsg, ex.getMessage());
            response = new ResponseEntity<>(BAD_RESPONSE.apply(msg), HttpStatus.BAD_REQUEST);
        }
        log.info(msg);
        return response;
    }
}
