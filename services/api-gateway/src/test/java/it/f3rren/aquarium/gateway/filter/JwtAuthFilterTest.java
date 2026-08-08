package it.f3rren.aquarium.gateway.filter;

import it.f3rren.aquarium.gateway.config.JwtProperties;
import it.f3rren.aquarium.gateway.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthFilterTest {

    private JwtTokenProvider tokenProvider;
    private JwtAuthFilter filter;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties();
        props.setSecret("test-secret-at-least-something");
        tokenProvider = new JwtTokenProvider(props);
        filter = new JwtAuthFilter(props, tokenProvider);
    }

    @Nested
    class ExcludedPaths {

        @Test
        void allowsAnExcludedPathWithoutAnyToken() {
            MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/auth/login").build());
            ServerWebExchange[] seen = new ServerWebExchange[1];

            StepVerifier.create(filter.filter(exchange, capturingChain(seen))).verifyComplete();

            assertThat(seen[0]).isNotNull();
            assertThat(exchange.getResponse().getStatusCode()).isNull();
        }
    }

    @Nested
    class MissingOrInvalidToken {

        @Test
        void rejectsARequestWithNoAuthorizationHeader() {
            MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/aquariums").build());

            StepVerifier.create(filter.filter(exchange, neverCalledChain())).verifyComplete();

            assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(exchange.getResponse().getHeaders().getFirst("X-Auth-Failure-Reason")).isEqualTo("missing-bearer-token");
        }

        @Test
        void rejectsANonBearerAuthorizationHeader() {
            MockServerWebExchange exchange = MockServerWebExchange.from(
                    MockServerHttpRequest.get("/aquariums").header("Authorization", "Basic dXNlcjpwYXNz").build());

            StepVerifier.create(filter.filter(exchange, neverCalledChain())).verifyComplete();

            assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        void rejectsAnInvalidToken() {
            MockServerWebExchange exchange = MockServerWebExchange.from(
                    MockServerHttpRequest.get("/aquariums").header("Authorization", "Bearer not-a-real-token").build());

            StepVerifier.create(filter.filter(exchange, neverCalledChain())).verifyComplete();

            assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(exchange.getResponse().getHeaders().getFirst("X-Auth-Failure-Reason")).isEqualTo("invalid-or-expired-token");
        }
    }

    @Nested
    class ValidToken {

        @Test
        void injectsTheTrustedUserIdHeaderAndStripsAnyClientSuppliedOne() {
            String token = tokenProvider.issueToken("42");
            MockServerWebExchange exchange = MockServerWebExchange.from(
                    MockServerHttpRequest.get("/aquariums")
                            .header("Authorization", "Bearer " + token)
                            .header("X-User-Id", "999") // attempted spoof of another identity
                            .build());
            ServerWebExchange[] seen = new ServerWebExchange[1];

            StepVerifier.create(filter.filter(exchange, capturingChain(seen))).verifyComplete();

            assertThat(seen[0]).isNotNull();
            assertThat(seen[0].getRequest().getHeaders().get("X-User-Id")).containsExactly("42");
        }
    }

    @Nested
    class GetOrder {

        @Test
        void returnsHighestPrecedencePlusThree() {
            assertThat(filter.getOrder()).isEqualTo(Ordered.HIGHEST_PRECEDENCE + 3);
        }
    }

    private GatewayFilterChain capturingChain(ServerWebExchange[] seen) {
        return exchange -> {
            seen[0] = exchange;
            return Mono.empty();
        };
    }

    private GatewayFilterChain neverCalledChain() {
        return exchange -> {
            throw new AssertionError("the filter chain must not be invoked when the request is rejected");
        };
    }
}
