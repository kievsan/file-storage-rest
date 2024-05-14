package ru.mail.kievsan.cloud_storage_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.kievsan.cloud_storage_api.exception.NoRightsException;
import ru.mail.kievsan.cloud_storage_api.exception.UserNotFoundException;
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

    public SignUpResponse getUserById(Long id, User currentUser) throws UserNotFoundException {
        User user = userRepo.findById(id).orElseThrow(UserNotFoundException::new);
        log.info("Success: got user {} ({}) by id={}. Current user {} ({}), {}", user.getUsername(), user.getNickname(), id,
                currentUser.getUsername(), currentUser.getNickname(), currentUser.getAuthorities());
        return new SignUpResponse(user.getId(), user.getNickname(), user.getEmail(), user.getRole());
    }

    public SignUpResponse getCurrentUser(User user) throws UserNotFoundException {
        log.info("Success: got owner. User {} ({})", user.getUsername(), user.getNickname());
        return new SignUpResponse(user.getId(), user.getNickname(), user.getEmail(), user.getRole());
    }

    @Transactional
    public void delUserById(Long id, User currentUser) throws NoRightsException, UserNotFoundException {
        if (!currentUser.getAuthorities().contains(Role.ADMIN)) {
            throw new NoRightsException("You do not have enough rights to delete a user if you are not an Admin",
                    null, "USER", "'/user'", "'delete user service'");
        }
        var user = userRepo.findById(id).orElseThrow(UserNotFoundException::new);
        userRepo.deleteById(id);
        log.info("Success: deleted user '{}' by id={}. Current user {} ({}), {}", user.getNickname(), id,
                currentUser.getUsername(), currentUser.getNickname(), currentUser.getAuthorities());
    }

    @Transactional
    public void delCurrentUser(User user) throws UserNotFoundException {
        userRepo.delete(user);
        log.info("Success: deleted owner, user {} ({})", user.getUsername(), user.getNickname());
    }
}
