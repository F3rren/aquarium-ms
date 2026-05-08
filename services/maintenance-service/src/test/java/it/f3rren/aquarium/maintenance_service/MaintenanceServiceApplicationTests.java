package it.f3rren.aquarium.maintenance_service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS maintenance",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "spring.jpa.properties.hibernate.default_schema=maintenance",
    "spring.application.name=maintenance-service",
    "spring.flyway.enabled=false",
    "spring.kafka.bootstrap-servers=localhost:9092",
    "spring.kafka.admin.auto-create=false",
    "spring.kafka.listener.auto-startup=false",
    "management.health.db.enabled=false"
})
class MaintenanceServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    @DisplayName("main() should delegate to SpringApplication.run with correct class argument")
    void mainShouldDelegateToSpringApplication() {
        try (MockedStatic<SpringApplication> mockedStatic = mockStatic(SpringApplication.class)) {
            mockedStatic.when(() -> SpringApplication.run(any(Class.class), any(String[].class)))
                        .thenReturn(null);

            MaintenanceServiceApplication.main(new String[]{});

            mockedStatic.verify(() ->
                    SpringApplication.run(eq(MaintenanceServiceApplication.class), any(String[].class)));
        }
    }

}
