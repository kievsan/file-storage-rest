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

    @Override
    public User loadUserByUsername(String username) throws UnauthorizedUserException {
        return userRepo.findByEmail(username)
                .orElseThrow(() -> new UnauthorizedUserException("User '" + username + "' not found"));
    }

    public User loadUserByJWT(String jwt) throws UnauthorizedUserException {
        log.info("  Start JWTUserDetails.loadUserByJWT(), jwt = '{}'", jwt);
        return loadUserByUsername(ValidateJWTandExtractUsername(jwt));
    }

    public String ValidateJWTandExtractUsername(String jwt) throws UnauthorizedUserException {
        String badJWTExceptionMsg = "Bad user auth token";
        String badJWTErrMsg = "loadUserByJWT(jwt) ERRor:  " + badJWTExceptionMsg;

        jwt = provider.resolveToken(jwt);
        if (jwt == null || jwt.isBlank())  {
            log.error("  {} or is Null ", badJWTErrMsg);
            throw new UnauthorizedUserException(badJWTExceptionMsg);
        }
        try {
            return provider.extractUsername(jwt);
        } catch (RuntimeException ex) {
            log.error("  {}. {}", badJWTErrMsg, ex.getMessage());
            throw new UnauthorizedUserException(badJWTExceptionMsg);
        }
    }

}
