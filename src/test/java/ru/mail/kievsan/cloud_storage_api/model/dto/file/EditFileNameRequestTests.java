package ru.mail.kievsan.cloud_storage_api.model.dto.file;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EditFileNameRequestTests {

    private static long suiteStartTime;

    private final String filename = "example";

    private EditFileNameRequest request, request2;

    @BeforeAll
    public static void testSuiteInit() {
        System.out.println("Running dto EditFileNameRequest testing...");
        suiteStartTime = System.currentTimeMillis(); // .nanoTime()
    }

    @AfterAll
    public static void testSuiteComplete() {
        System.out.printf("EditFileNameRequest tests complete: %s ms.\n\n", (System.currentTimeMillis() - suiteStartTime));
    }

    @BeforeEach
    public void runTest() {
        System.out.println("Starting new test " + this);
        request = new EditFileNameRequest(this.filename);
    }

    @AfterEach
    public void finishTest() {
        request = null;
        request2 = null;
    }

    @Test
    @DisplayName("dtoEditFileNameRequestClassTest")
    public void requestClassTest() {
        assertEquals(EditFileNameRequest.class, request.getClass());
        assertEquals(String.class, request.getFilename().getClass());
    }

    @Test
    @DisplayName("dtoEditFileNameRequestObjTest")
    public void requestObjTest() {
        assertEquals(filename, request.getFilename());
    }

    @Test
    @DisplayName("dtoEditFileNameRequestObjEqualTest")
    public void requestObjectsEqualTest() {
        request2 = new EditFileNameRequest(filename);
        assertEquals(request2.equals(request), request2.getFilename().equals(request.getFilename()));
    }
}
