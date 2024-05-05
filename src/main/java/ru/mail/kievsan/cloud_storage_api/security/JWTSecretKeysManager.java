package ru.mail.kievsan.cloud_storage_api.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;

public class JWTSecretKeysManager {

    public static void main(String[] args) {
        String secretKey = generateKey();
        System.out.println(secretKey);
        System.out.println(getSigningKey(secretKey));
    }

    public static String generateKey() {
        var secretKey = Jwts.SIG.HS384.key().build();
        return Encoders.BASE64.encode(secretKey.getEncoded());
    }

    public static String generateKey(String secret) {
        return Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public static Key getSigningKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
