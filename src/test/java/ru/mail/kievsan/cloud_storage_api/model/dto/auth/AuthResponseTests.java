package ru.mail.kievsan.cloud_storage_api.model.dto.auth;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthResponseTests {

    private static long suiteStartTime;

    private final String authToken = "example";

    private AuthResponse response, response2;

    @BeforeAll
    public static void testSuiteInit() {
        System.out.println("Running dto AuthResponse testing...");
        suiteStartTime = System.currentTimeMillis(); // .nanoTime()
    }

    @AfterAll
    public static void testSuiteComplete() {
        System.out.printf("AuthResponse tests complete: %s ms.\n\n", (System.currentTimeMillis() - suiteStartTime));
    }

    @BeforeEach
    public void runTest() {
        System.out.println("Starting new test " + this);
        response = new AuthResponse(this.authToken);
    }

    @AfterEach
    public void finishTest() {
        response = null;
        response2 = null;
    }

    @Test
    @DisplayName("dtoErrResponseClassTest")
    public void responseClassTest() {
        assertEquals(AuthResponse.class, response.getClass());
        assertEquals(String.class, response.getAuthToken().getClass());
    }

    @Test
    @DisplayName("dtoErrResponseObjTest")
    public void responseObjTest() {
        assertEquals(authToken, response.getAuthToken());
    }

    @Test
    @DisplayName("dtoErrResponseObjectsEqualTest")
    public void responseObjectsEqualTest() {
        response2 = new AuthResponse(authToken);
        assertEquals(response2.equals(response), response2.getAuthToken().equals(response.getAuthToken()));
    }
}
