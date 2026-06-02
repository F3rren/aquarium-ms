package it.f3rren.aquarium.gateway.filter;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import it.f3rren.aquarium.gateway.config.BotProtectionProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class BotProtectionFilter implements GlobalFilter, Ordered {

    public static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 1;

    private final BotProtectionProperties props;
    private final Counter blockedBotsCounter;

    public BotProtectionFilter(BotProtectionProperties props, MeterRegistry meterRegistry) {
        this.props = props;
        this.blockedBotsCounter = Counter.builder("gateway.bot_protection.blocked_total")
                .description("Total requests blocked by bot protection")
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

        String userAgent = exchange.getRequest().getHeaders().getFirst("User-Agent");

        if (userAgent == null || userAgent.isBlank()) {
            return blockRequest(exchange, "missing-user-agent");
        }

        String uaLower = userAgent.toLowerCase();
        boolean isKnownBot = props.getBlockedUserAgentPatterns().stream()
                .anyMatch(pattern -> uaLower.contains(pattern.toLowerCase()));
        if (isKnownBot) {
            return blockRequest(exchange, "known-bot-user-agent");
        }

        return chain.filter(exchange);
    }

    private Mono<Void> blockRequest(ServerWebExchange exchange, String reason) {
        blockedBotsCounter.increment();
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().add("X-Blocked-Reason", reason);
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
