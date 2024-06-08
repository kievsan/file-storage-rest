package ru.mail.kievsan.cloud_storage_api.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.mail.kievsan.cloud_storage_api.security.ISecuritySettings.*;

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
import ru.mail.kievsan.cloud_storage_api.exception.NoRightsException;
import ru.mail.kievsan.cloud_storage_api.exception.UserRegistrationException;
import ru.mail.kievsan.cloud_storage_api.model.Role;
import ru.mail.kievsan.cloud_storage_api.model.dto.err.ErrResponse;
import ru.mail.kievsan.cloud_storage_api.model.dto.user.SignUpRequest;
import ru.mail.kievsan.cloud_storage_api.model.dto.user.SignUpResponse;
import ru.mail.kievsan.cloud_storage_api.model.dto.user.UpdateRequest;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.repository.UserJPARepo;
import ru.mail.kievsan.cloud_storage_api.security.*;
import ru.mail.kievsan.cloud_storage_api.service.UserService;
import ru.mail.kievsan.cloud_storage_api.util.UserProvider;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, AuthConfig.class})
public class UserControllerUnitTests {

    private static final Logger log = LoggerFactory.getLogger(UserControllerUnitTests.class);
    private static long suiteStartTime;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

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

    User testUser;
    String testJwt;
    SignUpResponse testResponse;
    final String secretKey = "0K3l/+/b+b8VaB67FyspX7aSU++kdO6MXHJR2Kqr4VPE7y2R2UJ3iMOJnLNI7+T1";
    final long tokenLifetime = 600000;

    @BeforeAll
    public static void testSuiteInit() {
        System.out.println("----------- Running 'User' controller tests... at " + LocalDateTime.now());
        suiteStartTime = System.currentTimeMillis();
    }

    @AfterAll
    public static void testSuiteComplete() {
        System.out.printf("\n'User' controller tests complete: %s ms.\n\n", (System.currentTimeMillis() - suiteStartTime));
    }

    @BeforeEach
    public void runTest() {
        System.out.println("\nStarting new test " + this);
        testUser = newUser();
        testJwt = newJwt();
        testResponse = new SignUpResponse(testUser);
    }

    @AfterEach
    public void finishTest() {
        testUser = null;
        testJwt = null;
        testResponse = null;
    }

    @Test
    public void getOwner() throws Exception {
        Mockito.when(userService.getCurrentUser(Mockito.any())).thenReturn(testResponse);
        mockAuthorize();
        mockMvc.perform(mockRequest(get(USER_URI))).andExpect(status().isOk());
    }

    @Test
    public void getUser() throws Exception {
        testUser.setRole(Role.ADMIN);
        testJwt = newJwt();
        testResponse.setRole(Role.ADMIN);

        Mockito.when(userService.getUserById(Mockito.anyLong(), Mockito.any())).thenReturn(testResponse);
        mockAuthorize();

        mockMvc.perform(mockRequest(get(USER_URI + "/1"))).andExpect(status().isOk());
    }

    @Test
    public void getUserGetForbiddenErrIfIamNotAdmin() throws Exception {
        Mockito.when(userService.getUserById(Mockito.anyLong(), Mockito.any())).thenReturn(testResponse);
        mockAuthorize();
        mockMvc.perform(mockRequest(get(USER_URI + "/1"))).andExpect(status().isForbidden());
    }

    @Test
    public void delOwner() throws Exception {
        mockAuthorize();
        mockMvc.perform(mockRequest(delete(USER_URI))).andExpect(status().isOk());
    }

    @Test
    public void delOwnerGetForbiddenErrIfIamAdmin() throws Exception {
        String errMsg = String.format("Has no rights: deleting owner - ADMIN ('%s'), %s. Can't del an ADMIN !",
                testUser.getNickname(), testUser.getUsername());
        var ex = new NoRightsException(errMsg, null, "USER", "'/user'", "'delCurrentUser' service");
        var errResponse = new ResponseEntity<>(new ErrResponse(ex.getMessage(), 0), ex.getHttpStatus());
        testUser.setRole(Role.ADMIN); //        System.out.println(mapper.writeValueAsString(testUser));
        testJwt = newJwt();

        Mockito.doReturn(errResponse).when(exceptionHandlerAdvice).handlerNoRightsErr(Mockito.any(ex.getClass()));
        Mockito.doThrow(ex).when(userService).delCurrentUser(Mockito.any());
        mockAuthorize();

        log.info(errMsg);
        mockMvc.perform(mockRequest(delete(USER_URI))).andExpect(status().isForbidden());
    }

    @Test
    public void delUser() throws Exception {
        testUser.setRole(Role.ADMIN);
        testJwt = newJwt();
        mockAuthorize();
        mockMvc.perform(mockRequest(delete(USER_URI + "/1"))).andExpect(status().isOk());
    }

    @Test
    public void delUserGetForbiddenErrIfIamNotAdmin() throws Exception {
        mockAuthorize();
        mockMvc.perform(mockRequest(delete(USER_URI + "/1"))).andExpect(status().isForbidden());
    }

    @Test
    public void delUserGetForbiddenErrIfDeletingIsStarterAdmin() throws Exception {
        Mockito.when(userRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(newStarterAdmin()));
        mockAuthorize();
        mockMvc.perform(mockRequest(delete(USER_URI + "/1"))).andExpect(status().isForbidden());
    }

    @Test
    public void register() throws Exception {
        var testRequest = new SignUpRequest(testUser);

        Mockito.when(userService.register(Mockito.any(SignUpRequest.class), Mockito.any())).thenReturn(testResponse);
        mockAuthorize();

        var mockRequest = post(SIGN_UP_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(testRequest))
                .with(csrf());
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())  //     .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.nickname", Matchers.is(testUser.getNickname())))
                .andExpect(jsonPath("$.email", Matchers.is(testUser.getEmail())))
                .andExpect(jsonPath("$.role", Matchers.is(testUser.getRole().toString())));
    }

    @Test
    public void notRegister() throws Exception {
        String errMsg = "The username is already in use, registration is not possible!";
        testUser = newStarterAdmin();
        testJwt = newJwt();
        var testRequest = new SignUpRequest(testUser);
        var ex = new UserRegistrationException(errMsg, null, null, null, "'register service'");
        var errResponse = new ResponseEntity<>(new ErrResponse(ex.getMessage(), 0), ex.getHttpStatus());

        Mockito.doReturn(errResponse).when(exceptionHandlerAdvice).handlerUserRegistrationErr(Mockito.any(ex.getClass()));
        Mockito.doThrow(ex).when(userService).register(Mockito.any(SignUpRequest.class), Mockito.any());
        mockAuthorize();

        log.info(errMsg);
        var mockRequest = post(SIGN_UP_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(testRequest))
                .with(csrf());
        mockMvc.perform(mockRequest)
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void edit() throws Exception {
        var testRequest = new UpdateRequest("new_" + testUser.getEmail(), "new_" + testUser.getPassword());
        testResponse = new SignUpResponse(testUser.getId(), testUser.getNickname(), testRequest.getEmail(), testUser.getRole());

        Mockito.when(userService.updateUser(Mockito.any(UpdateRequest.class), Mockito.any())).thenReturn(testResponse);
        mockAuthorize();

        var mockRequest = mockRequest(put(USER_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(testRequest));
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", Matchers.is(testRequest.getEmail())));
    }

    @Test
    public void notEdit() throws Exception {
        String newEmail = newStarterAdmin().getEmail();
        String errMsg = String.format("User with email '%s' already exists, the updating is not possible!", newEmail);
        var testRequest = new UpdateRequest(newEmail, "new_" + testUser.getPassword());
        var ex = new UserRegistrationException(errMsg, null, null, null, "'register service'");
        var errResponse = new ResponseEntity<>(new ErrResponse(ex.getMessage(), 0), ex.getHttpStatus());

        Mockito.doReturn(errResponse).when(exceptionHandlerAdvice).handlerUserRegistrationErr(Mockito.any(ex.getClass()));
        Mockito.doThrow(ex).when(userService).updateUser(Mockito.any(UpdateRequest.class), Mockito.any());
        mockAuthorize();

        log.info(errMsg);
        var mockRequest = mockRequest(put(USER_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(testRequest));
        mockMvc.perform(mockRequest)
                .andExpect(status().isUnprocessableEntity());
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

    private User newStarterAdmin() {
        return User.builder()
                .nickname("starter")
                .email("admin.starter@gmail.ru")
                .password("password")
                .role(Role.ADMIN)
                .enabled(true)
                .build();
    }

    public String newJwt() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        Key signingKey = Keys.hmacShaKeyFor(keyBytes);
        //auth = testUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        return Jwts.builder()
                //.claim("authorities", auth)
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
