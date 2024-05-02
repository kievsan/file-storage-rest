package ru.mail.kievsan.cloud_storage_api.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.mail.kievsan.cloud_storage_api.model.Role;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    @Autowired
    private UserJPARepo userRepo;

    private User testUser;

    private final String email = "testuser@mail.ru";
    private final String newUsername = "newUsername@mail.ru";

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
        assertEquals(testUser.getUsername(), savedUser.getUsername());
        assertEquals(testUser.getPassword(), savedUser.getPassword());
    }

    @Test
    void user_whenUpdated_thenCanBeFoundByIdWithUpdatedData() {
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

}
