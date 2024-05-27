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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import ru.mail.kievsan.cloud_storage_api.config.AuthConfig;
import ru.mail.kievsan.cloud_storage_api.model.Role;
import ru.mail.kievsan.cloud_storage_api.model.dto.user.SignUpRequest;
import ru.mail.kievsan.cloud_storage_api.model.dto.user.SignUpResponse;
import ru.mail.kievsan.cloud_storage_api.model.dto.user.UpdateRequest;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.repository.UserJPARepo;
import ru.mail.kievsan.cloud_storage_api.security.*;
import ru.mail.kievsan.cloud_storage_api.service.UserService;
import ru.mail.kievsan.cloud_storage_api.util.UserProvider;

import java.security.Key;
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

    User testUser;
    SignUpResponse testResponse;
    final String secretKey = "0K3l/+/b+b8VaB67FyspX7aSU++kdO6MXHJR2Kqr4VPE7y2R2UJ3iMOJnLNI7+T1";
    final long tokenLifetime = 600000;

    @BeforeAll
    public static void testSuiteInit() {
        System.out.println("----------- Running 'User' controller tests...");
        suiteStartTime = System.currentTimeMillis();
    }

    @AfterAll
    public static void testSuiteComplete() {
        System.out.printf("'User' controller tests complete: %s ms.\n\n", (System.currentTimeMillis() - suiteStartTime));
    }

    @BeforeEach
    public void runTest() {
        System.out.println("Starting new test " + this);
        testUser = newUser();
        testResponse = new SignUpResponse(testUser);
    }

    @AfterEach
    public void finishTest() {
        testUser = null;
        testResponse = null;
    }

    @Test
    public void getOwner() throws Exception {
        Mockito.when(userService.getCurrentUser(Mockito.any())).thenReturn(testResponse); //+++++ Mock

        mockAuthorize();
        var mockRequest = get(USER_URI)
                .header("auth-token", "Bearer " + testJwt())
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(testUser));
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void getUser() throws Exception {
        testUser.setRole(Role.ADMIN);
        testResponse.setRole(Role.ADMIN);

        Mockito.when(userService.getUserById(Mockito.anyLong(), Mockito.any())).thenReturn(testResponse); //+++++ Mock

        mockAuthorize();
        var mockRequest = get(USER_URI + "/1")
                .header("auth-token", "Bearer " + testJwt())
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(testUser));
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void getUserGetForbiddenErrIfIamNotAdmin() throws Exception {
        Mockito.when(userService.getUserById(Mockito.anyLong(), Mockito.any())).thenReturn(testResponse); //+++++ Mock

        mockAuthorize();
        var mockRequest = get(USER_URI + "/1")
                .header("auth-token", "Bearer " + testJwt())
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(testUser));
        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden());
    }

    @Test
    public void delOwner() throws Exception {
        mockAuthorize();
        var mockRequest = delete(USER_URI)
                .header("auth-token", "Bearer " + testJwt())
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(testUser));
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

//    @Test
//    public void delOwnerGetForbiddenErrIfIamAdmin() throws Exception {
//        testUser.setRole(Role.ADMIN);
//
//        mockAuthorize();
//        var mockRequest = delete(USER_URI)
//                .header("auth-token", "Bearer " + testJwt())
//                .with(csrf())
//                .with(SecurityMockMvcRequestPostProcessors.user(testUser));
//        mockMvc.perform(mockRequest)
//                .andExpect(status().isForbidden());
//    }

    @Test
    public void delUser() throws Exception {
        testUser.setRole(Role.ADMIN);

        mockAuthorize();
        var mockRequest = delete(USER_URI + "/1")
                .header("auth-token", "Bearer " + testJwt())
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(testUser));
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void delUserGetForbiddenErrIfIamNotAdmin() throws Exception {
        mockAuthorize();
        var mockRequest = delete(USER_URI + "/1")
                .header("auth-token", "Bearer " + testJwt())
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(testUser));
        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden());
    }

    @Test
    public void delUserGetForbiddenErrIfDeletingIsStarterAdmin() throws Exception {
        var deletingUser = newStarterAdmin();
        Mockito.when(userRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(deletingUser)); //+++++ Mock

        mockAuthorize();
        var mockRequest = delete(USER_URI + "/1")
                .header("auth-token", "Bearer " + testJwt())
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(testUser));
        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden());
    }

    @Test
    public void register() throws Exception {
        var testRequest = new SignUpRequest(testUser);

        Mockito.when(userService.register(Mockito.any(SignUpRequest.class), Mockito.any())).thenReturn(testResponse); //+++++ Mock

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
    public void edit() throws Exception {
        var testRequest = new UpdateRequest("new_" + testUser.getEmail(), "new_" + testUser.getPassword());
        testResponse = new SignUpResponse(testUser.getId(), testUser.getNickname(), testRequest.getEmail(), testUser.getRole());

        Mockito.when(userService.updateUser(Mockito.any(UpdateRequest.class), Mockito.any())).thenReturn(testResponse); //+++++ Mock

        mockAuthorize();
        var mockRequest = put(USER_URI)
                .header("auth-token", "Bearer " + testJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(testRequest))
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(testUser));
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", Matchers.is(testRequest.getEmail())));
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

    public String testJwt() {
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
        Mockito.when(jwtProvider.generateToken(Mockito.any(UserDetails.class))).thenReturn(testJwt()); //+++++ Mock

        Mockito.when(userDetails.loadUserByUsername(Mockito.anyString())).thenReturn(testUser); //+++++ Mock
        Mockito.when(userDetails.loadUserByJWT(Mockito.anyString())).thenReturn(testUser); //+++++ Mock
        Mockito.when(userDetails.presentAuthenticated())
                .thenReturn("user  %s, %s".formatted(testUser.getUsername(), testUser.getAuthorities())); //+++++ Mock
        Mockito.when(userDetails.presentJWT(Mockito.anyString()))
                .thenReturn(testJwt().substring(0, testJwt().length()/10) + "..."); //+++++ Mock

        log.info("testing user:  '{}', {}, {}", testUser.getNickname(), testUser.getEmail(), testUser.getRole());
    }
}
