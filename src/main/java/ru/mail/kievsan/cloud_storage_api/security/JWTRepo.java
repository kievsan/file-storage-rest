package ru.mail.kievsan.cloud_storage_api.security;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class JWTRepo {

    private final Map<String, String> usernames_tokens = new ConcurrentHashMap<>();

    public void put(String username, String token) {
        usernames_tokens.put(username, token);
    }

    public void remove(String username) {
        usernames_tokens.remove(username);
    }

    public void removeByToken(String token) {
        getUsernameByToken(token).ifPresent(this::remove);
    }

    public String get(String username) {
        return usernames_tokens.get(username);
    }

    public Optional<String> getUsernameByToken(String token) {
        return (token == null || token.isEmpty()) ? Optional.empty()
                : usernames_tokens.entrySet().stream()
                .filter(e -> token.equals(e.getValue()))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    public void print() {
        usernames_tokens.forEach((key, value) -> System.out.printf("Current Token list:\n   %s: %s\n", key, value));
    }
}
