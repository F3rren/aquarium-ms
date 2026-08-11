package it.f3rren.aquarium.gateway.controller;

import it.f3rren.aquarium.gateway.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Minimal login endpoint for the two fixed test identities used to exercise Sentinel's IDOR
 * module: plaintext-compares a username/password pair against env-configured test credentials
 * and issues a JWT carrying the matching user id as its subject. Deliberately not a general
 * user-management system - password hashing and self-service registration would be pointless
 * for two fixed seed accounts whose entire purpose is to be known, reproducible test identities.
 * <p>
 * Also exposes {@link #listUsers}, an intentional BFLA test fixture - see its own javadoc.
 */
@RestController
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

    @PostMapping("/auth/login")
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

    /**
     * Lists every known test identity (username + id). Reachable by any caller holding a valid
     * JWT - checked here explicitly via {@link JwtTokenProvider#validateAndGetSubject}, the same
     * way {@link it.f3rren.aquarium.gateway.filter.JwtAuthFilter} does for routed traffic,
     * because that filter is a {@code GlobalFilter} and never sees locally-handled controller
     * endpoints such as this one (the same reason {@code /auth/login} needs its own
     * {@link it.f3rren.aquarium.gateway.filter.LoginRateLimitFilter} instead of relying on the
     * gateway's general rate-limit filter).
     * <p>
     * Deliberately does not check whether that identity is actually privileged to administer
     * users - any authenticated identity, not just a genuine admin, can list them. This is an
     * intentional BFLA (Broken Function Level Authorization) test fixture: the path looks like
     * it should require an elevated role ("admin"), but nothing beyond ordinary authentication
     * is enforced, so a security scan can demonstrate the difference between an endpoint that's
     * merely authenticated and one that's actually authorized for the function it exposes.
     */
    @GetMapping("/admin/users")
    public ResponseEntity<Map<String, Object>> listUsers(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        if (authHeader == null || !authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "missing_bearer_token"));
        }
        Optional<String> subject = jwtTokenProvider.validateAndGetSubject(authHeader.substring(7));
        if (subject.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "invalid_or_expired_token"));
        }
        List<Map<String, String>> users = userIdsByUsername.entrySet().stream()
                .map(entry -> Map.of("username", entry.getKey(), "id", entry.getValue()))
                .toList();
        return ResponseEntity.ok(Map.of("users", users));
    }

    public record LoginRequest(String username, String password) {
    }
}
