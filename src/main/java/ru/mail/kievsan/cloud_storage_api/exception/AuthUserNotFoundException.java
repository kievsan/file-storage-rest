package ru.mail.kievsan.cloud_storage_api.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AuthUserNotFoundException extends UsernameNotFoundException {

    public AuthUserNotFoundException(String msg) {
        super(msg);
    }
}
