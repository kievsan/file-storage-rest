package ru.mail.kievsan.cloud_storage_api.service;

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
import ru.mail.kievsan.cloud_storage_api.model.dto.user.UpdateRequest;
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

    public SignUpResponse register(SignUpRequest req, User owner) throws UserRegistrationException {
        Predicate<User> USER_IS_ADMIN = user-> user != null && user.isAccountNonLocked() && user.getRole() == Role.ADMIN;
        Predicate<User> USER_IS_SUPER_ADMIN = user-> USER_IS_ADMIN.test(user) && "starter".equals(user.getNickname());

        var newUser = User.builder()
                .nickname(req.getNickname())
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .role(USER_IS_SUPER_ADMIN.test(owner) ? req.getRole() : Role.USER)
                .enabled(true)
                .build();
        if (userRepo.existsByEmail(newUser.getUsername())) {
            throw new UserRegistrationException("The username is already in use, registration is not possible!",
                    null, null, null, "'register service'");
        }
        signup(newUser);
        return new SignUpResponse(newUser);
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

    @Transactional
    public SignUpResponse updateUser(UpdateRequest req, User user) throws UserRegistrationException {
        String newEmail = req.getEmail() == null || req.getEmail().isBlank() ? user.getEmail() : req.getEmail().trim();
        String newPassword = req.getPassword() == null || req.getPassword().isEmpty() ? user.getPassword() : encoder.encode(req.getPassword());

        if (!newEmail.equals(user.getEmail()) && userRepo.existsByEmail(newEmail)) {
            String errMsg = String.format("User with email '%s' already exists, the updating is not possible!", newEmail);
            log.error("{}  {} ('{}'): {}", logErrTitle, user.getEmail(), user.getNickname(), errMsg);
            throw new UserRegistrationException(errMsg, null, null, null, "'updateUser service'");
        }

        userRepo.updateUserByEmailAndPassword(newEmail, newPassword, user);

        log.info("SUCCESS update user '{}'  ->  email: '{}', password: {}", user.getNickname(), newEmail, newPassword);
        user.setEmail(newEmail);
        return new SignUpResponse(user);
    }

    public SignUpResponse getUserById(Long id, User currentUser) throws NoRightsException, UserNotFoundException {
        var exception = getNoRightsException(currentUser.getNickname(), Role.ADMIN, "to get a user", "getUserById");

        if (!currentUser.getAuthorities().contains(new SimpleGrantedAuthority(Role.ADMIN.name()))) {
            throw exception;
        }

        User user = userRepo.findById(id).orElseThrow(UserNotFoundException::new);

        if (user.getNickname().equalsIgnoreCase("starter")) {
            throw new UserRegistrationException("Can't get the 'starter' user data");
        }

        if (user.getAuthorities().contains(new SimpleGrantedAuthority(Role.ADMIN.name()))
                && !currentUser.getNickname().equals("starter")) {
            throw exception;
        }

        log.info("Success: got user {} ({}) by id={}. Current user {} ({}), {}", user.getUsername(), user.getNickname(), id,
                currentUser.getUsername(), currentUser.getNickname(), currentUser.getAuthorities());
        return new SignUpResponse(user);
    }

    public SignUpResponse getCurrentUser(User user) throws UserNotFoundException {
        log.info("Success: got owner. User {} ({})", user.getUsername(), user.getNickname());
        return new SignUpResponse(user);
    }

    @Transactional
    public void delUserById(Long id, User currentUser) throws NoRightsException, UserNotFoundException, UserRegistrationException {
        var exception = getNoRightsException(currentUser.getNickname(), Role.ADMIN, "to delete a user", "delUserById");

        if (!currentUser.getAuthorities().contains(new SimpleGrantedAuthority(Role.ADMIN.name()))) {
            throw exception;
        }

        var user = userRepo.findById(id).orElseThrow(UserNotFoundException::new);

        if (user.getNickname().equalsIgnoreCase("starter")) {
            throw new UserRegistrationException("Can't del the 'starter' user");
        }

        if (user.getAuthorities().contains(new SimpleGrantedAuthority(Role.ADMIN.name()))
                && !currentUser.getNickname().equals("starter")) {
            throw exception;
        }

        userRepo.deleteById(id);

        log.info("Success: deleted user '{}' by id={}. Current user {} ('{}'), {}", user.getNickname(), id,
                currentUser.getUsername(), currentUser.getNickname(), currentUser.getAuthorities());
    }

    @Transactional
    public void delCurrentUser(User user) throws UserRegistrationException {
        var exception = getNoRightsException(user.getNickname(), Role.ADMIN, "to delete an ADMIN", "delCurrentUser");

        if (user.getAuthorities().contains(new SimpleGrantedAuthority(Role.ADMIN.name()))) {
            log.warn("Has no rights: deleting owner - ADMIN ('{}'), {}. Can't del an ADMIN !", user.getNickname(), user.getUsername()); //
            throw exception;
        }
//        if (user.getRole().equals(Role.ADMIN)) {
//            throw new UserRegistrationException("Can't del an ADMIN");
//        }

        userRepo.delete(user);
        log.info("Success: deleted owner, user {} ('{}')", user.getUsername(), user.getNickname());
    }

    public NoRightsException getNoRightsException(String username, Role role, String action, String service) {
        String errMsg = "%s:  You do not have enough rights %s if you has no '%s', for example...".formatted(username, action, role); // "to delete a user"
        return new NoRightsException(errMsg, null, "USER", "'/user'", "'" + service + "' service"); // "delUserById"
    }
}
