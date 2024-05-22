package ru.mail.kievsan.cloud_storage_api.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.mail.kievsan.cloud_storage_api.exception.UserRegistrationException;
import ru.mail.kievsan.cloud_storage_api.model.Role;
import ru.mail.kievsan.cloud_storage_api.model.dto.user.SignUpResponse;
import ru.mail.kievsan.cloud_storage_api.model.dto.user.UpdateRequest;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.repository.UserJPARepo;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTests {

    private static long suiteStartTime;
    private static User testUser;

    @Autowired
    private UserService userService;
    @Autowired
    private UserJPARepo userRepo;
    @Autowired
    private PasswordEncoder encoder;

    @BeforeAll
    public static void testSuiteInit() {
        System.out.println("----------- Running User service tests...");
        suiteStartTime = System.currentTimeMillis();
    }

    @AfterAll
    public static void testSuiteComplete() {
        System.out.printf("User service tests complete: %s ms.\n\n", (System.currentTimeMillis() - suiteStartTime));
    }

    @BeforeEach
    public void runTest() {
        System.out.println("Starting new test " + this);
        testUser = newUser();
        userService.signup(testUser);
    }

    @AfterEach
    public void finishTest() {
        userRepo.delete(testUser);
        testUser = null;
    }

    @Test
    public void testSignUpIntoDB() {
        User newUser = userRepo.findById(testUser.getId()).orElse(null);

        assertNotNull(newUser);
        assertEquals(testUser.getUsername(), newUser.getUsername());
    }

    @Test
    public void testUpdateUserIntoDB() {
        var newEmail = "new_" + testUser.getEmail();
        var newPassword = "new_password";
        var testRequest = new UpdateRequest(newEmail, newPassword);

        userService.updateUser(testRequest, testUser);
        User updatedUser = userRepo.findByEmail(newEmail).orElse(null);

        assertNotNull(updatedUser);
        assertEquals(testUser.getId(), updatedUser.getId());
        assertTrue(encoder.matches(newPassword, updatedUser.getPassword()));
    }

    @Test
    public void testGetCurrentUser() {
        var user = userService.getCurrentUser(testUser);
        var trueUser = userRepo.findByEmail(testUser.getEmail()).orElse(null);

        assertNotNull(trueUser);
        assertEquals(user, new SignUpResponse(trueUser));
    }

    @Test
    public void testDelCurrentUser() {
        userService.delCurrentUser(testUser);
        var user = userRepo.findById(testUser.getId()).orElse(null);

        assertNull(user);
    }

    @Test
    public void testDelCurrentAdmin() {
        testUser.setRole(Role.ADMIN);
        userRepo.save(testUser);

        assertThrows(UserRegistrationException.class, () -> userService.delCurrentUser(testUser));
        var user = userRepo.findById(testUser.getId()).orElse(null);
        assertNotNull(user);
    }

    private User newUser() {
        return User.builder()
                .nickname("testuser")
                .email("testuser@mail.ru")
                .password(encoder.encode("password"))
                .role(Role.USER)
                .enabled(true)
                .build();
    }
}
