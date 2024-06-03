package ru.mail.kievsan.cloud_storage_api.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mail.kievsan.cloud_storage_api.repository.UserJPARepo;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTests {

    private static long suiteStartTime;

    @InjectMocks
    private UserService userService;

    @Mock
    UserJPARepo userRepo;

//    AutoCloseable openMocks;

    @BeforeAll
    public static void testSuiteInit() {
        System.out.println("----------- Running User service unit tests...");
        suiteStartTime = System.currentTimeMillis();
    }

    @AfterAll
    public static void testSuiteComplete() {
        System.out.printf("User service unit tests complete: %s ms.\n\n", (System.currentTimeMillis() - suiteStartTime));
    }

    @BeforeEach
    public void runTest() {
        System.out.println("Starting new test " + this);
//        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void finishTest() throws Exception {
        // my tear down code...
//        openMocks.close();
    }

}
