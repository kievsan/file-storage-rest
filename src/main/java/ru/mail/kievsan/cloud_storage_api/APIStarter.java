package ru.mail.kievsan.cloud_storage_api;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.mail.kievsan.cloud_storage_api.model.Role;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.service.UserService;

@RequiredArgsConstructor
@Component
public class APIStarter implements CommandLineRunner {

    private final PasswordEncoder encoder;
    private final UserService userService;

    @Override
    public void run(String... args) {
        userService.signup(User.builder()
                .nickname("starter")
                .email("admin.starter@gmail.ru")
                .password(encoder.encode("7410"))
                .role(Role.ADMIN)
                .enabled(true)
                .build(),
                "User 'admin.starter@gmail.ru' ");
    }
}
