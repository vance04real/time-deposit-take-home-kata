package org.ikigaidigital.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
public class TestDatabaseConfiguration {

    // This configuration is only used when running tests without Testcontainers
    // When using Testcontainers with @DynamicPropertySource, those properties take precedence
}