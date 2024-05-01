package ru.mail.kievsan.cloud_storage_api.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
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

    public User loadUserByJWT(String jwt) throws UnauthorizedUserException {
        log.info("  Start JWTUserDetails.loadUserByJWT() :  token:  '{}'", jwt);
        String badJWTErr = "Bad user auth token";
        jwt = provider.resolveToken(jwt);
        if (jwt == null) {
            log.error("  loadUserByJWT(jwt) ERRor:  {} or is Null ", badJWTErr);
            throw new UnauthorizedUserException(badJWTErr);
        }
        try {
            return loadUserByUsername(provider.extractUsername(jwt));
        } catch (RuntimeException ex) {
            log.error("  loadUserByJWT(jwt) ERRor:  {}. {}", badJWTErr, ex.getMessage());
            throw new UnauthorizedUserException(badJWTErr);
        }
    }
    public User loadUserByUsername(String username) throws UnauthorizedUserException {
        return userRepo.findByEmail(username)
                .orElseThrow(() -> new UnauthorizedUserException("User '" + username + "' not found"));
    }

}
