package ru.mail.kievsan.cloud_storage_api.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.mail.kievsan.cloud_storage_api.exception.UnauthorizedUserException;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.security.JwtUserDetails;

import java.util.function.Consumer;

@Slf4j
@Component
@AllArgsConstructor
public class UserProvider {

    public final JwtUserDetails userDetails;

    public User trueUser(String jwt, String logMsg, String logErr, Consumer<String> logger) throws UnauthorizedUserException {
        log.info(" {}:  {}", logMsg, userDetails.presentAuthenticated());
        try {
            return userDetails.loadUserByJWT(jwt); // валидация jwt и выдача user
        } catch (UnauthorizedUserException ex) {
            String msg = "%s:  user unauthorized.  %s".formatted(logErr, ex);
            logger.accept(msg);
            throw new UnauthorizedUserException(ex, msg);
        }
    }
}
