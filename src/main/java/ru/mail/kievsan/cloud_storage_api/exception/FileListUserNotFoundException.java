package ru.mail.kievsan.cloud_storage_api.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class FileListUserNotFoundException extends UsernameNotFoundException {

    public FileListUserNotFoundException(String msg) {
        super(msg);
    }
}
