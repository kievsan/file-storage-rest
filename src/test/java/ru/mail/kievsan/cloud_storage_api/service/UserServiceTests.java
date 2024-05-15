package ru.mail.kievsan.cloud_storage_api.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.mail.kievsan.cloud_storage_api.model.Role;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.repository.UserJPARepo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class UserServiceTests {

    private static long suiteStartTime;

    @Autowired
    private UserService userService;
    @Autowired
    private UserJPARepo userRepo;

    private User testUser;

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
        testUser = User.builder()
                .nickname("testuser")
                .email("testuser@mail.ru")
                .password("password")
                .role(Role.USER)
                .enabled(true)
                .build();
    }

    @AfterEach
    public void finishTest() {
        userRepo.delete(testUser);
        testUser = null;
    }

    @Test
    public void testSignUpIntoDB() {
        userService.signup(testUser);
        User newUser = userRepo.findById(testUser.getId()).orElse(null);

        assertNotNull(newUser);
        assertEquals(testUser.getUsername(), newUser.getUsername());
    }
}
