package com.github.mehdihadeli.buildingblocks.test;

import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.TestHarness;
import com.github.mehdihadeli.buildingblocks.core.messaging.TestHarnessImpl;
import com.github.mehdihadeli.buildingblocks.rabbitmq.ConsumePipeline;
import com.github.mehdihadeli.buildingblocks.rabbitmq.PrePublishPipeline;
import com.github.mehdihadeli.buildingblocks.rabbitmq.test.TestConsumePipeLine;
import com.github.mehdihadeli.buildingblocks.rabbitmq.test.TestPrePublishPipeline;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@Import(IntegrationTestBase.TestConfigs.class)
public abstract class IntegrationTestBase {
    private static final SharedFixture sharedFixture = new SharedFixture();
    private static ApplicationContext applicationContext;

    protected IntegrationTestBase(ApplicationContext context) {
        applicationContext = context;
    }

    private String testApiPrefix = "";

    @LocalServerPort
    private int testApiPort;

    // after resolving dependencies and creating ApplicationContext
    @PostConstruct
    void postConstruct() {}

    // will call before resolving dependencies and PostConstruct and beforeAllHook
    @DynamicPropertySource
    public static void configureTestProperties(DynamicPropertyRegistry registry) {
        sharedFixture.configureTestProperties(registry);
    }

    public SharedFixture sharedFixture() {
        return sharedFixture;
    }

    public void setTestApiPrefix(String testApiPrefix) {
        this.testApiPrefix = testApiPrefix;
    }

    public int getTestApiPort() {
        return testApiPort;
    }

    public String getTestApiPrefix() {
        return testApiPrefix;
    }

    public String getTestApiUrl() {
        return "http://localhost:%s/%s".formatted(testApiPort, testApiPrefix);
    }

    @BeforeAll
    static void beforeAllHook() {
        sharedFixture.initialize(applicationContext);
    }

    @AfterAll
    static void afterAllHook() {
        sharedFixture.dispose();
    }

    @AfterEach
    public void afterEachHook() {
        sharedFixture.resetDatabasesAsync();
        sharedFixture.cleanupMessaging();
    }

    @BeforeEach
    public void beforeEachHook() {}

    // - Provides test-specific bean configurations
    // - Allows overriding of production beans for testing
    // https://www.educative.io/answers/how-to-use-testconfigurations-annotations-using-import
    @TestConfiguration
    public static class TestConfigs {
        @Bean
        public TestHarness testRestTemplate() {
            return new TestHarnessImpl();
        }

        @Bean
        public ConsumePipeline testConsumePipeLine(TestHarness testHarness) {
            return new TestConsumePipeLine(testHarness);
        }

        @Bean
        public PrePublishPipeline testPrePublishPipeline(TestHarness testHarness) {
            return new TestPrePublishPipeline(testHarness);
        }
    }
}
