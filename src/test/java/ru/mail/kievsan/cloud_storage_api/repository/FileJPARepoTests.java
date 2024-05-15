package ru.mail.kievsan.cloud_storage_api.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.mail.kievsan.cloud_storage_api.model.Role;
import ru.mail.kievsan.cloud_storage_api.model.entity.File;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;

import java.time.LocalDateTime;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor
public class FileJPARepoTests {

    private static long suiteStartTime;

    @Autowired
    private FileJPARepo fileRepo;
    @Autowired
    private UserJPARepo userRepo;

    private File testFile;
    private User testUser;

    private final byte[] content = HexFormat.of().parseHex("e04fd020ea3a6910a2d808002b30309d");
    private final Long size = (long) content.length;

    @BeforeAll
    public static void testSuiteInit() {
        System.out.println("----------- Running File entity repository tests...");
        suiteStartTime = System.currentTimeMillis();
    }

    @AfterAll
    public static void testSuiteComplete() {
        System.out.printf("File entity repository tests complete: %s ms.\n\n", (System.currentTimeMillis() - suiteStartTime));
    }

    @BeforeEach
    public void runTest() {
        System.out.println("Starting new test " + this);
        testUser = User.builder()
                .nickname("testuser")
                .email("testuser@mail.ru")
                .password("password")
                .role(Role.USER)
                .enabled(true)
                .build();
        userRepo.save(testUser);

        testFile = File.builder()
                .filename("testfile")
                .date(LocalDateTime.now())
                .size(size)
                .content(content)
                .user(testUser)
                .build();
        fileRepo.save(testFile);
    }

    @AfterEach
    public void finishTest() {
        fileRepo.delete(testFile);
        testFile = null;
        testUser = null;
    }

    @Test
    void file_whenSaved_thenCanBeFoundById() {
        File savedFile = fileRepo.findById(testFile.getId()).orElse(null);

        assertNotNull(savedFile);
        assertEquals(testFile.getFilename(), savedFile.getFilename());
        assertEquals(testFile.getDate(), savedFile.getDate());
        assertEquals(testFile.getSize(), savedFile.getSize());
        assertEquals(testFile.getContent(), savedFile.getContent());
        assertEquals(testFile.getUser(), savedFile.getUser());
    }

    @Test
    void file_whenUpdated_thenCanBeFoundByIdWithUpdatedData() {
        testFile.setFilename("newfilename");
        fileRepo.save(testFile);

        File updatedFile = fileRepo.findById(testFile.getId()).orElse(null);

        assertNotNull(updatedFile);
        assertEquals("newfilename", updatedFile.getFilename());
    }

    @Test
    void file_whenFindByUserAndFilenameCalled_thenFileIsFound() {
        File foundFile = fileRepo.findByUserAndFilename(testUser, "testfile");

        assertNotNull(foundFile);
        assertEquals("testfile", foundFile.getFilename());
    }
}
