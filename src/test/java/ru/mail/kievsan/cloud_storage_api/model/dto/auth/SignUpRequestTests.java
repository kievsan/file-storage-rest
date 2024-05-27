package ru.mail.kievsan.cloud_storage_api.model.dto.auth;

import org.junit.jupiter.api.*;
import ru.mail.kievsan.cloud_storage_api.model.Role;
import ru.mail.kievsan.cloud_storage_api.model.dto.user.SignUpRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SignUpRequestTests {

    private static long suiteStartTime;

    private final String nickname = "nickname";
    private final String email = "email";
    private final String password = "password";
    private final Role role = Role.USER;

    private SignUpRequest request, request2;

    @BeforeAll
    public static void testSuiteInit() {
        System.out.println("----------- Running dto SignUpRequest testing...");
        suiteStartTime = System.currentTimeMillis(); // .nanoTime()
    }

    @AfterAll
    public static void testSuiteComplete() {
        System.out.printf("SignUpRequest tests complete: %s ms.\n\n", (System.currentTimeMillis() - suiteStartTime));
    }

    @BeforeEach
    public void runTest() {
        System.out.println("Starting new test " + this);
        request = new SignUpRequest(this.nickname, this.email, this.password, this.role);
    }

    @AfterEach
    public void finishTest() {
        request = null;
        request2 = null;
    }

    @Test
    @DisplayName("dtoSignUpRequestClassTest")
    public void requestClassTest() {
        assertEquals(SignUpRequest.class, request.getClass());
        assertEquals(String.class, request.getNickname().getClass());
        assertEquals(String.class, request.getEmail().getClass());
        assertEquals(String.class, request.getPassword().getClass());
        assertEquals(Role.class, request.getRole().getClass());
    }

    @Test
    @DisplayName("dtoSignUpRequestObjTest")
    public void requestObjTest() {
        assertEquals(nickname, request.getNickname());
        assertEquals(email, request.getEmail());
        assertEquals(password, request.getPassword());
        assertEquals(role, request.getRole());
    }

    @Test
    @DisplayName("dtoSignUpRequestObjEqualTest")
    public void requestObjectsEqualTest() {
        request2 = new SignUpRequest(nickname, email, password, role);
        assertEquals(request2.equals(request),
                request2.getNickname().equals(request.getNickname())
                        && request2.getEmail().equals(request.getEmail())
                        && request2.getPassword().equals(request.getPassword())
                        && request2.getRole().equals(request.getRole())
        );
    }
}
