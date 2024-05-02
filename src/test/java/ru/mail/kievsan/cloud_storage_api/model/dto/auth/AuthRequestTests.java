package ru.mail.kievsan.cloud_storage_api.model.dto.auth;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthRequestTests {

    private static long suiteStartTime;

    private final String login = "login";
    private final String password = "password";

    private AuthRequest request, request2;

    @BeforeAll
    public static void testSuiteInit() {
        System.out.println("----------- Running dto AuthRequest testing...");
        suiteStartTime = System.currentTimeMillis(); // .nanoTime()
    }

    @AfterAll
    public static void testSuiteComplete() {
        System.out.printf("AuthRequest tests complete: %s ms.\n\n", (System.currentTimeMillis() - suiteStartTime));
    }

    @BeforeEach
    public void runTest() {
        System.out.println("Starting new test " + this);
        request = new AuthRequest(this.login, this.password);
    }

    @AfterEach
    public void finishTest() {
        request = null;
        request2 = null;
    }

    @Test
    @DisplayName("dtoAuthRequestClassTest")
    public void requestClassTest() {
        assertEquals(AuthRequest.class, request.getClass());
        assertEquals(String.class, request.getLogin().getClass());
        assertEquals(String.class, request.getPassword().getClass());
    }

    @Test
    @DisplayName("dtoAuthRequestObjTest")
    public void requestObjTest() {
        assertEquals(login, request.getLogin());
        assertEquals(password, request.getPassword());
    }

    @Test
    @DisplayName("dtoAuthRequestObjEqualTest")
    public void requestObjectsEqualTest() {
        request2 = new AuthRequest(login, password);
        assertEquals(request2.equals(request),
                request2.getLogin().equals(request.getLogin())
                        && request2.getPassword().equals(request.getPassword())
        );
    }
}
