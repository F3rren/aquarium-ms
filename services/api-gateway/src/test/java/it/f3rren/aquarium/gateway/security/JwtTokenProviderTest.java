package it.f3rren.aquarium.gateway.security;

import it.f3rren.aquarium.gateway.config.JwtProperties;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider providerWithSecret(String secret) {
        JwtProperties props = new JwtProperties();
        props.setSecret(secret);
        return new JwtTokenProvider(props);
    }

    @Test
    void issuedTokenValidatesBackToTheSameSubject() {
        JwtTokenProvider provider = providerWithSecret("any-length-secret-is-fine-its-hashed");

        String token = provider.issueToken("42");

        assertThat(provider.validateAndGetSubject(token)).contains("42");
    }

    @Test
    void rejectsATokenSignedWithADifferentSecret() {
        String token = providerWithSecret("secret-a").issueToken("42");

        assertThat(providerWithSecret("secret-b").validateAndGetSubject(token)).isEmpty();
    }

    @Test
    void rejectsGarbageInput() {
        JwtTokenProvider provider = providerWithSecret("some-secret");

        assertThat(provider.validateAndGetSubject("not-a-jwt-at-all")).isEmpty();
    }

    @Test
    void acceptsAShortSecretWithoutThrowingAWeakKeyException() {
        // 5 raw characters is far below HS256's 256-bit minimum key length on its own - only
        // works because JwtTokenProvider SHA-256-hashes the secret before using it as the key.
        JwtTokenProvider provider = providerWithSecret("short");

        String token = provider.issueToken("1");

        assertThat(provider.validateAndGetSubject(token)).contains("1");
    }
}
