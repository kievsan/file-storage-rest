package ru.mail.kievsan.cloud_storage_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.kievsan.cloud_storage_api.exception.UserRegistrationException;
import ru.mail.kievsan.cloud_storage_api.model.Role;
import ru.mail.kievsan.cloud_storage_api.model.dto.auth.*;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.repository.UserJPARepo;

import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserJPARepo userRepo;
    private final PasswordEncoder encoder;
    //
    public SignUpResponse register(SignUpRequest request, User owner) throws UserRegistrationException {
        Predicate<User> USER_IS_ADMIN = user-> user != null && user.isAccountNonLocked() && user.getRole() == Role.ADMIN;
        Predicate<User> USER_IS_SUPER_ADMIN = user-> USER_IS_ADMIN.test(user) && "starter".equals(user.getNickname());

        var newUser = User.builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .role(USER_IS_SUPER_ADMIN.test(owner) ? request.getRole() : Role.USER)
                .enabled(true)
                .build();
        if (userRepo.existsByEmail(newUser.getUsername())) {
            throw new UserRegistrationException("The username is already in use, registration is not possible!",
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
        signup(newUser);
        return new SignUpResponse(newUser.getId(), newUser.getNickname(), newUser.getEmail(), newUser.getRole());
    }
    //
    public void signup(User newUser) throws UserRegistrationException {
        String msg = String.format("User %s (%s)", newUser.getUsername(), newUser.getNickname());
        try {
            userRepo.save(newUser);
            var user = userRepo.findByEmail(newUser.getEmail()).orElseThrow();
            msg += " signup: Id=" + user.getId();
        } catch (RuntimeException ex) {
            msg += " was not signup: %s" + ex.getMessage();
            throw new UserRegistrationException(msg);
        }
        log.info("SUCCESS! {}", msg);
    }
}
