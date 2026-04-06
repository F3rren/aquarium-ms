package it.f3rren.aquarium.gateway.filter;

import com.github.bucket4j.Bandwidth;
import com.github.bucket4j.Bucket;
import com.github.bucket4j.Refill;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import it.f3rren.aquarium.gateway.config.RateLimitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter implements GlobalFilter, Ordered {

    public static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 2;

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

    private static final Set<HttpMethod> WRITE_METHODS = Set.of(
            HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.PATCH
    );

    private record BucketEntry(Bucket bucket, long[] lastAccessNano) {}

    private final ConcurrentHashMap<String, BucketEntry> generalBuckets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, BucketEntry> writeBuckets = new ConcurrentHashMap<>();

    private final RateLimitProperties props;
    private final Counter rateLimitedCounter;

    public RateLimitingFilter(RateLimitProperties props, MeterRegistry meterRegistry) {
        this.props = props;
        this.rateLimitedCounter = Counter.builder("gateway.rate_limit.throttled_total")
                .description("Total requests throttled by rate limiting")
                .register(meterRegistry);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!props.isEnabled()) {
            return chain.filter(exchange);
        }

        String path = exchange.getRequest().getURI().getPath();
        if (isExcluded(path)) {
            return chain.filter(exchange);
        }

        String ip = extractClientIp(exchange);
        boolean isWrite = WRITE_METHODS.contains(exchange.getRequest().getMethod());

        ConcurrentHashMap<String, BucketEntry> bucketMap = isWrite ? writeBuckets : generalBuckets;
        BucketEntry entry = bucketMap.computeIfAbsent(ip, k -> createEntry(isWrite));
        entry.lastAccessNano()[0] = System.nanoTime();

        if (entry.bucket().tryConsume(1)) {
            long remaining = entry.bucket().getAvailableTokens();
            long capacity = isWrite ? props.getWriteCapacity() : props.getGeneralCapacity();
            exchange.getResponse().getHeaders().add("X-RateLimit-Limit", String.valueOf(capacity));
            exchange.getResponse().getHeaders().add("X-RateLimit-Remaining", String.valueOf(remaining));
            return chain.filter(exchange);
        } else {
            return rejectRequest(exchange, isWrite);
        }
    }

    private Mono<Void> rejectRequest(ServerWebExchange exchange, boolean isWrite) {
        rateLimitedCounter.increment();
        int retryAfter = isWrite ? props.getWriteRefillSeconds() : props.getGeneralRefillSeconds();
        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        exchange.getResponse().getHeaders().add("Retry-After", String.valueOf(retryAfter));
        exchange.getResponse().getHeaders().add("X-RateLimit-Retry-After-Seconds", String.valueOf(retryAfter));
        return exchange.getResponse().setComplete();
    }

    private BucketEntry createEntry(boolean isWrite) {
        Bandwidth limit;
        if (isWrite) {
            limit = Bandwidth.classic(
                    props.getWriteCapacity(),
                    Refill.greedy(props.getWriteRefillTokens(), Duration.ofSeconds(props.getWriteRefillSeconds()))
            );
        } else {
            limit = Bandwidth.classic(
                    props.getGeneralCapacity(),
                    Refill.greedy(props.getGeneralRefillTokens(), Duration.ofSeconds(props.getGeneralRefillSeconds()))
            );
        }
        return new BucketEntry(Bucket.builder().addLimit(limit).build(), new long[]{System.nanoTime()});
    }

    private String extractClientIp(ServerWebExchange exchange) {
        String forwarded = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        InetSocketAddress addr = exchange.getRequest().getRemoteAddress();
        return addr != null ? addr.getAddress().getHostAddress() : "unknown";
    }

    private boolean isExcluded(String path) {
        return props.getExcludedPaths().stream().anyMatch(path::contains);
    }

    @Scheduled(fixedRateString = "${rate-limit.cleanup-interval-ms:300000}")
    public void evictStaleBuckets() {
        long ttlNanos = Duration.ofMinutes(props.getBucketTtlMinutes()).toNanos();
        long now = System.nanoTime();
        int removedGeneral = 0;
        int removedWrite = 0;

        var genIt = generalBuckets.entrySet().iterator();
        while (genIt.hasNext()) {
            if ((now - genIt.next().getValue().lastAccessNano()[0]) > ttlNanos) {
                genIt.remove();
                removedGeneral++;
            }
        }
        var writeIt = writeBuckets.entrySet().iterator();
        while (writeIt.hasNext()) {
            if ((now - writeIt.next().getValue().lastAccessNano()[0]) > ttlNanos) {
                writeIt.remove();
                removedWrite++;
            }
        }

        if (removedGeneral > 0 || removedWrite > 0) {
            logger.debug("Rate limit bucket cleanup: removed {} general, {} write entries", removedGeneral, removedWrite);
        }
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
