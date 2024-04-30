package ru.mail.kievsan.cloud_storage_api.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.mail.kievsan.cloud_storage_api.exception.UnauthorizedUserException;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.security.JWTUserDetails;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthTokenValidator {

    private final JWTUserDetails userDetails;

    public User validateJWT(String jwt, String logMsg, String errMsg) {
        logg(logMsg);
        try {
            return userDetails.loadUserByJWT(jwt);
        } catch (UnauthorizedUserException ex) {
            String msg = String.format("%s:  user unauthorized.  %s", errMsg, ex);
            log.error(msg);
            throw new UnauthorizedUserException(msg);
        }
    }

    public void logg(String msg) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        log.info(" {}:  user  {}, {}", msg, auth.getName(), auth.getAuthorities());
    }
}
