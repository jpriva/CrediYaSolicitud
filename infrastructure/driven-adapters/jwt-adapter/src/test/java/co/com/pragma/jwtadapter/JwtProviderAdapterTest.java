package co.com.pragma.jwtadapter;

import co.com.pragma.jwtadapter.config.JwtProperties;
import co.com.pragma.model.jwt.JwtData;
import co.com.pragma.model.logs.gateways.LoggerPort;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

class JwtProviderAdapterTest {

    private JwtProviderAdapter jwtProviderAdapter;
    private JwtProperties properties;
    private LoggerPort logger;

    @BeforeEach
    void setUp() {
        properties = new JwtProperties();
        properties.setCallbackSecret("VGhpcyBpcyBhIHZlcnkgc2VjdXJlIGFuZCBsb25nIHNlY3JldCBrZXkgZm9yIEpXVCBhdXRoZW50aWNhdGlvbg==");
        properties.setExpiration(3600L); // 1 hour

        jwtProviderAdapter = new JwtProviderAdapter(properties,logger);

    }

    @Test
    void getClaims_shouldParseValidToken() {
        String token = generateTestToken("john.doe@example.com", "CLIENTE", properties.getExpiration());

        JwtData claims = jwtProviderAdapter.getClaims(token);

        assertThat(claims).isNotNull();
        assertThat(claims.subject()).isEqualTo("john.doe@example.com");
        assertThat(claims.role()).isEqualTo("CLIENTE");
    }

    @Test
    void getClaims_shouldThrowExceptionForInvalidSignature() {
        String validToken = generateTestToken("user@test.com", "ADMIN", properties.getExpiration());
        String invalidToken = validToken.substring(0, validToken.lastIndexOf('.')) + ".invalidSignature";

        assertThatThrownBy(() -> jwtProviderAdapter.getClaims(invalidToken))
                .isInstanceOf(SignatureException.class);
    }

    @Test
    void getClaims_shouldThrowExceptionForExpiredToken() {
        String expiredToken = generateTestToken("user@test.com", "ADMIN", 0L);

        await().atMost(Duration.ofSeconds(1)).untilAsserted(() ->
                assertThatThrownBy(() -> jwtProviderAdapter.getClaims(expiredToken))
                        .isInstanceOf(ExpiredJwtException.class)
        );
    }

    private String generateTestToken(String subject, String role, long expirationSeconds) {
        Map<String, Object> claims = new HashMap<>();
        if (role != null) {
            claims.put("role", role);
        }

        byte[] keyBytes = Base64.getDecoder().decode(properties.getCallbackSecret());
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationSeconds * 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}