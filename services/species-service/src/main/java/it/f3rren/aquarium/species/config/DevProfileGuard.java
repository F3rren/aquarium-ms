package it.f3rren.aquarium.species.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Refuses to start the application if the {@code dev} Spring profile is active outside of a
 * developer's own machine. The dev profile relaxes several production safeguards - Flyway
 * validation disabled, {@code ddl-auto=update}, verbose error messages, unauthenticated health
 * details, debug logging - that must never reach a shared or production deployment.
 * <p>
 * {@code application-dev.properties} sets {@code app.deployment.environment=local}; the base
 * {@code application.properties} defaults it to {@code production}. So the only way to satisfy
 * this guard is to genuinely have the dev profile's own properties loaded: accidentally setting
 * {@code SPRING_PROFILES_ACTIVE=dev} on a shared or production deployment (which does not also
 * carry that override) fails fast here, before any bean - including the datasource - is created.
 * <p>
 * Runs as an {@link EnvironmentPostProcessor} (registered via
 * {@code META-INF/spring/org.springframework.boot.env.EnvironmentPostProcessor.imports}) rather
 * than an ordinary bean so the check happens before context refresh, not after Flyway/Hikari
 * have already touched a database that might not even be a local one.
 */
public class DevProfileGuard implements EnvironmentPostProcessor {

    private static final String DEV_PROFILE = "dev";
    private static final String DEPLOYMENT_ENVIRONMENT_PROPERTY = "app.deployment.environment";
    private static final String LOCAL_DEPLOYMENT_ENVIRONMENT = "local";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (!environment.matchesProfiles(DEV_PROFILE)) {
            return;
        }
        String deploymentEnvironment = environment.getProperty(DEPLOYMENT_ENVIRONMENT_PROPERTY, "production");
        if (!LOCAL_DEPLOYMENT_ENVIRONMENT.equalsIgnoreCase(deploymentEnvironment)) {
            throw new IllegalStateException(
                    "Refusing to start: the 'dev' Spring profile is active but "
                    + DEPLOYMENT_ENVIRONMENT_PROPERTY + "='" + deploymentEnvironment + "' (expected '"
                    + LOCAL_DEPLOYMENT_ENVIRONMENT + "'). The dev profile disables Flyway validation, "
                    + "relaxes ddl-auto to 'update', and exposes verbose errors and health details - it "
                    + "must only run on a developer's own machine with application-dev.properties on the "
                    + "classpath, never on a shared or production deployment.");
        }
    }
}
