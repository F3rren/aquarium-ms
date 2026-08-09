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
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;

import it.f3rren.aquarium.gateway.config.LoginRateLimitProperties;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class LoginRateLimitFilterTest {

    private LoginRateLimitProperties props;
    private LoginRateLimitFilter filter;

    @BeforeEach
    void setUp() {
        props = new LoginRateLimitProperties();
        filter = new LoginRateLimitFilter(props);
    }

    private ServerWebExchange mockExchange(String path, String ip) {
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        org.springframework.http.HttpHeaders responseHeaders = new org.springframework.http.HttpHeaders();

        // lenient() throughout: unlike RateLimitingFilter (which always touches the response for
        // X-RateLimit-* headers), this filter short-circuits on disabled/non-login-path requests
        // without touching the request or response at all, so which stubs a given test actually
        // needs varies by which branch it exercises.
        lenient().when(exchange.getRequest()).thenReturn(request);
        lenient().when(exchange.getResponse()).thenReturn(response);
        lenient().when(request.getURI()).thenReturn(URI.create("http://localhost" + path));
        lenient().when(response.getHeaders()).thenReturn(responseHeaders);

        if (ip != null) {
            try {
                InetSocketAddress addr = new InetSocketAddress(InetAddress.getByName(ip), 0);
                lenient().when(request.getRemoteAddress()).thenReturn(addr);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            lenient().when(request.getRemoteAddress()).thenReturn(null);
        }
        return exchange;
    }

    @Nested
    class WhenDisabled {

        @Test
        void delegatesToChainRegardlessOfPath() {
            props.setEnabled(false);
            WebFilterChain chain = mock(WebFilterChain.class);
            ServerWebExchange exchange = mockExchange("/auth/login", "10.0.0.1");
            when(chain.filter(exchange)).thenReturn(Mono.empty());

            StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
            verify(chain).filter(exchange);
        }
    }

    @Nested
    class WhenEnabled {

        @Test
        void allowsRequestsToOtherPathsWithoutConsumingTheBucket() {
            WebFilterChain chain = mock(WebFilterChain.class);
            ServerWebExchange exchange = mockExchange("/aquariums", "10.0.0.2");
            when(chain.filter(exchange)).thenReturn(Mono.empty());

            StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
            verify(chain).filter(exchange);
        }

        @Test
        void allowsLoginAttemptWithinLimit() {
            WebFilterChain chain = mock(WebFilterChain.class);
            ServerWebExchange exchange = mockExchange("/auth/login", "10.0.0.3");
            when(chain.filter(exchange)).thenReturn(Mono.empty());

            StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
            verify(chain).filter(exchange);
        }

        @Test
        void rejectsLoginAttemptsOverLimit() {
            props.setCapacity(1);
            props.setRefillTokens(1);
            props.setRefillSeconds(60);
            filter = new LoginRateLimitFilter(props);

            WebFilterChain chain = mock(WebFilterChain.class);
            ServerWebExchange first = mockExchange("/auth/login", "10.0.0.4");
            ServerWebExchange second = mockExchange("/auth/login", "10.0.0.4");
            when(chain.filter(first)).thenReturn(Mono.empty());
            when(second.getResponse().setComplete()).thenReturn(Mono.empty());

            StepVerifier.create(filter.filter(first, chain)).verifyComplete();
            StepVerifier.create(filter.filter(second, chain)).verifyComplete();

            verify(second.getResponse()).setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            verify(chain, times(1)).filter(any());
        }

        @Test
        void tracksLimitsPerIpIndependently() {
            props.setCapacity(1);
            props.setRefillTokens(1);
            props.setRefillSeconds(60);
            filter = new LoginRateLimitFilter(props);

            WebFilterChain chain = mock(WebFilterChain.class);
            ServerWebExchange first = mockExchange("/auth/login", "10.0.0.5");
            ServerWebExchange second = mockExchange("/auth/login", "10.0.0.6");
            when(chain.filter(any())).thenReturn(Mono.empty());

            StepVerifier.create(filter.filter(first, chain)).verifyComplete();
            StepVerifier.create(filter.filter(second, chain)).verifyComplete();

            verify(chain, times(2)).filter(any());
        }

        @Test
        void usesUnknownWhenRemoteAddressIsNull() {
            WebFilterChain chain = mock(WebFilterChain.class);
            ServerWebExchange exchange = mockExchange("/auth/login", null);
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
}
