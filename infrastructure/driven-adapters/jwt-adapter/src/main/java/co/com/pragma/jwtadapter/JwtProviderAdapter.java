package co.com.pragma.jwtadapter;

import co.com.pragma.jwtadapter.config.JwtProperties;
import co.com.pragma.model.jwt.JwtData;
import co.com.pragma.model.jwt.gateways.JwtProviderPort;
import co.com.pragma.model.logs.gateways.LoggerPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProperties.class)
public class JwtProviderAdapter implements JwtProviderPort {

    private final JwtProperties jwtProperties;
    private final LoggerPort logger;

    @Override
    public String generateCallbackToken(String taskId) {
        long now = System.currentTimeMillis();
        long expirationTime = now + (15 * 60 * 1000);//15 minutes
        return Jwts.builder()
                .setSubject(taskId)
                .claim("role", "CALLBACK_ROLE")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public JwtData getClaims(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
        String subject = claims.getSubject();
        String role = claims.get("role", String.class);
        return new JwtData(subject, role,1,"","");
    }

    @Override
    public Mono<Boolean> validateCallbackToken(String token) {
        return Mono.fromCallable(() -> {
                    Jwts.parserBuilder()
                            .setSigningKey(getSigningKey())
                            .build()
                            .parseClaimsJws(token);
                    return true;
                }).doOnError(ex -> logger.error("Invalid JWT token: {}", ex.getMessage()))
                .onErrorResume(ex -> Mono.empty());
    }


    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getCallbackSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
