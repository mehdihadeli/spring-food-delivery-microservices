package com.github.mehdihadeli.buildingblocks.test;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.Mediator;
import org.junit.jupiter.api.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
        // classes = TestApplication.class
        )
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class ApplicationTestBase {

    private static SharedFixture sharedFixture;

    public ApplicationTestBase(ApplicationContext applicationContext) {
        sharedFixture = new SharedFixture(applicationContext);
    }

    @LocalServerPort
    protected int port;

    // This is a wrapper around the real RabbitTemplate or Mediator object using Mockito's spy functionality
    // It maintains all the real functionality but adds monitoring capabilities like: Track method invocations, Verify
    // if specific methods were called
    @MockitoSpyBean
    protected RabbitTemplate spyRabbitTemplate;

    @MockitoSpyBean
    protected Mediator spyMediator;

    @BeforeAll
    static void beforeAllHook() {
        sharedFixture.initialize();
    }

    @AfterAll
    static void afterAllHook() {
        sharedFixture.dispose();
    }

    @AfterEach
    public void afterEachHook() {
        sharedFixture.resetDatabasesAsync();
    }

    @BeforeEach
    public void beforeEachHook() {}

    @DynamicPropertySource
    static void configureTestDatabase(DynamicPropertyRegistry registry) {
        sharedFixture.configureProperties(registry);
    }

    // - Provides test-specific bean configurations
    // - Allows overriding of production beans for testing
    @TestConfiguration
    static class TestConfigs {
        @Bean
        public TestRestTemplate testRestTemplate() {
            return new TestRestTemplate();
        }
    }
}
