package ru.mail.kievsan.cloud_storage_api.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.mail.kievsan.cloud_storage_api.model.Role;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;

import static org.junit.jupiter.api.Assertions.*;

//@DataJpaTest(properties = {
//       "spring.datasource.url=jdbc:h2:mem:testdb",
//        "jdbc:postgresql://localhost:5438/file_storage",
//        "spring.jpa.hibernate.ddl-auto=create-drop"}
//)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor
public class UserJPARepoTests {

    private static long suiteStartTime;

    private User testUser;
    private final String email = "testuser@mail.ru";

    @Autowired
    private UserJPARepo userRepo;

    @BeforeAll
    public static void testSuiteInit() {
        System.out.println("----------- Running User entity repository tests...");
        suiteStartTime = System.currentTimeMillis();
    }

    @AfterAll
    public static void testSuiteComplete() {
        System.out.printf("User entity repository tests complete: %s ms.\n\n", (System.currentTimeMillis() - suiteStartTime));
    }

    @BeforeEach
    public void runTest() {
        System.out.println("Starting new test " + this);
        testUser = User.builder()
                .nickname("testuser")
                .email(email)
                .password("password")
                .role(Role.USER)
                .enabled(true)
                .build();
        userRepo.save(testUser);
    }

    @AfterEach
    public void finishTest() {
        userRepo.delete(testUser);
        testUser = null;
    }

    @Test
    void user_whenSaved_thenCanBeFoundById() {
        User savedUser = userRepo.findById(testUser.getId()).orElse(null);

        assertNotNull(savedUser);
        assertEquals(testUser.getNickname(), savedUser.getNickname());
        assertEquals(testUser.getUsername(), savedUser.getUsername());
        assertEquals(testUser.getPassword(), savedUser.getPassword());
        assertEquals(testUser.getRole(), savedUser.getRole());
        assertEquals(testUser.isEnabled(), savedUser.isEnabled());
        assertIterableEquals(testUser.getUserFiles(), savedUser.getUserFiles());
    }

    @Test
    void user_whenUpdated_thenCanBeFoundByIdWithUpdatedData() {
        String newUsername = "newUsername@mail.ru";
        testUser.setUsername(newUsername);
        userRepo.save(testUser);

        User updatedUser = userRepo.findById(testUser.getId()).orElse(null);

        assertNotNull(updatedUser);
        assertEquals(newUsername, updatedUser.getUsername());
    }

    @Test
    void user_whenFindByEmailCalled_thenUserIsFound() {
        User foundUser = userRepo.findByEmail(email).orElse(null);

        assertNotNull(foundUser);
        assertEquals(email, foundUser.getUsername());
    }

    @Test
    void updateUserByEmailAndPassword() {
        String newEmail = "newUsername@mail.ru";
        String newPassword = "newPassword";

        userRepo.updateUserByEmailAndPassword(newEmail, newPassword, testUser);
        User updatedUser = userRepo.findByEmail(newEmail).orElse(null);

        assertNotNull(updatedUser);
        assertEquals(testUser, updatedUser);
    }

    @Test
    void updateUserByRole() {
        userRepo.updateUserByRole(Role.ADMIN, testUser);
        User updatedUser = userRepo.findById(testUser.getId()).orElse(null);

        assertNotNull(updatedUser);
        assertEquals(testUser, updatedUser);
    }

}
