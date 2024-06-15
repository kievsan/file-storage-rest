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
import ru.mail.kievsan.cloud_storage_api.exception.AdviceException;
import ru.mail.kievsan.cloud_storage_api.exception.InputDataException;
import ru.mail.kievsan.cloud_storage_api.exception.InternalServerException;
import ru.mail.kievsan.cloud_storage_api.model.Role;
import ru.mail.kievsan.cloud_storage_api.model.entity.File;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.repository.FileJPARepo;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.reset;

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
        reset(fileRepo);
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

        Mockito.when(fileRepo.save(Mockito.any(File.class))).thenReturn(testFile); // операция записи файла без ошибки
        Mockito
                .when(fileRepo.findByUserAndFilename(Mockito.any(User.class), Mockito.anyString()))
                .thenReturn(null)      // до операции записи файла нет другого файла с таким же именем в БД
                .thenReturn(testFile);   // после операции записи файла он найден в БД

        assertDoesNotThrow(() -> service.uploadFile("testfile", mockFile, testUser)); // файл успешно подгружен
    }

    @ParameterizedTest(name = "{index} - {argumentsWithNames}")
    @ValueSource(classes = {InputDataException.class})
    @DisplayName("Checks the file was not uploaded due to an error:" +
            "   uploadFileErrTest() ")
    public void uploadFileErrTest(Class<AdviceException> testException) {
        System.out.println("  Failed file upload:");
        logCapture("  Got exception %s \n".formatted(testException.getSimpleName()));

        Mockito.when(fileRepo.findByUserAndFilename(Mockito.any(User.class), Mockito.anyString())).thenReturn(null);
        Mockito.when(fileRepo.save(Mockito.any(File.class))).thenThrow(testException);

        assertThrows(testException, () -> service.uploadFile("testfile", mockFile, testUser));
    }

    @ParameterizedTest(name = "{index} - {argumentsWithNames}")
    @ValueSource(classes = {NullPointerException.class})
    @DisplayName("Checks the file was not uploaded to the storage due to an error:" +
            "   getContentFromFileErrNullFileTest() ")
    public void uploadFileErrNullFileTest(Class<RuntimeException> testException) {
        System.out.println("  Failed file upload:");
        logCapture("  Got exception %s \n".formatted(testException.getSimpleName()));

        assertThrows(InputDataException.class, () -> service.uploadFile("testfile", null, testUser));
    }

    @ParameterizedTest(name = "{index} - {argumentsWithNames}")
    @ValueSource(classes = {InputDataException.class})
    @DisplayName("Checks the file was not uploaded due to an error:" +
            "   uploadFileErrFileExistsTest() ")
    public void uploadFileErrFileExistsTest(Class<AdviceException> testException) {
        System.out.println("  Failed file upload:");
        logCapture("  Got exception %s \n".formatted(testException.getSimpleName()));

        Mockito.when(fileRepo.findByUserAndFilename(Mockito.any(User.class), Mockito.anyString())).thenReturn(testFile);

        assertThrows(testException, () -> service.uploadFile("testfile", mockFile, testUser));
    }

    @ParameterizedTest(name = "{index} - {argumentsWithNames}")
    @ValueSource(classes = {InternalServerException.class})
    @DisplayName("Checks that the file looks like it was uploaded, but was not found after\n:" +
            "   uploadFileErrServerTest() ")
    public void uploadFileErrServerTest(Class<AdviceException> testException) {
        System.out.println("  Failed file upload:");
        logCapture("  Got exception %s \n".formatted(testException.getSimpleName()));

        Mockito.when(fileRepo.save(Mockito.any(File.class))).thenReturn(testFile);
        Mockito
                .when(fileRepo.findByUserAndFilename(Mockito.any(User.class), Mockito.anyString()))
                .thenReturn(null)
                .thenReturn(null);

        assertThrows(testException, () -> service.uploadFile("testfile", mockFile, testUser));
    }

    @Test
    @DisplayName("Checks if the file was downloaded from the storage successfully:" +
            "   downloadFileOkTest() ")
    public void downloadFileOkTest() {
        System.out.println("  Successful file download: ");
        logCapture();
        Mockito
                .when(fileRepo.findByUserAndFilename(Mockito.any(User.class), Mockito.anyString()))
                .thenReturn(testFile);

        assertDoesNotThrow(() -> service.downloadFile(testFile.getFilename(), testUser));
    }

    @Test
    @DisplayName("Checks the file was not downloaded from the storage because it was not found:" +
            "   downloadFileErrFileNotFoundTest() ")
    public void downloadFileErrFileNotFoundTest() {
        System.out.println("  Failed download file:");
        logCapture("  Got exception %s \n".formatted(InputDataException.class.getSimpleName()));
        Mockito
                .when(fileRepo.findByUserAndFilename(Mockito.any(User.class), Mockito.anyString()))
                .thenReturn(null);

        assertThrows(InputDataException.class, () -> service.downloadFile("notFoundFileName", testUser));
    }

    @Test
    @DisplayName("Checks if the file was deleted successfully:" +
            "   deleteFileOkTest() ")
    public void deleteFileOkTest() {
        System.out.println("  Successful file delete: ");
        logCapture();
        Mockito
                .when(fileRepo.findByUserAndFilename(Mockito.any(User.class), Mockito.anyString()))
                .thenReturn(testFile)
                .thenReturn(null);

        assertDoesNotThrow(() -> service.deleteFile(testFile.getFilename(), testUser));
    }

    @Test
    @DisplayName("Checks the file was not deleted because it was not found:" +
            "   deleteFileErrFileNotFoundTest() ")
    public void deleteFileErrFileNotFoundTest() {
        System.out.println("  Failed delete file:");
        logCapture("  Got exception %s \n".formatted(InputDataException.class.getSimpleName()));
        Mockito
                .when(fileRepo.findByUserAndFilename(Mockito.any(User.class), Mockito.anyString()))
                .thenReturn(null);

        assertThrows(InputDataException.class, () -> service.deleteFile("notFoundFileName", testUser));
    }

    @Test
    @DisplayName("Checks the file was not deleted due to an internal server error:" +
            "   deletedFileErrServerTest() ")
    public void deleteFileErrServerTest() {
        System.out.println("  Failed download file:");
        logCapture("  Got exception %s \n".formatted(InternalServerException.class.getSimpleName()));
        Mockito
                .when(fileRepo.findByUserAndFilename(Mockito.any(User.class), Mockito.anyString()))
                .thenReturn(testFile)
                .thenReturn(testFile);

        assertThrows(InternalServerException.class, () -> service.deleteFile(testFile.getFilename(), testUser));
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
                .thenReturn(null)
                .thenReturn(null);

        assertDoesNotThrow(() -> service.editFileName(testFile.getFilename(), newFileName, testUser));
    }

    @ParameterizedTest(name = "{index} - ''{argumentsWithNames}''")
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("Checks the file was not updated due to an empty or blank new file name:" +
            "   editFileNameErrNullNewFileNameTest() ")
    public void editFileNameErrNullNewFileNameTest(String newFileName) {
        System.out.println("  Failed edit file name:");
        logCapture("  Got exception %s \n".formatted(InputDataException.class.getSimpleName()));

        assertThrows(InputDataException.class, () -> service.editFileName(testFile.getFilename(), newFileName, testUser));
    }

    @Test
    @DisplayName("Checks the file was not updated due to a file name that not found:" +
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
    @DisplayName("Checks the file was not updated due to user file exists with the same file name:" +
            "   editFileNameErrFileExistsTest() ")
    public void editFileNameErrFileExistsTest() {
        System.out.println("  Failed edit file name:");
        logCapture("  Got exception %s \n".formatted(InputDataException.class.getSimpleName()));
        Mockito
                .when(fileRepo.findByUserAndFilename(Mockito.any(User.class), Mockito.anyString()))
                .thenReturn(testFile)
                .thenReturn(testFile);

        assertThrows(InputDataException.class,
                () -> service.editFileName(testFile.getFilename(), "same_filename", testUser));
    }

    @Test
    @DisplayName("Checks the file was not updated due to an unknown error:" +
            "   editFileNameErrServerTest() ")
    public void editFileNameErrServerTest() {
        System.out.println("  Failed edit file name:");
        logCapture("  Got exception %s \n".formatted(InternalServerException.class.getSimpleName()));
        Mockito
                .when(fileRepo.findByUserAndFilename(Mockito.any(User.class), Mockito.anyString()))
                .thenReturn(testFile)
                .thenReturn(null)
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
