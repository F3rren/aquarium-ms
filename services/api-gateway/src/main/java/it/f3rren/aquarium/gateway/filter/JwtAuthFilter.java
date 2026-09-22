package it.f3rren.aquarium.gateway.filter;

import it.f3rren.aquarium.gateway.config.JwtProperties;
import it.f3rren.aquarium.gateway.security.JwtTokenProvider;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Deny-by-default authentication at the edge: every route requires a valid
 * {@code Authorization:
 * Bearer <jwt>} unless its path is explicitly excluded (login itself, actuator,
 * swagger/docs -
 * Sentinel's own discovery phase must stay anonymous even once a target
 * requires auth for the
 * actual attack phase). This is the only place authentication is enforced -
 * none of the seven
 * downstream services validate a token themselves; they trust the
 * {@code X-User-Id} header this
 * filter injects, which is safe only because they are reachable exclusively
 * through this gateway
 * on the Docker network, never published on a host port directly.
 * <p>
 * Deliberately does NOT check resource ownership - only "is this caller
 * authenticated at all".
 * Whether an authenticated identity is authorized on the *specific* resource
 * it's requesting is
 * each downstream service's own concern (see aquariums-service, which enforces
 * it on write
 * endpoints but not on {@code GET /aquariums/{id}} - the intentional IDOR test
 * fixture).
 */
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    public static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 3;

    private final JwtProperties props;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthFilter(JwtProperties props, JwtTokenProvider jwtTokenProvider) {
        this.props = props;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (isExcluded(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return reject(exchange, "missing-bearer-token");
        }

        String token = authHeader.substring(7);
        Optional<String> userId = jwtTokenProvider.validateAndGetSubject(token);
        if (userId.isEmpty()) {
            return reject(exchange, "invalid-or-expired-token");
        }

        // Never trust a client-supplied X-User-Id - strip it before setting our own, so
        // a
        // caller cannot impersonate another user by simply adding the header itself.
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .headers(headers -> headers.remove("X-User-Id"))
                .header("X-User-Id", userId.get())
                .build();
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private Mono<Void> reject(ServerWebExchange exchange, String reason) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("X-Auth-Failure-Reason", reason);
        return exchange.getResponse().setComplete();
    }

    private boolean isExcluded(String path) {
        return props.getExcludedPaths().stream().anyMatch(path::contains);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
