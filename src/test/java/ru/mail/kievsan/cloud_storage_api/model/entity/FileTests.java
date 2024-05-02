package ru.mail.kievsan.cloud_storage_api.model.entity;

import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

public class FileTests {

    private static long suiteStartTime;

    private final Long id = 0L;
    private final String filename = "filename";
    private final LocalDateTime date = LocalDateTime.now();
    private final Long size = 0L;
    private final byte[] content = HexFormat.of().parseHex("e04fd020ea3a6910a2d808002b30309d");
    private final User user = new User();

    private File file, file2;

    @BeforeAll
    public static void testSuiteInit() {
        System.out.println("Running entity File testing...");
        suiteStartTime = System.currentTimeMillis(); // .nanoTime()
    }

    @AfterAll
    public static void testSuiteComplete() {
        System.out.printf("File entity tests complete: %s ms.\n\n", (System.currentTimeMillis() - suiteStartTime));
    }

    @BeforeEach
    public void runTest() {
        System.out.println("Starting new test " + this);
        file = new File(this.id, this.filename, this.date, this.size, this.content, this.user);
    }

    @AfterEach
    public void finishTest() {
        file = null;
        file2 = null;
    }

    @Test
    @DisplayName("entityFileClassTest")
    public void entityClassTest() {
        assertEquals(File.class, file.getClass());
        assertEquals(Long.class, file.getId().getClass());
        assertEquals(String.class, file.getFilename().getClass());
        assertEquals(LocalDateTime.class, file.getDate().getClass());
        assertEquals(Long.class, file.getSize().getClass());
        assertArrayEquals(content, file.getContent());
        assertEquals(User.class, file.getUser().getClass());
    }

    @Test
    @DisplayName("entityFileTest")
    public void entityTest() {
        assertEquals(id, file.getId());
        assertEquals(filename, file.getFilename());
        assertEquals(date, file.getDate());
        assertEquals(size, file.getSize());
        assertArrayEquals(content, file.getContent());
        assertEquals(user, file.getUser());
    }

    @Test
    @DisplayName("entityFileEqualTest")
    public void entityEqualTest() {
        file2 = new File(id, filename, date, size, content, user);
        assertEquals(file2.equals(file),
                file2.getId().equals(file.getId())
                        && file2.getFilename().equals(file.getFilename())
                        && file2.getDate().equals(file.getDate())
                        && file2.getSize().equals(file.getSize())
                        && Arrays.equals(file2.getContent(), file.getContent())
                        && file2.getUser().equals(file.getUser())
        );
    }
}
