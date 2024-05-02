package ru.mail.kievsan.cloud_storage_api.model.entity;

import org.junit.jupiter.api.*;
import ru.mail.kievsan.cloud_storage_api.model.Role;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class UserTests {

    private static long suiteStartTime;

    private final Long id = 0L;
    private final String nickname = "nickname";
    private final String email = "email";
    private final String password = "password";
    private final Role role = Role.USER;
    private final boolean enabled = true;
    private final List<File> userFiles = List.of(new File());

    private User user, user2;

    @BeforeAll
    public static void testSuiteInit() {
        System.out.println("Running entity User testing...");
        suiteStartTime = System.currentTimeMillis(); // .nanoTime()
    }

    @AfterAll
    public static void testSuiteComplete() {
        System.out.printf("User entity tests complete: %s ms.\n\n", (System.currentTimeMillis() - suiteStartTime));
    }

    @BeforeEach
    public void runTest() {
        System.out.println("Starting new test " + this);
        user = new User(this.id, this.nickname, this.email, this.password, this.role, this.enabled, this.userFiles);
    }

    @AfterEach
    public void finishTest() {
        user = null;
        user2 = null;
    }

    @Test
    @DisplayName("entityUserClassTest")
    public void entityClassTest() {
        assertEquals(User.class, user.getClass());
        assertEquals(Long.class, user.getId().getClass());
        assertEquals(String.class, user.getNickname().getClass());
        assertEquals(String.class, user.getEmail().getClass());
        assertEquals(String.class, user.getPassword().getClass());
        assertEquals(Role.class, user.getRole().getClass());
        assertThat(List.of(true, false), hasItem(user.isEnabled()));
        assertInstanceOf(List.class, user.getUserFiles());
        assertEquals(File.class, user.getUserFiles().getFirst().getClass());
    }

    @Test
    @DisplayName("entityUserTest")
    public void entityTest() {
        assertEquals(id, user.getId());
        assertEquals(role, user.getRole());
        assertEquals(nickname, user.getNickname());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(role, user.getRole());
        assertEquals(enabled, user.isEnabled());
        assertEquals(userFiles, user.getUserFiles());
    }

    @Test
    @DisplayName("entityUserEqualTest")
    public void entityEqualTest() {
        user2 = new User(id, nickname, email, password, role, enabled, userFiles);
        assertEquals(user2.equals(user),
                user2.getId().equals(user.getId())
                        && user2.getNickname().equals(user.getNickname())
                        && user2.getEmail().equals(user.getEmail())
                        && user2.getPassword().equals(user.getPassword())
                        && user2.getRole().equals(user.getRole())
                        && user2.isEnabled() == user.isEnabled()
                        && user2.getUserFiles().equals(user.getUserFiles())
        );
    }
}
