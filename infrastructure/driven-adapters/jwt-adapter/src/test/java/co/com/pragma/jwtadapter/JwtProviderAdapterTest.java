package co.com.pragma.jwtadapter;

import co.com.pragma.jwtadapter.config.JwtProperties;
import co.com.pragma.model.jwt.JwtData;
import co.com.pragma.model.user.Role;
import co.com.pragma.model.user.User;
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
    private User testUser;
    private JwtProperties properties;

    @BeforeEach
    void setUp() {
        properties = new JwtProperties();
        properties.setSecret("VGhpcyBpcyBhIHZlcnkgc2VjdXJlIGFuZCBsb25nIHNlY3JldCBrZXkgZm9yIEpXVCBhdXRoZW50aWNhdGlvbg==");
        properties.setExpiration(3600L); // 1 hour

        jwtProviderAdapter = new JwtProviderAdapter(properties);

        testUser = User.builder()
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .idNumber("123456789")
                .role(Role.builder().rolId(1).name("ADMIN").build())
                .build();
    }

    @Test
    void getClaims_shouldParseValidToken() {
        String token = generateTestToken(testUser, properties.getExpiration());

        JwtData claims = jwtProviderAdapter.getClaims(token);

        assertThat(claims).isNotNull();
        assertThat(claims.subject()).isEqualTo(testUser.getEmail());
        assertThat(claims.role()).isEqualTo(testUser.getRole().getName());
        assertThat(claims.roleId()).isEqualTo((Integer) testUser.getRole().getRolId());
        assertThat(claims.name()).isEqualTo("John Doe");
        assertThat(claims.idNumber()).isEqualTo(testUser.getIdNumber());
    }

    @Test
    void getClaims_shouldThrowExceptionForInvalidSignature() {
        String validToken = generateTestToken(testUser, properties.getExpiration());
        String invalidToken = validToken.substring(0, validToken.lastIndexOf('.')) + ".invalidSignature";

        assertThatThrownBy(() -> jwtProviderAdapter.getClaims(invalidToken))
                .isInstanceOf(SignatureException.class);
    }

    @Test
    void getClaims_shouldThrowExceptionForExpiredToken() {
        String expiredToken = generateTestToken(testUser, 0L);

        await().atMost(Duration.ofSeconds(1)).untilAsserted(() ->
                assertThatThrownBy(() -> jwtProviderAdapter.getClaims(expiredToken))
                        .isInstanceOf(ExpiredJwtException.class)
        );
    }
    private String generateTestToken(User user, long expirationSeconds) {
        Map<String, Object> claims = new HashMap<>();
        if (user.getRole() != null) {
            claims.put("role", user.getRole().getName());
            claims.put("roleId", user.getRole().getRolId());
        }
        claims.put("name", user.getName() + " " + user.getLastName());
        claims.put("idNumber", user.getIdNumber());

        byte[] keyBytes = Base64.getDecoder().decode(properties.getSecret());
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationSeconds * 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}