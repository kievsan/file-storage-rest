package ru.mail.kievsan.cloud_storage_api.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mail.kievsan.cloud_storage_api.model.Role;
import ru.mail.kievsan.cloud_storage_api.model.entity.File;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.repository.FileJPARepo;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FileListServiceUnitTests {

    private static final Logger log = LoggerFactory.getLogger(FileListServiceUnitTests.class);
    private static long suiteStartTime;

    @InjectMocks
    private FileListService service;

    @Mock
    FileJPARepo fileRepo;

    User testUser;
    List<File> listResponse;

    @BeforeAll
    public static void testSuiteInit() {
        System.out.println("----------- Running File List service unit tests...");
        suiteStartTime = System.currentTimeMillis();
    }

    @AfterAll
    public static void testSuiteComplete() {
        System.out.printf("\nFile List service unit tests complete: %s ms.\n\n", (System.currentTimeMillis() - suiteStartTime));
    }

    @BeforeEach
    public void runTest() {
        System.out.println("\nStarting new test " + this);
        testUser = newUser();
        listResponse = List.of(newFile(1), newFile(2), newFile(3), newFile(4));
    }

    @AfterEach
    void finishTest() {
        testUser = null;
        listResponse = null;
    }

    @Test
    public void getFileListTest() {
        System.out.println("  File list service");
        logCapture();
        Mockito.when(fileRepo.findAllByUserOrderByFilename(Mockito.any(User.class))).thenReturn(listResponse);

        assertDoesNotThrow(() -> service.getFileList(listResponse.size(), testUser));

        var testResponse = service.getFileList(listResponse.size(), testUser);

        assertNotNull(testResponse);
        assertEquals(listResponse.size(), testResponse.size());
    }

    private File newFile(int number) {
        var numberLong = Long.parseLong(String.valueOf(number));
        return File.builder()
                .id(numberLong)
                .filename("testfile" + number)
                .size(numberLong * 10)
                .date(LocalDateTime.now())
                .content("Hello, %s".formatted(number).getBytes())
                .user(testUser)
                .build();
    }

    private User newUser() {
        return User.builder()
                .id(2L)
                .nickname("testuser")
                .email("testuser@mail.ru")
                .password("password")
                .role(Role.USER)
                .enabled(true)
                .build();
    }

    public void logCapture() {
        log.info("testing user:  '{}', {}, {}", testUser.getNickname(), testUser.getEmail(), testUser.getRole());
    }

    public void logCapture(String msg) {
        logCapture();
        log.info(msg);
    }
}
