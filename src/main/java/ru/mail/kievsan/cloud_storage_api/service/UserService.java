package ru.mail.kievsan.cloud_storage_api.service;

import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.kievsan.cloud_storage_api.exception.*;
import ru.mail.kievsan.cloud_storage_api.model.Role;
import ru.mail.kievsan.cloud_storage_api.model.dto.user.SignUpRequest;
import ru.mail.kievsan.cloud_storage_api.model.dto.user.SignUpResponse;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.repository.UserJPARepo;

import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    static final String logErrTitle = "[USER service error]";

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
                    null, null, null, "'register service'");
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

    public SignUpResponse getUserById(Long id, User currentUser) throws NoRightsException, UserNotFoundException {
        var exception = getNoRightsException(currentUser.getNickname(), Role.ADMIN, "to get a user", "getUserById");

        if (!currentUser.getAuthorities().contains(new SimpleGrantedAuthority(Role.ADMIN.name()))) {
            throw exception;
        }

        User user = userRepo.findById(id).orElseThrow(UserNotFoundException::new);

        if (user.getNickname().equals("starter")) {
            throw new UserRegistrationException("Can't get the 'starter' user data");
        }

        if (user.getAuthorities().contains(new SimpleGrantedAuthority(Role.ADMIN.name()))
                && !currentUser.getNickname().equals("starter")) {
            throw exception;
        }

        log.info("Success: got user {} ({}) by id={}. Current user {} ({}), {}", user.getUsername(), user.getNickname(), id,
                currentUser.getUsername(), currentUser.getNickname(), currentUser.getAuthorities());
        return new SignUpResponse(user.getId(), user.getNickname(), user.getEmail(), user.getRole());
    }

    public SignUpResponse getCurrentUser(User user) throws UserNotFoundException {
        if (user.getNickname().equals("starter")) {
            throw new UserRegistrationException("Can't get the 'starter' user data");
        }
        log.info("Success: got owner. User {} ({})", user.getUsername(), user.getNickname());
        return new SignUpResponse(user.getId(), user.getNickname(), user.getEmail(), user.getRole());
    }

    @Transactional
    public void updateUser(@Email String newEmail, String newPassword, User user) throws UserRegistrationException {
        if (user.getNickname().equals("starter")) {
            throw new UserRegistrationException("Can't update the 'starter' user");
        }

        newEmail = newEmail == null || newEmail.isBlank() ? user.getEmail() : newEmail.trim();
        newPassword = newPassword == null || newPassword.isBlank() ? user.getPassword() : encoder.encode(newPassword);

        if (!newEmail.equals(user.getEmail()) && userRepo.existsByEmail(newEmail)) {
            String errMsg = String.format("User with email '%s' already exists, the update is not possible!", newEmail);
            log.error("{}  {} ('{}'): {}", logErrTitle, user.getEmail(), user.getNickname(), errMsg);
            throw new UserRegistrationException(errMsg, null, null, null, "'updateUser service'");
        }

        userRepo.updateUserByEmailAndPassword(newEmail, newPassword, user);

        log.info("SUCCESS update user '{}'  ->  email: '{}', password: {}", user.getNickname(), user.getEmail(), user.getPassword());
    }

    @Transactional
    public void delUserById(Long id, User currentUser) throws NoRightsException, UserNotFoundException {
        var exception = getNoRightsException(currentUser.getNickname(), Role.ADMIN, "to delete a user", "delUserById");

        if (!currentUser.getAuthorities().contains(new SimpleGrantedAuthority(Role.ADMIN.name()))) {
            throw exception;
        }

        var user = userRepo.findById(id).orElseThrow(UserNotFoundException::new);

        if (user.getNickname().equals("starter")) {
            throw new UserRegistrationException("Can't del the 'starter' user");
        }

        if (user.getAuthorities().contains(new SimpleGrantedAuthority(Role.ADMIN.name()))
                && !currentUser.getNickname().equals("starter")) {
            throw exception;
        }

        userRepo.deleteById(id);

        log.info("Success: deleted user '{}' by id={}. Current user {} ({}), {}", user.getNickname(), id,
                currentUser.getUsername(), currentUser.getNickname(), currentUser.getAuthorities());
    }

    @Transactional
    public void delCurrentUser(User user) {
        if (user.getNickname().equals("starter")) {
            throw new UserRegistrationException("Can't del the 'starter' user");
        }
        userRepo.delete(user);
        log.info("Success: deleted owner, user {} ({})", user.getUsername(), user.getNickname());
    }

    public NoRightsException getNoRightsException(String username, Role role, String action, String service) {
        String errMsg = "%s:  You do not have enough rights %s if you has no '%s', for example...".formatted(username, action, role); // "to delete a user"
        return new NoRightsException(errMsg, null, "USER", "'/user'", "'" + service + "' service"); // "delUserById"
    }
}
