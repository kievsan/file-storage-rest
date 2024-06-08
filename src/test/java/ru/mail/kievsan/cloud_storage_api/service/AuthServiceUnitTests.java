package ru.mail.kievsan.cloud_storage_api.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.mail.kievsan.cloud_storage_api.exception.NotAuthenticateException;
import ru.mail.kievsan.cloud_storage_api.exception.UserNotFoundException;
import ru.mail.kievsan.cloud_storage_api.model.Role;
import ru.mail.kievsan.cloud_storage_api.model.dto.auth.AuthRequest;
import ru.mail.kievsan.cloud_storage_api.model.dto.auth.AuthResponse;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.security.JwtProvider;
import ru.mail.kievsan.cloud_storage_api.security.JwtUserDetails;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceUnitTests {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceUnitTests.class);
    private static long suiteStartTime;

    @Mock
    JwtProvider jwtProvider;
    @Mock
    JwtUserDetails userDetails;
    @Mock
    AuthenticationManager authManager;

    @InjectMocks
    private AuthService service;

    User testUser;
    String testJwt;
    AuthRequest loginRequest;
    Authentication auth;

    final String secretKey = "0K3l/+/b+b8VaB67FyspX7aSU++kdO6MXHJR2Kqr4VPE7y2R2UJ3iMOJnLNI7+T1";
    final long tokenLifetime = 600000;

    @BeforeAll
    public static void testSuiteInit() {
        System.out.println("----------- Running Auth service unit tests... at " + LocalDateTime.now());
        suiteStartTime = System.currentTimeMillis();
    }

    @AfterAll
    public static void testSuiteComplete() {
        System.out.printf("\nAuth service unit tests complete: %s ms.\n\n", (System.currentTimeMillis() - suiteStartTime));
    }

    @BeforeEach
    public void runTest() {
        System.out.println("\nStarting new test " + this);
        testUser = newUser();
        testJwt = newJwt();
        loginRequest = new AuthRequest(testUser.getEmail(), testUser.getPassword());
        auth = new UsernamePasswordAuthenticationToken(testUser.getEmail(), testUser.getPassword(), testUser.getAuthorities());
    }

    @AfterEach
    void finishTest() {
        testUser = null;
        testJwt = null;
        loginRequest = null;
        auth = null;
    }

    @Test
    public void authenticateHappyTest() {
        System.out.println("  Authenticate user");
        logCapture();
        var loginResponse = new AuthResponse(testJwt);
        mockAuth();

        assertDoesNotThrow(() -> service.authenticate(loginRequest));

        var testResponse = service.authenticate(loginRequest);

        assertNotNull(testResponse.getAuthToken());
        assertEquals(loginResponse, testResponse);
    }

    @Test
    public void authenticateErrNotFoundTest() {
        System.out.printf("  Authenticate user error:  the user with email '%s' not found...\n", testUser.getEmail());
        logCapture("  The user was not found...\n");

        Mockito.when(userDetails.loadUserByUsername(Mockito.anyString())).thenThrow(UsernameNotFoundException.class);

        assertThrows(UserNotFoundException.class, () ->service.authenticate(loginRequest));
    }

    @Test
    public void authenticateErrNotAuthenticateTest() {
        System.out.printf("  Authenticate user error:  the user %s was not authenticated...\n", testUser.getEmail());
        logCapture("  The user was not authenticated, unknown runtime error!");

        Mockito.when(authManager.authenticate(Mockito.any())).thenThrow(RuntimeException.class);

        assertThrows(NotAuthenticateException.class, () ->service.authenticate(loginRequest));
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
        var JwtPresentation = testJwt.substring(0, testJwt.length()/10)
                + "..." + testJwt.substring(testJwt.length() - 2);

        Mockito.when(userDetails.presentJWT(Mockito.anyString())).thenReturn(JwtPresentation);
        Mockito.when(userDetails.loadUserByUsername(Mockito.anyString())).thenReturn(testUser);
        Mockito.when(authManager.authenticate(Mockito.any())).thenReturn(auth);
        Mockito.when(jwtProvider.generateToken(Mockito.any(UserDetails.class))).thenReturn(testJwt);
    }

    public void logCapture() {
        log.info("testing user:  '{}', {}, {}", testUser.getNickname(), testUser.getEmail(), testUser.getRole());
    }

    public void logCapture(String msg) {
        logCapture();
        log.info(msg);
    }
}
