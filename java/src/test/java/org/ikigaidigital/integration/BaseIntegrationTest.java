package org.ikigaidigital.integration;

import org.ikigaidigital.TimeDepositApplication;
import org.ikigaidigital.util.DatabaseTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for integration tests.
 * Provides common configuration and utilities for all integration tests.
 * Follows DRY principle by centralizing common test setup.
 */
@SpringBootTest(classes = TimeDepositApplication.class)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    @Container
    protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.liquibase.enabled", () -> "false");
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected DatabaseTestUtil databaseTestUtil;

    /**
     * Clean database before each test to ensure test isolation.
     * Subclasses can override this method but should call super.setUp().
     */
    @BeforeEach
    protected void setUp() {
        databaseTestUtil.cleanDatabase();
    }

    /**
     * Helper method to get the base URL for API endpoints.
     * Can be overridden by subclasses if needed.
     */
    protected String getApiBasePath() {
        return "/api";
    }

    /**
     * Helper method to construct full API endpoint paths.
     */
    protected String apiPath(String endpoint) {
        return getApiBasePath() + endpoint;
    }
}