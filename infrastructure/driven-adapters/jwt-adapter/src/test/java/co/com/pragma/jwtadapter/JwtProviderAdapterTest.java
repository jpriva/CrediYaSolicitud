package co.com.pragma.jwtadapter;

import co.com.pragma.jwtadapter.config.JwtProperties;
import co.com.pragma.model.jwt.JwtData;
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

    @BeforeEach
    void setUp() {
        properties = new JwtProperties();
        properties.setSecret("VGhpcyBpcyBhIHZlcnkgc2VjdXJlIGFuZCBsb25nIHNlY3JldCBrZXkgZm9yIEpXVCBhdXRoZW50aWNhdGlvbg==");
        properties.setExpiration(3600L); // 1 hour

        jwtProviderAdapter = new JwtProviderAdapter(properties);

    }

    @Test
    void getClaims_shouldParseValidToken() {
        String token = generateTestToken("john.doe@example.com", "John Doe", "12345", "CLIENTE", 1, properties.getExpiration());

        JwtData claims = jwtProviderAdapter.getClaims(token);

        assertThat(claims).isNotNull();
        assertThat(claims.subject()).isEqualTo("john.doe@example.com");
        assertThat(claims.role()).isEqualTo("CLIENTE");
        assertThat(claims.roleId()).isEqualTo(1);
        assertThat(claims.name()).isEqualTo("John Doe");
        assertThat(claims.idNumber()).isEqualTo("12345");
    }

    @Test
    void getClaims_shouldThrowExceptionForInvalidSignature() {
        String validToken = generateTestToken("user@test.com", "Test User", "111", "ADMIN", 2, properties.getExpiration());
        String invalidToken = validToken.substring(0, validToken.lastIndexOf('.')) + ".invalidSignature";

        assertThatThrownBy(() -> jwtProviderAdapter.getClaims(invalidToken))
                .isInstanceOf(SignatureException.class);
    }

    @Test
    void getClaims_shouldThrowExceptionForExpiredToken() {
        String expiredToken = generateTestToken("user@test.com", "Test User", "111", "ADMIN", 2, 0L);

        await().atMost(Duration.ofSeconds(1)).untilAsserted(() ->
                assertThatThrownBy(() -> jwtProviderAdapter.getClaims(expiredToken))
                        .isInstanceOf(ExpiredJwtException.class)
        );
    }

    private String generateTestToken(String subject, String name, String idNumber, String role, Integer roleId, long expirationSeconds) {
        Map<String, Object> claims = new HashMap<>();
        if (role != null) {
            claims.put("role", role);
            claims.put("roleId", roleId);
        }
        claims.put("name", name);
        claims.put("idNumber", idNumber);

        byte[] keyBytes = Base64.getDecoder().decode(properties.getSecret());
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