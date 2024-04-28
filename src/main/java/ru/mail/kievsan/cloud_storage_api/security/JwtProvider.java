package ru.mail.kievsan.cloud_storage_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.mail.kievsan.cloud_storage_api.exception.HttpStatusException;
import ru.mail.kievsan.cloud_storage_api.repository.UserJPARepo;

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

    private final UserJPARepo userRepo;

    private final String tokenHeaderName = "Authorization";
    private final String tokenPrefix = "Bearer ";

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        String username = extractUsername(token);
        UserDetails userDetails = userRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User '" + username + "' not found"));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String extractUsername(String token) {
        return extractClaim(token,Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new ConcurrentHashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(getSigningKey())
                .compact();
    }

//    public String resolveToken(HttpServletRequest request) {
//        return resolveToken(request, tokenHeaderName, tokenPrefix);
//    }
//
//    public String resolveToken(HttpServletRequest request, String tokenHeaderName) {
//        return resolveToken(request, tokenHeaderName, tokenPrefix);
//    }
//
//    public String resolveToken(HttpServletRequest request, String tokenHeaderName, String startsWith) {
//        String token = request.getHeader(tokenHeaderName.isEmpty() ? this.tokenHeaderName : tokenHeaderName);
//        return resolveToken(token, startsWith);
//    }
//
//    public String resolveToken(String token, String startsWith) {
//        return token != null && token.startsWith(startsWith) && token.length() > startsWith.length()
//                ? token.replace(startsWith, "") : null;
//    }
//
//    public String resolveToken(String token) {
//        return resolveToken(token, tokenPrefix);
//    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            throw new HttpStatusException("Expired or invalid JWT token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean isTokenValid(String token) throws RuntimeException {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) getSigningKey()).build()
                    .parseSignedClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException e) {
            throw new HttpStatusException("Invalid JWT token", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            throw new HttpStatusException("Expired JWT token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSigningKey()).build()
//                .setSigningKey(getSigningKey()).build()
                .parseSignedClaims(token).getPayload();
//                .parseClaimsJws(token).getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
