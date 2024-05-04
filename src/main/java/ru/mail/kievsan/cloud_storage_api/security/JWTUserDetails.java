package ru.mail.kievsan.cloud_storage_api.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        log.info("  Start JWTUserDetails.loadUserByJWT(), jwt = '{}'", jwt);
        return loadUserByUsername(ValidateJWTandExtractUsername(jwt));
    }

    public String ValidateJWTandExtractUsername(String jwt) throws UnauthorizedUserException {
        String badJWTExceptionMsg = "Bad user auth token";
        String badJWTErrMsg = "loadUserByJWT(jwt) ERRor:  " + badJWTExceptionMsg;
        try {
            return provider.extractUsername(validateJWT(jwt));
        } catch (RuntimeException ex) {
            log.error("  {}. {}", badJWTErrMsg, ex.getMessage());
            throw new UnauthorizedUserException(badJWTExceptionMsg);
        }
    }

    public String validateJWT(String jwt) throws UnauthorizedUserException {
        return provider.resolveToken(jwt);
    }

}
