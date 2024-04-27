package ru.mail.kievsan.cloud_storage_api.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

public class JWTSecretKeysManager {

    public static void main(String[] args) {
        String secretKey = generateKey();
        System.out.println(secretKey);
        System.out.println(getSigningKey(secretKey));
    }

    private static String generateKey() {
        var secretKey = Jwts.SIG.HS384.key().build();
        return Encoders.BASE64.encode(secretKey.getEncoded());
    }

    private static Key getSigningKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
