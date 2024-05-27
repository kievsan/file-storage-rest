package ru.mail.kievsan.cloud_storage_api.model.dto.file_list;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileListResponseTests {

    private static long suiteStartTime;

    private final String filename = "example";
    private final Long size = 0L;

    private FileListResponse response, response2;

    @BeforeAll
    public static void testSuiteInit() {
        System.out.println("Running dto FileListResponse testing...");
        suiteStartTime = System.currentTimeMillis(); // .nanoTime()
    }

    @AfterAll
    public static void testSuiteComplete() {
        System.out.printf("FileListResponse tests complete: %s ms.\n\n", (System.currentTimeMillis() - suiteStartTime));
    }

    @BeforeEach
    public void runTest() {
        System.out.println("Starting new test " + this);
        response = new FileListResponse(this.filename, this.size);
    }

    @AfterEach
    public void finishTest() {
        response = null;
        response2 = null;
    }

    @Test
    @DisplayName("dtoErrResponseClassTest")
    public void responseClassTest() {
        assertEquals(FileListResponse.class, response.getClass());
        assertEquals(String.class, response.getFilename().getClass());
        assertEquals(Long.class, response.getSize().getClass());
    }

    @Test
    @DisplayName("dtoErrResponseObjTest")
    public void responseObjTest() {
        assertEquals(filename, response.getFilename());
        assertEquals(size, response.getSize());
    }

    @Test
    @DisplayName("dtoErrResponseObjEqualTest")
    public void responseObjEqualTest() {
        response2 = new FileListResponse(filename, size);

        assertEquals(response2.equals(response),
                response2.getFilename().equals(response.getFilename()) && response2.getSize().equals(response.getSize())
        );
    }
}
