package ru.mail.kievsan.cloud_storage_api.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.mail.kievsan.cloud_storage_api.security.SecuritySettings.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.mail.kievsan.cloud_storage_api.config.AuthConfig;
import ru.mail.kievsan.cloud_storage_api.model.Role;
import ru.mail.kievsan.cloud_storage_api.model.dto.user.SignUpRequest;
import ru.mail.kievsan.cloud_storage_api.model.dto.user.SignUpResponse;
import ru.mail.kievsan.cloud_storage_api.model.dto.user.UpdateRequest;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.security.JWTUserDetails;
import ru.mail.kievsan.cloud_storage_api.security.JwtAuthenticationEntryPoint;
import ru.mail.kievsan.cloud_storage_api.security.SecurityConfig;
import ru.mail.kievsan.cloud_storage_api.service.UserService;
import ru.mail.kievsan.cloud_storage_api.util.UserProvider;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, AuthConfig.class})
public class UserControllerUnitTests {

    private static long suiteStartTime;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    UserService userService;
    @MockBean
    JwtAuthenticationEntryPoint entryPoint;
    @MockBean
    UserProvider userProvider;
    @MockBean
    JWTUserDetails userDetails;

    User testUser;
    SignUpResponse testResponse;

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
    }

    @AfterEach
    public void finishTest() {
        testUser = null;
        testResponse = null;
    }

    @Test
    public void register() throws Exception {
        var testRequest = new SignUpRequest(testUser.getNickname(), testUser.getEmail(), testUser.getPassword(), testUser.getRole());
        testResponse = new SignUpResponse(testUser.getId(), testUser.getNickname(), testUser.getEmail(), testUser.getRole());

        Mockito.when(userService.register(Mockito.any(SignUpRequest.class), Mockito.any())).thenReturn(testResponse);

        mockMvc.perform(post(SIGN_UP_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testRequest))
                        .with(csrf())
                )
//                .andExpect(status().isUnauthorized())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname", Matchers.is(testUser.getNickname())))
                .andExpect(jsonPath("$.email", Matchers.is(testUser.getEmail())))
                .andExpect(jsonPath("$.role", Matchers.is(testUser.getRole().toString())))
        ;
    }

    @Test
    public void edit() throws Exception {
        var testRequest = new UpdateRequest("new_" + testUser.getEmail(), "new_" + testUser.getPassword());
        testResponse = new SignUpResponse(testUser.getId(), testUser.getNickname(), testRequest.getEmail(), testUser.getRole());

        // ????

        Mockito.when(userService.updateUser(Mockito.any(UpdateRequest.class), Mockito.any())).thenReturn(testResponse);

        mockMvc.perform(put(USER_URI).header("auth-token", "......")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testRequest))
                        .with(csrf())
                )
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.nickname", Matchers.is(testUser.getNickname())))
//                .andExpect(jsonPath("$.email", Matchers.is(testRequest.getEmail())))
//                .andExpect(jsonPath("$.role", Matchers.is(testUser.getRole().name())))
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
}
