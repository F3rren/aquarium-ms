package it.f3rren.aquarium.gateway.controller;

import it.f3rren.aquarium.gateway.config.JwtProperties;
import it.f3rren.aquarium.gateway.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
}
