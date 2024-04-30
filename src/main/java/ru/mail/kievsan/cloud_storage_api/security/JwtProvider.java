package ru.mail.kievsan.cloud_storage_api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.mail.kievsan.cloud_storage_api.exception.UnauthorizedUserException;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtProvider {

    private static final String SECRET_KEY = "5wpkzuGthhtkToSjB/s/6ulZJeV2hYKbPyz9C0WFRIDiYtrOJvvPFb3UeZimcWV/";

    public String extractUsername(String token) throws UnauthorizedUserException {
        return extractClaim(token,Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws UnauthorizedUserException {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new ConcurrentHashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) throws UnauthorizedUserException {
        try {
            return (extractUsername(token).equals(userDetails.getUsername()));
        } catch (ExpiredJwtException | IllegalArgumentException e) {
            throw new UnauthorizedUserException("Expired or invalid JWT token");
        }
    }

    public boolean isTokenValid(String token) throws UnauthorizedUserException {
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) throws UnauthorizedUserException {
        try {
            return getJwtParser().parseSignedClaims(token).getPayload(); // .parseClaimsJws(token).getBody()
        } catch (JwtException | IllegalArgumentException ex) {
            log.error("       Error parsing JWT token when extractAllClaims(jwt) ", ex);
            throw new UnauthorizedUserException("Expired or Invalid JWT token. " + ex);
        }
    }

    public JwtParser getJwtParser() {
        return Jwts.parser().verifyWith((SecretKey) getSigningKey()).build(); // .setSigningKey(getSigningKey()).build()
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
