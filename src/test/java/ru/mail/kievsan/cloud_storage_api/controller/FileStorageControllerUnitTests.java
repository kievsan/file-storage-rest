package ru.mail.kievsan.cloud_storage_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.mail.kievsan.cloud_storage_api.config.AuthConfig;
import ru.mail.kievsan.cloud_storage_api.controller.exception_handler_advice.ExceptionHandlerAdvice;
import ru.mail.kievsan.cloud_storage_api.exception.AdviceException;
import ru.mail.kievsan.cloud_storage_api.exception.InputDataException;
import ru.mail.kievsan.cloud_storage_api.exception.InternalServerException;
import ru.mail.kievsan.cloud_storage_api.model.Role;
import ru.mail.kievsan.cloud_storage_api.model.dto.err.ErrResponse;
import ru.mail.kievsan.cloud_storage_api.model.dto.file.EditFileNameRequest;
import ru.mail.kievsan.cloud_storage_api.model.entity.File;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.security.JwtAuthenticationEntryPoint;
import ru.mail.kievsan.cloud_storage_api.security.JwtProvider;
import ru.mail.kievsan.cloud_storage_api.security.JwtUserDetails;
import ru.mail.kievsan.cloud_storage_api.security.SecurityConfig;
import ru.mail.kievsan.cloud_storage_api.service.FileStorageService;
import ru.mail.kievsan.cloud_storage_api.util.UserProvider;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.mail.kievsan.cloud_storage_api.security.ISecuritySettings.FILE_URI;

@WebMvcTest(FileStorageController.class)
@Import({SecurityConfig.class, AuthConfig.class})
public class FileStorageControllerUnitTests {

    private static final Logger log = LoggerFactory.getLogger(FileStorageControllerUnitTests.class);
    private static long suiteStartTime;

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    FileStorageService fileService;

    @MockBean
    JwtProvider jwtProvider;
    @MockBean
    JwtUserDetails userDetails;
    @MockBean
    UserProvider userProvider;
    @MockBean
    JwtAuthenticationEntryPoint entryPoint;
    @MockBean
    ExceptionHandlerAdvice exceptionHandlerAdvice;

    File testFile;
    User testUser;
    String testJwt;
    final String secretKey = "0K3l/+/b+b8VaB67FyspX7aSU++kdO6MXHJR2Kqr4VPE7y2R2UJ3iMOJnLNI7+T1";
    final long tokenLifetime = 600000;

    @BeforeAll
    public static void testSuiteInit() {
        System.out.println("----------- Running 'File Storage' controller tests... at " + LocalDateTime.now());
        suiteStartTime = System.currentTimeMillis();
    }

    @AfterAll
    public static void testSuiteComplete() {
        System.out.printf("\n'File Storage' controller tests complete: %s ms.\n\n", (System.currentTimeMillis() - suiteStartTime));
    }

    @BeforeEach
    public void runTest() {
        System.out.println("\nStarting new test " + this);
        testFile = newFile(1);
        testUser = newUser();
        testJwt = newJwt();
    }

    @AfterEach
    public void finishTest() {
        testFile = null;
        testUser = null;
        testJwt = null;
    }

    @Test
    public void uploadFileOkTest() throws Exception {
        System.out.println("  Successful user file upload: ");
        mockAuth();

        var mockRequest = mockRequest(post(FILE_URI))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .content("test content".getBytes());
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @ParameterizedTest(name = "{index} - {argumentsWithNames}")
    @MethodSource
    public void uploadFileErrTest(AdviceException ex) throws Exception {
        System.out.println("  Upload user file error:   " + ex.getHttpStatus());
        log.info(ex.getMessage());

        var errResponse = new ResponseEntity<>(new ErrResponse(ex.getMessage(), 0), ex.getHttpStatus());

        Mockito.doThrow(ex).when(fileService).uploadFile(Mockito.anyString(), Mockito.any(), Mockito.any());
        var mockResponse = ex.isInternalServerException()
                ? Mockito.doReturn(errResponse).when(exceptionHandlerAdvice).handlerServerErr(Mockito.any(InternalServerException.class))
                : Mockito.doReturn(errResponse).when(exceptionHandlerAdvice).handlerErrInputData(Mockito.any(InputDataException.class));
        mockAuth();

        var mockRequest = mockRequest(post(FILE_URI))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .content("test with exception".getBytes());
        mockMvc.perform(mockRequest)
                .andExpect(ex.isInternalServerException()
                        ? status().isInternalServerError()
                        : status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    static Stream<AdviceException> uploadFileErrTest() {
        return Stream.of(
                new InputDataException("file name not found or the file already exists with the same name"),
                new InternalServerException("server error edit file name, the file name remains the same...")
        );
    }

    @Test
    public void downloadFileOkTest() throws Exception {
        System.out.println("  Successful user file download: ");
        Mockito.when(fileService.downloadFile(Mockito.anyString(), Mockito.any())).thenReturn(testFile);
        mockAuth();

        mockMvc.perform(mockRequest(get(FILE_URI)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.MULTIPART_FORM_DATA_VALUE));
    }

    @ParameterizedTest(name = "{index} - {argumentsWithNames}")
    @MethodSource
    public void downloadFileErrTest(AdviceException ex) throws Exception {
        System.out.println("  Download user file error:   " + ex.getHttpStatus());
        log.info(ex.getMessage());

        var errResponse = new ResponseEntity<>(new ErrResponse(ex.getMessage(), 0), ex.getHttpStatus());

        Mockito.doThrow(ex).when(fileService).downloadFile(Mockito.anyString(), Mockito.any());
        Mockito.doReturn(errResponse).when(exceptionHandlerAdvice).handlerErrInputData(Mockito.any(InputDataException.class));
        mockAuth();

        mockMvc.perform(mockRequest(get(FILE_URI)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    static Stream<AdviceException> downloadFileErrTest() {
        return Stream.of(new InputDataException("Download file error: file not found. Downloading failed!"));
    }

    @Test
    public void deleteFileOkTest() throws Exception {
        System.out.println("  Successful user file delete: ");
        Mockito.doNothing().when(fileService).deleteFile(Mockito.anyString(), Mockito.any());
        mockAuth();

        mockMvc.perform(mockRequest(delete(FILE_URI)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @ParameterizedTest(name = "{index} - {argumentsWithNames}")
    @MethodSource
    public void deleteFileErrTest(AdviceException ex) throws Exception {
        System.out.println("  Delete user file error:   " + ex.getHttpStatus());
        log.info(ex.getMessage());

        var errResponse = new ResponseEntity<>(new ErrResponse(ex.getMessage(), 0), ex.getHttpStatus());

        Mockito.doThrow(ex).when(fileService).deleteFile(Mockito.anyString(), Mockito.any());
        var mockResponse = ex.isInternalServerException()
                ? Mockito.doReturn(errResponse).when(exceptionHandlerAdvice).handlerServerErr(Mockito.any(InternalServerException.class))
                : Mockito.doReturn(errResponse).when(exceptionHandlerAdvice).handlerErrInputData(Mockito.any(InputDataException.class));
        mockAuth();

        mockMvc.perform(mockRequest(delete(FILE_URI)))
                .andExpect(ex.isInternalServerException()
                        ? status().isInternalServerError()
                        : status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    static Stream<AdviceException> deleteFileErrTest() {
        return Stream.of(
                new InputDataException("Delete user file error: file not found. Deleting failed!"),
                new InternalServerException("server error delete file...")
        );
    }

    @Test
    public void editFileNameOkTest() throws Exception {
        System.out.println("  Successful user file name edit: ");
        var request = new EditFileNameRequest("new_testfile");
        Mockito.doNothing().when(fileService).editFileName(Mockito.anyString(), Mockito.anyString(), Mockito.any());
        mockAuth();

        var mockRequest = mockRequest(put(FILE_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request));
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @ParameterizedTest(name = "{index} - {argumentsWithNames}")
    @MethodSource
    public void editFileNameErrTest(AdviceException ex) throws Exception {
        System.out.println("  Edit user file name error:   " + ex.getHttpStatus());
        log.info(ex.getMessage());

        var request = new EditFileNameRequest("new_testfile");
        var errResponse = new ResponseEntity<>(new ErrResponse(ex.getMessage(), 0), ex.getHttpStatus());

        Mockito.doThrow(ex).when(fileService).editFileName(Mockito.anyString(), Mockito.anyString(), Mockito.any());
        var mockResponse = ex.isInternalServerException()
                ? Mockito.doReturn(errResponse).when(exceptionHandlerAdvice).handlerServerErr(Mockito.any(InternalServerException.class))
                : Mockito.doReturn(errResponse).when(exceptionHandlerAdvice).handlerErrInputData(Mockito.any(InputDataException.class));
        mockAuth();

        var mockRequest = mockRequest(put(FILE_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request));
        mockMvc.perform(mockRequest)
                .andExpect(ex.isInternalServerException()
                        ? status().isInternalServerError()
                        : status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    static Stream<AdviceException> editFileNameErrTest() {
        return Stream.of(
                new InputDataException("file name not found or the file already exists with the same name"),
                new InternalServerException("server error edit file name, the file name remains the same...")
        );
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
                .nickname("testuser")
                .email("testuser@mail.ru")
                .password("password")
                .role(Role.USER)
                .enabled(true)
                .build();
    }

    public String newJwt() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        Key signingKey = Keys.hmacShaKeyFor(keyBytes);
        return Jwts.builder()
                .subject(testUser.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenLifetime))
                .signWith(signingKey)
                .compact();
    }

    public void mockAuth() {
        Mockito.when(jwtProvider.generateToken(Mockito.any(UserDetails.class))).thenReturn(testJwt);

        Mockito.when(userDetails.loadUserByUsername(Mockito.anyString())).thenReturn(testUser);
        Mockito.when(userDetails.loadUserByJWT(Mockito.anyString())).thenReturn(testUser);
        Mockito.when(userDetails.presentAuthenticated())
                .thenReturn("user  %s, %s".formatted(testUser.getUsername(), testUser.getAuthorities()));
        Mockito.when(userDetails.presentJWT(Mockito.anyString()))
                .thenReturn(testJwt.substring(0, testJwt.length()/10)
                        + "..." + testJwt.substring(testJwt.length() - 2));

        log.info("testing user:  '{}', {}, {}", testUser.getNickname(), testUser.getEmail(), testUser.getRole());
    }

    public MockHttpServletRequestBuilder mockRequest(MockHttpServletRequestBuilder entryPoint) {
        return entryPoint
                .header("auth-token", "Bearer " + testJwt)
                .param("filename", "testfile")
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(testUser));
    }
}
