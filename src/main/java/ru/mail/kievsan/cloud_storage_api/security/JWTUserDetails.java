package ru.mail.kievsan.cloud_storage_api.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.mail.kievsan.cloud_storage_api.exception.UnauthorizedUserException;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.repository.UserJPARepo;

@Service
@RequiredArgsConstructor
@Slf4j
public class JWTUserDetails implements UserDetailsService {

    private final UserJPARepo userRepo;
    private final JwtProvider provider;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User '" + username + "' not found"));
    }

    public User loadUserByJWT(String jwt) throws UnauthorizedUserException {
        log.info("  Start JWTUserDetails.loadUserByJWT(), jwt = '{}'", presentJWT(jwt));
        String badJWTExceptionMsg = "Bad user auth token";
        String badJWTErrMsg = "loadUserByJWT(jwt) warn:  " + badJWTExceptionMsg;
        try {
            return loadUserByUsername(provider.extractUsername(validatedJWT(jwt)));
        } catch (RuntimeException ex) {
            log.warn("  {}. {}", badJWTErrMsg, ex.getMessage());
            throw new UnauthorizedUserException(badJWTExceptionMsg);
        }
    }

    public String validatedJWT(String jwt) throws UnauthorizedUserException {
        String trueJWT = provider.resolveToken(jwt)
                .orElseThrow(() -> new UnauthorizedUserException("Empty or invalid JWT token."));
        provider.validateToken(trueJWT);
        return trueJWT;
    }

    public final String presentJWT(String jwt) {
        return jwt == null || jwt.isBlank() ? "" : jwt.substring(0,jwt.length()/10) + "...";
    }

    public final String presentAuthenticated() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return String.format("user  %s, %s", auth.getName(), auth.getAuthorities());
    }
}
