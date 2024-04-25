package ru.mail.kievsan.cloud_storage_api.security;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mail.kievsan.cloud_storage_api.model.entity.User;
import ru.mail.kievsan.cloud_storage_api.repository.UserJPARepo;

@Service
@AllArgsConstructor
public class JWTService {

    private final UserJPARepo userRepo;
    private final JwtProvider provider;

    public User getUserByAuthToken(String authToken) {
        var exception = new RuntimeException("Bad user auth token");
        try {
            if (authToken.startsWith("Bearer ")) {
                int spacePos = authToken.indexOf(" ");
                if (spacePos == -1) throw exception;
                authToken = authToken.substring(spacePos + 1); //7
            } else throw exception;
        } catch (IndexOutOfBoundsException ex) {
            throw exception;
        }
        return userRepo.findByEmail(provider.extractUsername(authToken))
                .orElseThrow(() -> new RuntimeException("Unauthorized user"));
    }
}
