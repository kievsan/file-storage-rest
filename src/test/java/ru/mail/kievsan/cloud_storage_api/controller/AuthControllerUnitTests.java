package ru.mail.kievsan.cloud_storage_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.mail.kievsan.cloud_storage_api.config.AuthConfig;
import ru.mail.kievsan.cloud_storage_api.controller.exception_handler_advice.ExceptionHandlerAdvice;
import ru.mail.kievsan.cloud_storage_api.exception.NotAuthenticateException;
import ru.mail.kievsan.cloud_storage_api.exception.UserNotFoundException;
import ru.mail.kievsan.cloud_storage_api.model.Role;
import ru.mail.kievsan.cloud_storage_api.model.dto.auth.AuthRequest;
import ru.mail.kievsan.cloud_storage_api.model.dto.auth.AuthResponse;
import ru.mail.kievsan.cloud_storage_api.model.dto.err.ErrResponse;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.repository.UserJPARepo;
import ru.mail.kievsan.cloud_storage_api.security.JwtAuthenticationEntryPoint;
import ru.mail.kievsan.cloud_storage_api.security.JwtProvider;
import ru.mail.kievsan.cloud_storage_api.security.JwtUserDetails;
import ru.mail.kievsan.cloud_storage_api.security.SecurityConfig;
import ru.mail.kievsan.cloud_storage_api.service.AuthService;
import ru.mail.kievsan.cloud_storage_api.util.UserProvider;

import java.security.Key;
import java.util.Date;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.mail.kievsan.cloud_storage_api.security.ISecuritySettings.*;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, AuthConfig.class})
public class AuthControllerUnitTests {

    private static final Logger log = LoggerFactory.getLogger(AuthControllerUnitTests.class);
    private static long suiteStartTime;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    AuthService authService;

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

    User testUser;
    String testJwt;
    AuthRequest loginRequest;

    final String secretKey = "0K3l/+/b+b8VaB67FyspX7aSU++kdO6MXHJR2Kqr4VPE7y2R2UJ3iMOJnLNI7+T1";
    final long tokenLifetime = 600000;

    @BeforeAll
    public static void testSuiteInit() {
        System.out.println("----------- Running 'Auth' controller tests...");
        suiteStartTime = System.currentTimeMillis();
    }

    @AfterAll
    public static void testSuiteComplete() {
        System.out.printf("\n'Auth' controller tests complete: %s ms.\n\n", (System.currentTimeMillis() - suiteStartTime));
    }

    @BeforeEach
    public void runTest() {
        System.out.println("\nStarting new test " + this);
        testUser = newUser();
        testJwt = newJwt();
        loginRequest = new AuthRequest(testUser.getEmail(), testUser.getPassword());
    }

    @AfterEach
    public void finishTest() {
        testUser = null;
        testJwt = null;
        loginRequest = null;
    }

    @Test
    public void loginTest() throws Exception {
        System.out.println("  Login user");
        var loginResponse = new AuthResponse(testJwt);
        mockAuthorize();
        Mockito.when(authService.authenticate(Mockito.any(AuthRequest.class))).thenReturn(loginResponse);

        var mockRequest = post(LOGIN_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(loginRequest))
                .with(csrf());
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.auth-token", Matchers.is(testJwt)));
    }

    @Test
    public void loginErrNotFoundTest() throws Exception {
        System.out.printf("  Login user error:  user with email %s not found...\n", testUser.getEmail());
        String errMsg = "The user not found, authentication is not possible!";
        log.info(errMsg);
        var ex = new UserNotFoundException(errMsg, null, null, null, "'auth service'");
        var errResponse = new ResponseEntity<>(new ErrResponse(ex.getMessage(), 0), ex.getHttpStatus());

        Mockito.doReturn(errResponse).when(exceptionHandlerAdvice).handlerUserNotFound(Mockito.any(ex.getClass()));
        Mockito.doThrow(ex).when(authService).authenticate(Mockito.any(AuthRequest.class));
        mockAuthorize();

        var mockRequest = post(LOGIN_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(loginRequest))
                .with(csrf());
        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void loginErrNotAuthenticateTest() throws Exception {
        System.out.println("  Login user error");
        String errMsg = "The user was not authenticated, unknown runtime error!";
        log.info(errMsg);
        var ex = new NotAuthenticateException(errMsg, null, null, null, "'auth service'");
        var errResponse = new ResponseEntity<>(new ErrResponse(ex.getMessage(), 0), ex.getHttpStatus());

        Mockito.doReturn(errResponse).when(exceptionHandlerAdvice).handlerServerErr(Mockito.any(ex.getClass()));
        Mockito.doThrow(ex).when(authService).authenticate(Mockito.any(AuthRequest.class));
        mockAuthorize();

        var mockRequest = post(LOGIN_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(loginRequest))
                .with(csrf());
        mockMvc.perform(mockRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void logoutTest() throws Exception {
        System.out.println("  Logout user");
        mockAuthorize();
        Mockito.when(authService.logout(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn("Success logout");

        mockMvc.perform(mockRequest(post(LOGOUT_URI))).andExpect(status().isOk());
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

        log.info("testing login/logout user:  '{}', {}, {}", testUser.getNickname(), testUser.getEmail(), testUser.getRole());
    }

    public MockHttpServletRequestBuilder mockRequest(MockHttpServletRequestBuilder entryPoint) {
        return entryPoint
                .header("auth-token", "Bearer " + testJwt)
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(testUser));
    }
}
