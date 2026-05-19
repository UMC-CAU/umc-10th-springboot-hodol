package com.example.umc_study.global.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class FlywayConfigurationTest {

    @Test
    @DisplayName("flyway migration and test profile safeguard exist")
    void flywayMigrationAndTestSafeguardExist() throws IOException {
        ClassPathResource migration = new ClassPathResource("db/migration/V1__allow_local_social_type.sql");
        String testApplication = Files.readString(Path.of("src/test/resources/application.yml"));

        assertThat(migration.exists()).isTrue();
        assertThat(testApplication).contains("flyway:");
        assertThat(testApplication).contains("enabled: false");
    }
}
