package ru.mail.kievsan.cloud_storage_api.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.mail.kievsan.cloud_storage_api.exception.HttpStatusException;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.repository.UserJPARepo;

@Service
@RequiredArgsConstructor
@Slf4j
public class JWTUserDetails implements UserDetailsService {

    private final UserJPARepo userRepo;
    private final JwtProvider provider;

    public User loadUserByJWT(String jwt) {
        log.info("  Start JWTUserDetails.loadUserByJWT() :  token:  {}", jwt);
        if (jwt == null || jwt.isEmpty()) {
            log.info("  ERRor:  Bad user auth token, is Null ");
            throw new HttpStatusException("loadUserByJWT: Bad user auth token", HttpStatus.UNAUTHORIZED);
        }
        try {
            provider.isTokenValid(jwt);
            return loadUserByUsername(provider.extractUsername(jwt));
        } catch (RuntimeException ex) {
            log.info("  ERRor:  Bad user auth token. {}", ex);
            throw new HttpStatusException("loadUserByJWT: Bad user auth token", HttpStatus.UNAUTHORIZED);
        }

    }

    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User '" + username + "' not found"));
    }

}
