package ru.mail.kievsan.cloud_storage_api.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import ru.mail.kievsan.cloud_storage_api.exception.InputDataException;
import ru.mail.kievsan.cloud_storage_api.exception.InternalServerException;
import ru.mail.kievsan.cloud_storage_api.model.Role;
import ru.mail.kievsan.cloud_storage_api.model.entity.File;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.repository.FileJPARepo;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FileStorageServiceUnitTests {

    private static final Logger log = LoggerFactory.getLogger(FileStorageServiceUnitTests.class);
    private static long suiteStartTime;

    private static User testUser;
    private static File testFile;

    @Mock
    FileJPARepo fileRepo;
    @Mock
    MockMultipartFile mockFile;

    @InjectMocks
    private FileStorageService service;

    @BeforeAll
    public static void testSuiteInit() {
        System.out.println("----------- Run File Storage service unit tests... at " + LocalDateTime.now());
        suiteStartTime = System.currentTimeMillis();
        testFile = newFile(1);
    }

    @AfterAll
    public static void testSuiteComplete() {
        System.out.printf("\nFile Storage service unit tests complete: %s ms.\n\n", (System.currentTimeMillis() - suiteStartTime));
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

    @Test
    @DisplayName("Checks if the file was uploaded successfully:" +
            "   uploadFileOkTest() ")
    public void uploadFileOkTest() {
        System.out.println("  Successful file upload: ");
        logCapture();
        Mockito.when(fileRepo.save(Mockito.any(File.class))).thenReturn(testFile);

        assertDoesNotThrow(() -> service.uploadFile("testfile", mockFile, testUser));
    }

    @ParameterizedTest(name = "{index} - {argumentsWithNames}")
    @ValueSource(classes = {InputDataException.class})
    @DisplayName("Checks that the file was not uploaded due to an error:" +
            "   uploadFileErrTest() ")
    public void uploadFileErrTest(Class<Exception> testException) {
        System.out.println("  Failed file upload:");
        logCapture("  Got exception %s \n".formatted(testException.getSimpleName()));
        Mockito.when(fileRepo.save(Mockito.any(File.class))).thenThrow(testException);

        assertThrows(testException, () -> service.uploadFile("testfile", mockFile, testUser));
    }

    @ParameterizedTest(name = "{index} - {argumentsWithNames}")
    @ValueSource(classes = {NullPointerException.class})
    @DisplayName("Checks that the file was not uploaded due to an error:" +
            "   getContentFromFileErrNullFileTest() ")
    public void uploadFileErrNullFileTest(Class<Exception> testException) {
        System.out.println("  Failed file upload:");
        logCapture("  Got exception %s \n".formatted(testException.getSimpleName()));

        assertThrows(InputDataException.class, () -> service.uploadFile("testfile", null, testUser));
    }

    @Test
    @DisplayName("Checks if the file was edited successfully:" +
            "   editFileNameOkTest() ")
    public void editFileNameOkTest() {
        System.out.println("  Successful filename edit: ");
        logCapture();
        var newFileName = "new_" + testFile.getFilename();
        Mockito
                .when(fileRepo.findByUserAndFilename(Mockito.any(User.class), Mockito.anyString()))
                .thenReturn(testFile)
                .thenReturn(null);

        assertDoesNotThrow(() -> service.editFileName(testFile.getFilename(), newFileName, testUser));
    }

    @ParameterizedTest(name = "{index} - ''{argumentsWithNames}''")
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("Checks that the file was not updated due to an empty or blank new file name:" +
            "   editFileNameErrNullNewFileNameTest() ")
    public void editFileNameErrNullNewFileNameTest(String newFileName) {
        System.out.println("  Failed edit file name:");
        logCapture("  Got exception %s \n".formatted(InputDataException.class.getSimpleName()));

        assertThrows(InputDataException.class, () -> service.editFileName(testFile.getFilename(), newFileName, testUser));
    }

    @Test
    @DisplayName("Checks that the file was not updated due to a file name that not found:" +
            "   editFileNameErrFileNotFoundTest() ")
    public void editFileNameErrFileNotFoundTest() {
        System.out.println("  Failed edit file name:");
        logCapture("  Got exception %s \n".formatted(InputDataException.class.getSimpleName()));
        Mockito
                .when(fileRepo.findByUserAndFilename(Mockito.any(User.class), Mockito.anyString()))
                .thenReturn(null);

        assertThrows(InputDataException.class,
                () -> service.editFileName("notFoundFileName", "newFileName", testUser));
    }

    @Test
    @DisplayName("Checks that the file was not updated due to an unknown error:" +
            "   editFileNameErrUnknownTest() ")
    public void editFileNameErrUnknownTest() {
        System.out.println("  Failed edit file name:");
        logCapture("  Got exception %s \n".formatted(InternalServerException.class.getSimpleName()));
        Mockito
                .when(fileRepo.findByUserAndFilename(Mockito.any(User.class), Mockito.anyString()))
                .thenReturn(testFile)
                .thenReturn(testFile);

        assertThrows(InternalServerException.class,
                () -> service.editFileName(testFile.getFilename(), "newFileName", testUser));
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

    public void logCapture(String msg) {
        logCapture();
        log.info(msg);
    }
}
