package ru.mail.kievsan.cloud_storage_api.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.mail.kievsan.cloud_storage_api.config.AuthConfig;
import ru.mail.kievsan.cloud_storage_api.controller.exception_handler_advice.ExceptionHandlerAdvice;
import ru.mail.kievsan.cloud_storage_api.model.Role;
import ru.mail.kievsan.cloud_storage_api.model.dto.err.ErrResponse;
import ru.mail.kievsan.cloud_storage_api.model.dto.file_list.FileListResponse;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.repository.UserJPARepo;
import ru.mail.kievsan.cloud_storage_api.security.JwtAuthenticationEntryPoint;
import ru.mail.kievsan.cloud_storage_api.security.JwtProvider;
import ru.mail.kievsan.cloud_storage_api.security.JwtUserDetails;
import ru.mail.kievsan.cloud_storage_api.security.SecurityConfig;
import ru.mail.kievsan.cloud_storage_api.service.FileListService;
import ru.mail.kievsan.cloud_storage_api.service.UserService;
import ru.mail.kievsan.cloud_storage_api.util.UserProvider;

import java.security.Key;
import java.util.Date;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.mail.kievsan.cloud_storage_api.security.ISecuritySettings.*;

@WebMvcTest(FileListController.class)
@Import({SecurityConfig.class, AuthConfig.class})
public class FileListControllerUnitTests {

    private static final Logger log = LoggerFactory.getLogger(FileListControllerUnitTests.class);
    private static long suiteStartTime;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    FileListService listService;

    @MockBean
    UserService userService;
    @MockBean
    UserJPARepo userRepo;
    @MockBean
    JwtProvider jwtProvider;
    @MockBean
    UserProvider userProvider;
    @MockBean
    JwtUserDetails userDetails;
    @MockBean
    JwtAuthenticationEntryPoint entryPoint;
    @MockBean
    ExceptionHandlerAdvice exceptionHandlerAdvice;

    final List<FileListResponse> testResponse = List.of(
            newFileListResponse(0), newFileListResponse(1), newFileListResponse(2),
            newFileListResponse(3), newFileListResponse(4), newFileListResponse(5)
    );

    User testUser;
    String testJwt;
    final String secretKey = "0K3l/+/b+b8VaB67FyspX7aSU++kdO6MXHJR2Kqr4VPE7y2R2UJ3iMOJnLNI7+T1";
    final long tokenLifetime = 600000;

    @BeforeAll
    public static void testSuiteInit() {
        System.out.println("----------- Running 'File List' controller tests...");
        suiteStartTime = System.currentTimeMillis();
    }

    @AfterAll
    public static void testSuiteComplete() {
        System.out.printf("\n'File List' controller tests complete: %s ms.\n\n", (System.currentTimeMillis() - suiteStartTime));
    }

    @BeforeEach
    public void runTest() {
        System.out.println("\nStarting new test " + this);

        testUser = newUser();
        testJwt = newJwt();
    }

    @AfterEach
    public void finishTest() {
        testUser = null;
        testJwt = null;
    }

    @Test
    public void getFileList() throws Exception {
        Mockito.when(listService.getFileList(Mockito.anyInt(), Mockito.any(User.class))).thenReturn(testResponse);
        mockAuthorize();

        mockMvc.perform(mockRequest(get(FILE_LIST_URI))).andExpect(status().isOk())
//                .andExpect(jsonPath("$", Matchers.hasSize(6)))
//                .andExpect(jsonPath("$[0].filename", Matchers.is(testResponse.getFirst().getFilename())))
        ;
    }

    private FileListResponse newFileListResponse(int num) {
        return FileListResponse.builder()
                .filename("testfile" + num)
                .size(Long.decode(String.valueOf(num)) * 10)
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

    public void mockAuthorize() {
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
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(testUser));
    }
}
