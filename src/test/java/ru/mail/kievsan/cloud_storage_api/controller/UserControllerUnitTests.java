package ru.mail.kievsan.cloud_storage_api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.mail.kievsan.cloud_storage_api.security.SecuritySettings.*;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mail.kievsan.cloud_storage_api.model.Role;
import ru.mail.kievsan.cloud_storage_api.model.dto.auth.SignUpRequest;
import ru.mail.kievsan.cloud_storage_api.model.dto.auth.SignUpResponse;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.security.JWTUserDetails;
import ru.mail.kievsan.cloud_storage_api.service.UserService;
import ru.mail.kievsan.cloud_storage_api.util.UserProvider;

@WebMvcTest(UserController.class)
public class UserControllerUnitTests {

    private static long suiteStartTime;
    private static String BASE_URL;

    @MockBean
    UserService userService;
    @MockBean
    UserProvider userProvider;
    @MockBean
    JWTUserDetails userDetails;

    @Autowired
    private MockMvc mockMvc;

    @Value("${server.port}")
    String port;

    User testUser;
    SignUpResponse testSignUpResponse;

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
        BASE_URL = "http://localhost:" + port;
        testUser = newUser();
        testSignUpResponse = newSignUpResponse();
    }

    @AfterEach
    public void finishTest() {
        testUser = null;
        testSignUpResponse = null;
    }

    @Test
    public void register() throws Exception {
        Mockito.when(userService.register(Mockito.any(SignUpRequest.class), Mockito.any(User.class))).thenReturn(testSignUpResponse);

        mockMvc.perform(post(BASE_URL + SIGN_UP_URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname", Matchers.is("testuser")))
                .andExpect(jsonPath("$.email", Matchers.is("testuser@mail\\.ru")))
                .andExpect(jsonPath("$.role", Matchers.is(Role.USER)))
        ;
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

    private SignUpResponse newSignUpResponse() {
        return new SignUpResponse(testUser.getId(), testUser.getNickname(), testUser.getEmail(), testUser.getRole());
    }
}
