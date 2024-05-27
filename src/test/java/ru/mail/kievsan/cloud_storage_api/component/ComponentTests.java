package ru.mail.kievsan.cloud_storage_api.component;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.mail.kievsan.cloud_storage_api.controller.AuthController;

@SpringBootTest
public class ComponentTests {

    @Autowired
    AuthController controller;

    @Test
    public void contextLoads() {
        assertThat(controller).isNotNull();
        // компонент был успешно введен в автоматически подключаемый атрибут
    }
}
