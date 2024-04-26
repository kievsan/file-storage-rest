package ru.mail.kievsan.cloud_storage_api.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.mail.kievsan.cloud_storage_api.exception.HttpStatusException;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.repository.UserJPARepo;

@Service
@RequiredArgsConstructor
public class JWTUserDetails implements UserDetailsService {

    private final UserJPARepo userRepo;
    private final JwtProvider provider;

    public User loadUserByJWT(String jwt) throws HttpStatusException {
        jwt = provider.resolveToken(jwt,"Bearer ");
        if (jwt == null || jwt.isEmpty() || !provider.isTokenValid(jwt)) {
            throw new HttpStatusException("Bad user auth token", HttpStatus.UNAUTHORIZED);
        }
        return loadUserByUsername(provider.extractUsername(jwt));
    }

    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User '" + username + "' not found"));
    }

}
