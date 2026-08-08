package it.f3rren.aquarium.species.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DevProfileGuardTest {

    private final DevProfileGuard guard = new DevProfileGuard();

    @Test
    void doesNothingWhenDevProfileIsNotActive() {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles("prod");

        assertThatCode(() -> guard.postProcessEnvironment(environment, null)).doesNotThrowAnyException();
    }

    @Test
    void allowsDevProfileWhenDeploymentEnvironmentIsLocal() {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles("dev");
        environment.setProperty("app.deployment.environment", "local");

        assertThatCode(() -> guard.postProcessEnvironment(environment, null)).doesNotThrowAnyException();
    }

    @Test
    void refusesDevProfileWhenDeploymentEnvironmentMarkerIsMissing() {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles("dev");

        assertThatThrownBy(() -> guard.postProcessEnvironment(environment, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("dev")
                .hasMessageContaining("app.deployment.environment");
    }

    @Test
    void refusesDevProfileWhenDeploymentEnvironmentIsProduction() {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles("dev");
        environment.setProperty("app.deployment.environment", "production");

        assertThatThrownBy(() -> guard.postProcessEnvironment(environment, null))
                .isInstanceOf(IllegalStateException.class);
    }
}
