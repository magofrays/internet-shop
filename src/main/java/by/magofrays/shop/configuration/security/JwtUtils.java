package by.magofrays.shop.configuration.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
public class JwtUtils {
    @Value("${security.jwt.expires-hours}")
    Integer expiresHours;

    @Value("${security.jwt.secret}")
    String secret;

    public String createJwt(UserDetails userDetails){
        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret),
                SignatureAlgorithm.HS256.getJcaName());
        return Jwts.builder()
                .claim("id", userDetails.getUsername())
                .claim("role", userDetails.getAuthorities().stream().findFirst())
                .setIssuer(this.getClass().getName())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(expiresHours, ChronoUnit.HOURS)))
                .signWith(hmacKey)
                .compact();
    }

    public Optional<Jws<Claims>> parseToken(String jwt){
        try {
            Key hmacKey = new SecretKeySpec(
                    Base64.getDecoder().decode(secret),
                    SignatureAlgorithm.HS256.getJcaName()
            );

            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(hmacKey)
                    .build()
                    .parseClaimsJws(jwt);

            return Optional.of(claims);
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
        }
        return Optional.empty();
    }
}
