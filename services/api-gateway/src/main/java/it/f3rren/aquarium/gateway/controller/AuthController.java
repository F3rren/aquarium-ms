package it.f3rren.aquarium.gateway.controller;

import it.f3rren.aquarium.gateway.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Minimal login endpoint for the two fixed test identities used to exercise Sentinel's IDOR
 * module: plaintext-compares a username/password pair against env-configured test credentials
 * and issues a JWT carrying the matching user id as its subject. Deliberately not a general
 * user-management system - password hashing and self-service registration would be pointless
 * for two fixed seed accounts whose entire purpose is to be known, reproducible test identities.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final Map<String, String> passwordsByUsername;
    private final Map<String, String> userIdsByUsername;

    public AuthController(
            JwtTokenProvider jwtTokenProvider,
            @Value("${auth.test-users.a.username:userA}") String usernameA,
            @Value("${auth.test-users.a.password}") String passwordA,
            @Value("${auth.test-users.b.username:userB}") String usernameB,
            @Value("${auth.test-users.b.password}") String passwordB
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordsByUsername = Map.of(usernameA, passwordA, usernameB, passwordB);
        this.userIdsByUsername = Map.of(usernameA, "1", usernameB, "2");
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, String>>> login(@RequestBody Mono<LoginRequest> requestBody) {
        return requestBody.map(this::authenticate);
    }

    private ResponseEntity<Map<String, String>> authenticate(LoginRequest request) {
        String expectedPassword = passwordsByUsername.get(request.username());
        if (expectedPassword == null || !expectedPassword.equals(request.password())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "invalid_credentials"));
        }
        String userId = userIdsByUsername.get(request.username());
        String token = jwtTokenProvider.issueToken(userId);
        return ResponseEntity.ok(Map.of("token", token, "userId", userId));
    }

    public record LoginRequest(String username, String password) {
    }
}
