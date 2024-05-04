package ru.mail.kievsan.cloud_storage_api.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.mail.kievsan.cloud_storage_api.model.Role;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.repository.UserJPARepo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    private UserService userService;
    @Autowired
    private UserJPARepo userRepo;

    @Test
    public void testSignUpIntoDB() {
        User testUser = User.builder()
                .nickname("testuser")
                .email("testuser@mail.ru")
                .password("password")
                .role(Role.USER)
                .enabled(true)
                .build();
        userService.signup(testUser);

        User newUser = userRepo.findById(testUser.getId()).orElse(null);

        assertNotNull(newUser);
        assertEquals("testuser@mail.ru", newUser.getUsername());
    }
}
