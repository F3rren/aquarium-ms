package it.f3rren.aquarium.gateway.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class SecurityHeadersFilterTest {

    @InjectMocks
    private SecurityHeadersFilter filter;

    @Nested
    class Filter {

        @Test
        void addsAllSecurityHeaders() {
            ServerWebExchange exchange = mock(ServerWebExchange.class);
            GatewayFilterChain chain = mock(GatewayFilterChain.class);
            ServerHttpResponse response = mock(ServerHttpResponse.class);
            HttpHeaders headers = new HttpHeaders();

            when(exchange.getResponse()).thenReturn(response);
            when(response.getHeaders()).thenReturn(headers);
            when(chain.filter(exchange)).thenReturn(Mono.empty());

            StepVerifier.create(filter.filter(exchange, chain))
                    .verifyComplete();

            assertThat(headers.getFirst("X-Content-Type-Options")).isEqualTo("nosniff");
            assertThat(headers.getFirst("X-Frame-Options")).isEqualTo("DENY");
            assertThat(headers.getFirst("Referrer-Policy")).isEqualTo("strict-origin-when-cross-origin");
            assertThat(headers.getFirst("Permissions-Policy")).isEqualTo("geolocation=(), microphone=(), camera=()");
        }

        @Test
        void delegatesToChain() {
            ServerWebExchange exchange = mock(ServerWebExchange.class);
            GatewayFilterChain chain = mock(GatewayFilterChain.class);
            ServerHttpResponse response = mock(ServerHttpResponse.class);

            when(exchange.getResponse()).thenReturn(response);
            when(response.getHeaders()).thenReturn(new HttpHeaders());
            when(chain.filter(exchange)).thenReturn(Mono.empty());

            StepVerifier.create(filter.filter(exchange, chain))
                    .verifyComplete();

            verify(chain).filter(exchange);
        }
    }

    @Nested
    class GetOrder {

        @Test
        void returnsLowestPrecedence() {
            assertThat(filter.getOrder()).isEqualTo(Ordered.LOWEST_PRECEDENCE);
        }
    }
}
