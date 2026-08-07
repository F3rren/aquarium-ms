package it.f3rren.aquarium.gateway.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import it.f3rren.aquarium.gateway.config.RateLimitProperties;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RateLimitingFilterTest {

    private RateLimitProperties props;
    private RateLimitingFilter filter;

    @BeforeEach
    void setUp() {
        props = new RateLimitProperties();
        filter = new RateLimitingFilter(props, new SimpleMeterRegistry());
    }

    private ServerWebExchange mockExchange(String path, HttpMethod method, String ip) {
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        org.springframework.http.HttpHeaders responseHeaders = new org.springframework.http.HttpHeaders();

        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        when(request.getURI()).thenReturn(URI.create("http://localhost" + path));
        when(request.getMethod()).thenReturn(method);
        when(response.getHeaders()).thenReturn(responseHeaders);

        if (ip != null) {
            try {
                InetSocketAddress addr = new InetSocketAddress(InetAddress.getByName(ip), 0);
                when(request.getRemoteAddress()).thenReturn(addr);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            when(request.getRemoteAddress()).thenReturn(null);
        }
        return exchange;
    }

    @Nested
    class WhenDisabled {

        @Test
        void delegatesToChain() {
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
            ServerWebExchange exchange = mock(ServerWebExchange.class);
            ServerHttpRequest request = mock(ServerHttpRequest.class);

            when(exchange.getRequest()).thenReturn(request);
            when(request.getURI()).thenReturn(java.net.URI.create("http://localhost/actuator/health"));
            when(chain.filter(exchange)).thenReturn(Mono.empty());

            StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
            verify(chain).filter(exchange);
        }

        @Test
        void allowsGetRequestWithinLimit() {
            GatewayFilterChain chain = mock(GatewayFilterChain.class);
            ServerWebExchange exchange = mockExchange("/api/aquariums", HttpMethod.GET, "10.0.0.2");
            when(chain.filter(exchange)).thenReturn(Mono.empty());

            StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
            verify(chain).filter(exchange);
            assertThat(exchange.getResponse().getHeaders().getFirst("X-RateLimit-Limit"))
                    .isEqualTo(String.valueOf(props.getGeneralCapacity()));
        }

        @Test
        void allowsPostRequestWithinLimit() {
            GatewayFilterChain chain = mock(GatewayFilterChain.class);
            ServerWebExchange exchange = mockExchange("/api/aquariums", HttpMethod.POST, "10.0.0.3");
            when(chain.filter(exchange)).thenReturn(Mono.empty());

            StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
            verify(chain).filter(exchange);
            assertThat(exchange.getResponse().getHeaders().getFirst("X-RateLimit-Limit"))
                    .isEqualTo(String.valueOf(props.getWriteCapacity()));
        }

        @Test
        void rejectsGetRequestOverLimit() {
            props.setGeneralCapacity(1);
            props.setGeneralRefillTokens(1);
            props.setGeneralRefillSeconds(60);
            filter = new RateLimitingFilter(props, new SimpleMeterRegistry());

            GatewayFilterChain chain = mock(GatewayFilterChain.class);
            ServerWebExchange first = mockExchange("/api/aquariums", HttpMethod.GET, "10.0.0.4");
            ServerWebExchange second = mockExchange("/api/aquariums", HttpMethod.GET, "10.0.0.4");
            when(chain.filter(first)).thenReturn(Mono.empty());
            when(second.getResponse().setComplete()).thenReturn(Mono.empty());

            StepVerifier.create(filter.filter(first, chain)).verifyComplete();
            StepVerifier.create(filter.filter(second, chain)).verifyComplete();

            verify(second.getResponse()).setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            verify(chain, times(1)).filter(any());
        }

        @Test
        void rejectsWriteRequestOverLimit() {
            props.setWriteCapacity(1);
            props.setWriteRefillTokens(1);
            props.setWriteRefillSeconds(60);
            filter = new RateLimitingFilter(props, new SimpleMeterRegistry());

            GatewayFilterChain chain = mock(GatewayFilterChain.class);
            ServerWebExchange first = mockExchange("/api/aquariums", HttpMethod.POST, "10.0.0.5");
            ServerWebExchange second = mockExchange("/api/aquariums", HttpMethod.POST, "10.0.0.5");
            when(chain.filter(first)).thenReturn(Mono.empty());
            when(second.getResponse().setComplete()).thenReturn(Mono.empty());

            StepVerifier.create(filter.filter(first, chain)).verifyComplete();
            StepVerifier.create(filter.filter(second, chain)).verifyComplete();

            verify(second.getResponse()).setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        }

        @Test
        void usesUnknownWhenRemoteAddressIsNull() {
            GatewayFilterChain chain = mock(GatewayFilterChain.class);
            ServerWebExchange exchange = mockExchange("/api/aquariums", HttpMethod.GET, null);
            when(chain.filter(exchange)).thenReturn(Mono.empty());

            StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
            verify(chain).filter(exchange);
        }
    }

    @Nested
    class EvictStaleBuckets {

        @Test
        void removesNothingWhenBucketsAreFresh() {
            filter.evictStaleBuckets();
        }
    }

    @Nested
    class GetOrder {

        @Test
        void returnsHighestPrecedencePlusTwo() {
            assertThat(filter.getOrder()).isEqualTo(Ordered.HIGHEST_PRECEDENCE + 2);
        }
    }
}
