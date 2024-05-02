package ru.mail.kievsan.cloud_storage_api.model.dto.auth;

import org.junit.jupiter.api.*;
import ru.mail.kievsan.cloud_storage_api.model.Role;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SignUpResponseTests {

    private static long suiteStartTime;

    private final Long id = 0L;
    private final String nickname = "nickname";
    private final String email = "email@email.com";
    private final Role role = Role.USER;

    private SignUpResponse response, response2;

    @BeforeAll
    public static void testSuiteInit() {
        System.out.println("----------- Running dto SignUpResponse testing...");
        suiteStartTime = System.currentTimeMillis(); // .nanoTime()
    }

    @AfterAll
    public static void testSuiteComplete() {
        System.out.printf("SignUpResponse tests complete: %s ms.\n\n", (System.currentTimeMillis() - suiteStartTime));
    }

    @BeforeEach
    public void runTest() {
        System.out.println("Starting new test " + this);
        response = new SignUpResponse(this.id, this.nickname, this.email, this.role);
    }

    @AfterEach
    public void finishTest() {
        response = null;
        response2 = null;
    }

    @Test
    @DisplayName("dtoSignUpResponseClassTest")
    public void responseClassTest() {
        assertEquals(SignUpResponse.class, response.getClass());
        assertEquals(Long.class, response.getId().getClass());
        assertEquals(String.class, response.getNickname().getClass());
        assertEquals(String.class, response.getEmail().getClass());
        assertEquals(Role.class, response.getRole().getClass());
    }

    @Test
    @DisplayName("dtoSignUpResponseObjTest")
    public void responseObjTest() {
        assertEquals(id, response.getId());
        assertEquals(nickname, response.getNickname());
        assertEquals(email, response.getEmail());
        assertEquals(role, response.getRole());
    }

    @Test
    @DisplayName("dtoSignUpResponseObjEqualTest")
    public void responseObjectsEqualTest() {
        response2 = new SignUpResponse(id, nickname, email, role);
        assertEquals(response2.equals(response),
                response2.getId().equals(response.getId())
                        && response2.getNickname().equals(nickname)
                        && response2.getEmail().equals(email)
                        && response2.getRole().equals(role)
        );
    }
}
