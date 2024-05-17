package ru.mail.kievsan.cloud_storage_api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.mail.kievsan.cloud_storage_api.exception.UnauthorizedUserException;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.mail.kievsan.cloud_storage_api.security.JWTSecretKeysManager.generateKey;
import static ru.mail.kievsan.cloud_storage_api.security.JWTSecretKeysManager.getSigningKey;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtProvider {

    @Getter
    @Value("${security.jwt.token.expire-length}")
    private long tokenLifetime;     // in milliseconds

    @Value("${security.jwt.token.secret-key}")
    private String secret;

    private String secretKey;

    @PostConstruct
    protected void secretInit() {
        boolean secretExists = secret != null && secret.trim().equals(secret) && secret.length() > 10;
        secretKey = secretExists ? generateKey(secret) : generateKey();
        log.warn(">--------------< Secret key: {}", secretKey);
    }

    public String extractUsername(String token) {
        return getJwtParser().parseSignedClaims((token)).getPayload().getSubject();
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenLifetime))
                .signWith(getSigningKey(secretKey))
                .compact();
    }

    public String generateToken(UserDetails userDetails) {
       return Jwts.builder()
                .claim("authorities", userDetails.getAuthorities()
                        .stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenLifetime))
                .signWith(getSigningKey(secretKey))
                .compact();
    }

    public String generateToken(Authentication auth) {
        return generateToken((UserDetails) auth.getPrincipal());
    }

    public Optional<String> resolveToken(String rawToken) {
        String tokenPrefix = "Bearer ";
        boolean isTrueToken = StringUtils.hasText(rawToken) && rawToken.trim().startsWith(tokenPrefix);
        return isTrueToken ? Optional.of(rawToken.replace(tokenPrefix, "")) : Optional.empty();
        // rawToken.substring(tokenPrefix.length());
    }

    public void validateToken(String token) throws UnauthorizedUserException {
        String err = "       Error parsing JWT token... ";
        try {
            getJwtParser().parseSignedClaims(token);
            return;
        }  catch (ExpiredJwtException ex) {
            err += "Expired JWT token. " + ex.getMessage();
        } catch (UnsupportedJwtException ex) {
            err += "Unsupported JWT token. " + ex.getMessage();
        } catch (IllegalArgumentException ex) {
            err += "JWT claims string is empty. " + ex.getMessage();
        } catch (SecurityException ex) {
            err += "there is an error with the signature of you token. " + ex.getMessage();
        } catch (MalformedJwtException ex) {
            err += "Invalid JWT token. " + ex.getMessage();
        } catch (JwtException ex) {
            err += "Problem JWT token. " + ex.getMessage();
        } catch (RuntimeException ex) {
            err += "Some problem JWT token. " + ex.getMessage();
        }
        log.error(err);
        throw new UnauthorizedUserException("Expired or invalid JWT token.");
    }

    public boolean isTokenValid(String token, UserDetails userDetails) throws UnauthorizedUserException {
        try {
            return (extractUsername(token).equals(userDetails.getUsername()));
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedUserException("Expired or invalid JWT token.");
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return getJwtParser().parseSignedClaims(token).getPayload();
    }

    public JwtParser getJwtParser() {
        return Jwts.parser().verifyWith((SecretKey) getSigningKey(secretKey)).build(); // .setSigningKey(getSigningKey()).build()
    }

}
