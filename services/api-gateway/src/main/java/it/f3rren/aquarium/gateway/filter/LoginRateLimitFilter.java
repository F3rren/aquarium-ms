package it.f3rren.aquarium.gateway.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import it.f3rren.aquarium.gateway.config.LoginRateLimitProperties;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Brute-force protection for {@code POST /auth/login}, keyed per client IP. Deliberately a plain
 * {@link WebFilter}, not a {@link org.springframework.cloud.gateway.filter.GlobalFilter} like
 * {@link RateLimitingFilter}: a {@code GlobalFilter} only wraps requests the gateway actually
 * routes to a backend, and {@code /auth/login} is handled locally by {@code AuthController} -
 * it never goes through that chain, so {@link RateLimitingFilter} never sees it no matter how
 * it's configured. A {@link WebFilter} runs for every request the reactive server receives,
 * routed or locally dispatched, which is what a login endpoint actually needs.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoginRateLimitFilter implements WebFilter {

    private static final String LOGIN_PATH = "/auth/login";

    private record BucketEntry(Bucket bucket, long[] lastAccessNano) {
    }

    private final LoginRateLimitProperties props;
    private final ConcurrentHashMap<String, BucketEntry> bucketsByIp = new ConcurrentHashMap<>();

    public LoginRateLimitFilter(LoginRateLimitProperties props) {
        this.props = props;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (!props.isEnabled() || !LOGIN_PATH.equals(exchange.getRequest().getURI().getPath())) {
            return chain.filter(exchange);
        }

        String ip = extractClientIp(exchange);
        BucketEntry entry = bucketsByIp.computeIfAbsent(ip, k -> new BucketEntry(createBucket(), new long[]{System.nanoTime()}));
        entry.lastAccessNano()[0] = System.nanoTime();

        if (entry.bucket().tryConsume(1)) {
            return chain.filter(exchange);
        }

        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        exchange.getResponse().getHeaders().add("Retry-After", String.valueOf(props.getRefillSeconds()));
        return exchange.getResponse().setComplete();
    }

    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.classic(props.getCapacity(),
                Refill.greedy(props.getRefillTokens(), Duration.ofSeconds(props.getRefillSeconds())));
        return Bucket.builder().addLimit(limit).build();
    }

    private String extractClientIp(ServerWebExchange exchange) {
        InetSocketAddress addr = exchange.getRequest().getRemoteAddress();
        return addr != null ? addr.getAddress().getHostAddress() : "unknown";
    }

    @Scheduled(fixedRateString = "${auth.rate-limit.cleanup-interval-ms:300000}")
    public void evictStaleBuckets() {
        long ttlNanos = Duration.ofMinutes(props.getBucketTtlMinutes()).toNanos();
        long now = System.nanoTime();
        bucketsByIp.entrySet().removeIf(e -> (now - e.getValue().lastAccessNano()[0]) > ttlNanos);
    }
}
