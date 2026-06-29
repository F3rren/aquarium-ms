package it.f3rren.aquarium.gateway.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import it.f3rren.aquarium.gateway.config.BotProtectionProperties;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class BotProtectionFilterTest {

    private BotProtectionProperties props;
    private BotProtectionFilter filter;

    @BeforeEach
    void setUp() {
        props = new BotProtectionProperties();
        filter = new BotProtectionFilter(props, new SimpleMeterRegistry());
    }

    private ServerWebExchange mockExchange(String path, String userAgent) {
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        ServerHttpResponse response = mock(ServerHttpResponse.class);

        when(exchange.getRequest()).thenReturn(request);
        lenient().when(exchange.getResponse()).thenReturn(response);
        when(request.getURI()).thenReturn(URI.create("http://localhost" + path));

        HttpHeaders headers = new HttpHeaders();
        if (userAgent != null) {
            headers.set("User-Agent", userAgent);
        }
        lenient().when(request.getHeaders()).thenReturn(headers);
        return exchange;
    }

    @Nested
    class WhenDisabled {

        @Test
        void delegatesToChainWithoutChecking() {
            props.setEnabled(false);
            GatewayFilterChain chain = mock(GatewayFilterChain.class);
            ServerWebExchange exchange = mock(ServerWebExchange.class);
            when(chain.filter(exchange)).thenReturn(Mono.empty());

            StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
            verify(chain).filter(exchange);
        }
    }

    @Nested
    class WhenEnabled {

        @Test
        void allowsRequestOnExcludedPath() {
            GatewayFilterChain chain = mock(GatewayFilterChain.class);
            ServerWebExchange exchange = mockExchange("/actuator/health", null);
            when(chain.filter(exchange)).thenReturn(Mono.empty());

            StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
            verify(chain).filter(exchange);
        }

        @Test
        void blocksMissingUserAgent() {
            GatewayFilterChain chain = mock(GatewayFilterChain.class);
            ServerWebExchange exchange = mockExchange("/api/aquariums", null);
            when(exchange.getResponse().setComplete()).thenReturn(Mono.empty());

            StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
            verify(exchange.getResponse()).setStatusCode(HttpStatus.FORBIDDEN);
            verify(chain, never()).filter(exchange);
        }

        @Test
        void blocksBlankUserAgent() {
            GatewayFilterChain chain = mock(GatewayFilterChain.class);
            ServerWebExchange exchange = mockExchange("/api/aquariums", "   ");
            when(exchange.getResponse().setComplete()).thenReturn(Mono.empty());

            StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
            verify(exchange.getResponse()).setStatusCode(HttpStatus.FORBIDDEN);
        }

        @Test
        void blocksKnownBotUserAgent() {
            GatewayFilterChain chain = mock(GatewayFilterChain.class);
            ServerWebExchange exchange = mockExchange("/api/aquariums", "python-requests/2.28.0");
            when(exchange.getResponse().setComplete()).thenReturn(Mono.empty());

            StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
            verify(exchange.getResponse()).setStatusCode(HttpStatus.FORBIDDEN);
        }

        @Test
        void allowsLegitimateUserAgent() {
            GatewayFilterChain chain = mock(GatewayFilterChain.class);
            ServerWebExchange exchange = mockExchange("/api/aquariums", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            when(chain.filter(exchange)).thenReturn(Mono.empty());

            StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
            verify(chain).filter(exchange);
        }
    }

    @Nested
    class GetOrder {

        @Test
        void returnsHighestPrecedencePlusOne() {
            assertThat(filter.getOrder()).isEqualTo(Ordered.HIGHEST_PRECEDENCE + 1);
        }
    }
}
