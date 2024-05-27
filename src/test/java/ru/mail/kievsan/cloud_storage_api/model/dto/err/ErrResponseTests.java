package ru.mail.kievsan.cloud_storage_api.model.dto.err;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrResponseTests {

    private static long suiteStartTime;

    private final String message = "example";
    private final Integer id = 0;

    private ErrResponse response, response2;

    @BeforeAll
    public static void testSuiteInit() {
        System.out.println("Running dto ErrResponse testing...");
        suiteStartTime = System.currentTimeMillis(); // .nanoTime()
    }

    @AfterAll
    public static void testSuiteComplete() {
        System.out.printf("ErrResponse tests complete: %s ms.\n\n", (System.currentTimeMillis() - suiteStartTime));
    }

    @BeforeEach
    public void runTest() {
        System.out.println("Starting new test " + this);
        response = new ErrResponse(this.message, this.id);
    }

    @AfterEach
    public void finishTest() {
        response = null;
        response2 = null;
    }

    @Test
    @DisplayName("dtoErrResponseClassTest")
    public void responseClassTest() {
        assertEquals(ErrResponse.class, response.getClass());
        assertEquals(String.class, response.getMessage().getClass());
        assertEquals(Integer.class, response.getId().getClass());
    }

    @Test
    @DisplayName("dtoErrResponseObjTest")
    public void responseObjTest() {
        assertEquals(message, response.getMessage());
        assertEquals(id, response.getId());
    }

    @Test
    @DisplayName("dtoErrResponseObjEqualTest")
    public void responseObjEqualTest() {
        response2 = new ErrResponse(message, id);
        assertEquals(response2.equals(response),
                response2.getMessage().equals(response.getMessage()) && response2.getId().equals(response.getId())
        );
    }
}
