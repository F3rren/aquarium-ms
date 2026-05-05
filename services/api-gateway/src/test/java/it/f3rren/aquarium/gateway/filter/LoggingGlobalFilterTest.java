package it.f3rren.aquarium.gateway.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.net.InetSocketAddress;
import java.net.URI;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class LoggingGlobalFilterTest {

    @InjectMocks
    private LoggingGlobalFilter filter;

    @Nested
    class Filter {

        @Test
        void completesSuccessfullyAndDelegatesChain() {
            ServerWebExchange exchange = mock(ServerWebExchange.class);
            GatewayFilterChain chain = mock(GatewayFilterChain.class);
            ServerHttpRequest request = mock(ServerHttpRequest.class);
            ServerHttpResponse response = mock(ServerHttpResponse.class);

            when(exchange.getRequest()).thenReturn(request);
            when(exchange.getResponse()).thenReturn(response);
            when(request.getMethod()).thenReturn(HttpMethod.GET);
            when(request.getURI()).thenReturn(URI.create("http://localhost/api/aquariums"));
            when(request.getRemoteAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 8080));
            when(response.getStatusCode()).thenReturn(HttpStatus.OK);
            when(chain.filter(exchange)).thenReturn(Mono.empty());

            StepVerifier.create(filter.filter(exchange, chain))
                    .verifyComplete();

            verify(chain).filter(exchange);
        }

        @Test
        void handlesNullRemoteAddress() {
            ServerWebExchange exchange = mock(ServerWebExchange.class);
            GatewayFilterChain chain = mock(GatewayFilterChain.class);
            ServerHttpRequest request = mock(ServerHttpRequest.class);
            ServerHttpResponse response = mock(ServerHttpResponse.class);

            when(exchange.getRequest()).thenReturn(request);
            when(exchange.getResponse()).thenReturn(response);
            when(request.getMethod()).thenReturn(HttpMethod.POST);
            when(request.getURI()).thenReturn(URI.create("http://localhost/api/aquariums"));
            when(request.getRemoteAddress()).thenReturn(null);
            when(response.getStatusCode()).thenReturn(HttpStatus.CREATED);
            when(chain.filter(exchange)).thenReturn(Mono.empty());

            StepVerifier.create(filter.filter(exchange, chain))
                    .verifyComplete();
        }

        @Test
        void handlesNullResponseStatus() {
            ServerWebExchange exchange = mock(ServerWebExchange.class);
            GatewayFilterChain chain = mock(GatewayFilterChain.class);
            ServerHttpRequest request = mock(ServerHttpRequest.class);
            ServerHttpResponse response = mock(ServerHttpResponse.class);

            when(exchange.getRequest()).thenReturn(request);
            when(exchange.getResponse()).thenReturn(response);
            when(request.getMethod()).thenReturn(HttpMethod.DELETE);
            when(request.getURI()).thenReturn(URI.create("http://localhost/api/aquariums/1"));
            when(request.getRemoteAddress()).thenReturn(null);
            when(response.getStatusCode()).thenReturn(null);
            when(chain.filter(exchange)).thenReturn(Mono.empty());

            StepVerifier.create(filter.filter(exchange, chain))
                    .verifyComplete();
        }
    }

    @Nested
    class GetOrder {

        @Test
        void returnsHighestPrecedence() {
            assertThat(filter.getOrder()).isEqualTo(Ordered.HIGHEST_PRECEDENCE);
        }
    }
}
