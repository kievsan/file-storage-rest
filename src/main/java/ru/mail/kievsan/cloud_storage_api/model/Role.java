package ru.mail.kievsan.cloud_storage_api.model;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum Role implements GrantedAuthority {

    ADMIN("ADMIN"),
    USER("USER");

    private final String value;

    @Override
    public String getAuthority() {
        return value;
    }

}
