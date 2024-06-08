package ru.mail.kievsan.cloud_storage_api.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FileListServiceUnitTests {

    private static final Logger log = LoggerFactory.getLogger(FileListServiceUnitTests.class);
    private static long suiteStartTime;

    private static User testUser;
    private static List<File> testFileList;

    @Mock
    FileJPARepo fileRepo;

    @InjectMocks
    private FileListService service;

    @BeforeAll
    public static void testSuiteInit() {
        System.out.println("----------- Run File List service unit tests... at " + LocalDateTime.now());
        suiteStartTime = System.currentTimeMillis();
        testFileList = List.of(newFile(1), newFile(2), newFile(3));
    }

    @AfterAll
    public static void testSuiteComplete() {
        System.out.printf("\nFile List service unit tests complete: %s ms.\n\n", (System.currentTimeMillis() - suiteStartTime));
    }

    @BeforeEach
    public void runTest() {
        System.out.println("\nStarting new test " + this);
        testUser = newUser();
    }

    @AfterEach
    void finishTest() {
        testUser = null;
    }

    @ParameterizedTest(name = "{index} - {argumentsWithNames}")
    @MethodSource
    @DisplayName("Test the getting your file list and has no exceptions:" +
            "   getFileListHappyTest() ")
    public void getFileListHappyTest(Integer limit, Integer size) {
        System.out.println("  Get file list happy test: ");
        logCapture();
        Mockito.when(fileRepo.findAllByUserOrderByFilename(Mockito.any(User.class))).thenReturn(testFileList);

        var testResponse = service.getFileList(limit, testUser);

        verify(fileRepo).findAllByUserOrderByFilename(testUser);
        assertNotNull(testResponse);
        assertEquals(testFileList.size(), testResponse.size());
    }

    static Stream<Integer[]> getFileListHappyTest() {
        final int SIZE = testFileList.size();
        return Stream.of(
                new Integer[]{SIZE, SIZE},
                new Integer[]{SIZE + 1, SIZE},
                new Integer[]{0, SIZE},
                new Integer[]{-1, SIZE}
        );
    }

    private static File newFile(int number) {
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

    private static User newUser() {
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
}
