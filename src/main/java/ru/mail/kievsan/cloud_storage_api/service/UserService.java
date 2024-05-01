package ru.mail.kievsan.cloud_storage_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.kievsan.cloud_storage_api.exception.UserRegisterUserInUseException;
import ru.mail.kievsan.cloud_storage_api.exception.UserSignupIncompleteTransactionException;
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
    public SignUpResponse register(SignUpRequest request, User owner) throws UserRegisterUserInUseException {
        Predicate<User> USER_IS_ADMIN = user-> user != null && user.isAccountNonLocked() && user.getRole() == Role.ADMIN;
        Predicate<User> USER_IS_SUPER_ADMIN = user-> USER_IS_ADMIN.test(user) && "starter".equals(user.getNickname());

        String msg = String.format("User '%s'", request.getEmail());

        var newUser = User.builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .role(USER_IS_SUPER_ADMIN.test(owner) ? request.getRole() : Role.USER)
                .enabled(true)
                .build();
        if (userRepo.existsByEmail(newUser.getUsername())) {
            throw new UserRegisterUserInUseException("Username is already in use");
        }
        signup(newUser, msg);
        return new SignUpResponse(newUser.getId(), newUser.getNickname(), newUser.getEmail(), newUser.getRole());
    }
//
    public void signup(User newUser, String msg) throws UserSignupIncompleteTransactionException {
        try {
            userRepo.save(newUser);
            var user = userRepo.findByEmail(newUser.getEmail()).orElseThrow();
            msg += String.format("(%s) signup: Id=%s", user.getNickname(), user.getId());
        } catch (RuntimeException ex) {
            msg += String.format(" was not signup: %s", ex.getMessage());
            log.info(msg);
            throw new UserSignupIncompleteTransactionException(msg);
        }
        log.info(msg);
    }
}
