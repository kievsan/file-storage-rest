package ru.mail.kievsan.cloud_storage_api.model;

import org.junit.jupiter.api.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class RoleTests {

    private static long suiteStartTime;

    @BeforeAll
    public static void testSuiteInit() {
        System.out.println("Running roles testing...");
        suiteStartTime = System.currentTimeMillis(); // .nanoTime()
    }

    @AfterAll
    public static void testSuiteComplete() {
        System.out.printf("Role tests complete: %s ms.\n\n", (System.currentTimeMillis() - suiteStartTime));
    }

    @Test
    public void hasRoles() {
        assertThat(Role.ADMIN.getAuthority(), is("ROLE_ADMIN"));
        assertThat(Role.USER.getAuthority(), is("ROLE_USER"));
    }
}
