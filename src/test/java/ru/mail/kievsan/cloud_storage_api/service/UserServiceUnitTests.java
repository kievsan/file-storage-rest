package ru.mail.kievsan.cloud_storage_api.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.mail.kievsan.cloud_storage_api.exception.UserRegistrationException;
import ru.mail.kievsan.cloud_storage_api.model.Role;
import ru.mail.kievsan.cloud_storage_api.model.dto.user.SignUpRequest;
import ru.mail.kievsan.cloud_storage_api.model.dto.user.UpdateRequest;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.repository.UserJPARepo;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTests {

    private static final Logger log = LoggerFactory.getLogger(UserServiceUnitTests.class);
    private static long suiteStartTime;
    private static User testUser;

    @InjectMocks
    private UserService service;

    @Mock
    UserJPARepo repo;
    @Mock
    PasswordEncoder encoder;

//    AutoCloseable openMocks;

    @BeforeAll
    public static void testSuiteInit() {
        System.out.println("----------- Running User service unit tests...");
        suiteStartTime = System.currentTimeMillis();
    }

    @AfterAll
    public static void testSuiteComplete() {
        System.out.printf("\nUser service unit tests complete: %s ms.\n\n", (System.currentTimeMillis() - suiteStartTime));
    }

    @BeforeEach
    public void runTest() {
        System.out.println("\nStarting new test " + this);
        testUser = newUser();
//        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void finishTest() {
        testUser = null;
//        openMocks.close();
    }

    @Test
    public void registerTest() {
        System.out.println("  Signup User");
        var testRequest = new SignUpRequest(testUser);
        logCapture();
        Mockito.when(repo.findByEmail(Mockito.anyString())).thenReturn(Optional.ofNullable(testUser));
        Mockito.when(repo.existsByEmail(Mockito.anyString())).thenReturn(false);
        Mockito.when(encoder.encode(Mockito.anyString())).thenReturn(testUser.getPassword());

        assertDoesNotThrow(() -> service.register(testRequest, null));

        var testResponse = service.register(testRequest, null);

        assertNotEquals(null, testResponse.getId());
        assertEquals(testUser.getId(), testResponse.getId());
        assertEquals(testUser.getNickname(), testResponse.getNickname());
        assertEquals(testUser.getEmail(), testResponse.getEmail());
        assertEquals(testUser.getRole(), testResponse.getRole());
    }

    @Test
    public void registerErrTest() {
        System.out.printf("  Signup User error:  the user with email '%s' already exists...\n", testUser.getEmail());
        var testRequest = new SignUpRequest(testUser);
        logCapture();
        Mockito.when(repo.existsByEmail(Mockito.anyString())).thenReturn(true);

        assertThrows(UserRegistrationException.class, () -> service.register(testRequest, null));
    }

    @Test
    public void updateUserTest() {
        System.out.println("  Update User");
        var testRequest = new UpdateRequest("new_" + testUser.getEmail(), "new_password");
        logCapture();
        Mockito.when(repo.existsByEmail(Mockito.anyString())).thenReturn(false);
        Mockito.when(encoder.encode(Mockito.anyString())).thenReturn(testUser.getPassword());

        assertDoesNotThrow(() -> service.updateUser(testRequest, testUser));
    }

    @Test
    public void updateUserErrTest() {
        var newEmail = "new_" + testUser.getEmail();
        System.out.printf("  Update user error:  the email '%s' is already in use...\n", newEmail);
        var testRequest = new UpdateRequest(newEmail, testUser.getPassword());
        logCapture();
        Mockito.when(repo.existsByEmail(Mockito.anyString())).thenReturn(true);

        assertThrows(UserRegistrationException.class, () -> service.updateUser(testRequest, testUser));
    }



    private User newUser() {
        return User.builder()
                .id(2L)
                .nickname("testuser")
                .email("testuser@mail.ru")
                .password("password")
                .role(Role.USER)
                .enabled(true)
                .build();
    }

    public void logCapture() {
        log.info("testing user:  '{}', {}, {}", testUser.getNickname(), testUser.getEmail(), testUser.getRole());
    }

}
