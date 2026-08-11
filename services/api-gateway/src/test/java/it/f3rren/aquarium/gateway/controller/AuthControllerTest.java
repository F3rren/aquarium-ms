package it.f3rren.aquarium.gateway.controller;

import it.f3rren.aquarium.gateway.config.JwtProperties;
import it.f3rren.aquarium.gateway.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AuthControllerTest {

    private AuthController controller;
    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties();
        props.setSecret("test-secret");
        tokenProvider = new JwtTokenProvider(props);
        controller = new AuthController(tokenProvider, "userA", "passwordA", "userB", "passwordB");
    }

    @Test
    void issuesATokenForValidCredentials() {
        Mono<AuthController.LoginRequest> request = Mono.just(new AuthController.LoginRequest("userA", "passwordA"));

        StepVerifier.create(controller.login(request))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(response.getBody()).isNotNull();
                    assertThat(response.getBody().get("userId")).isEqualTo("1");
                    assertThat(tokenProvider.validateAndGetSubject(response.getBody().get("token"))).contains("1");
                })
                .verifyComplete();
    }

    @Test
    void rejectsWrongPassword() {
        Mono<AuthController.LoginRequest> request = Mono.just(new AuthController.LoginRequest("userA", "wrong"));

        StepVerifier.create(controller.login(request))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
                    assertThat(response.getBody()).containsEntry("error", "invalid_credentials");
                })
                .verifyComplete();
    }

    @Test
    void rejectsAnUnknownUsername() {
        Mono<AuthController.LoginRequest> request = Mono.just(new AuthController.LoginRequest("nobody", "whatever"));

        StepVerifier.create(controller.login(request))
                .assertNext(response -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED))
                .verifyComplete();
    }

    @Test
    void theSecondTestUserGetsADifferentUserId() {
        Mono<AuthController.LoginRequest> request = Mono.just(new AuthController.LoginRequest("userB", "passwordB"));

        StepVerifier.create(controller.login(request))
                .assertNext(response -> assertThat(response.getBody()).containsEntry("userId", "2"))
                .verifyComplete();
    }

    @Test
    void listUsersRejectsAMissingBearerToken() {
        var response = controller.listUsers(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).containsEntry("error", "missing_bearer_token");
    }

    @Test
    void listUsersRejectsAMalformedAuthorizationHeader() {
        var response = controller.listUsers("not-a-bearer-token");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void listUsersRejectsAnInvalidToken() {
        var response = controller.listUsers("Bearer garbage");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).containsEntry("error", "invalid_or_expired_token");
    }

    @Test
    void listUsersReturnsBothTestIdentitiesForAnyValidToken() {
        // No role check at all: a token for the ordinary userA identity is enough - this is the
        // intentional BFLA fixture's whole point (see AuthController#listUsers).
        String token = tokenProvider.issueToken("1");

        var response = controller.listUsers("Bearer " + token);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        @SuppressWarnings("unchecked")
        var users = (List<Map<String, String>>) response.getBody().get("users");
        assertThat(users).containsExactlyInAnyOrder(
                Map.of("username", "userA", "id", "1"),
                Map.of("username", "userB", "id", "2")
        );
    }
}
