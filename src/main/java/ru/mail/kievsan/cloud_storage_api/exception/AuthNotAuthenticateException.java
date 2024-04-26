package ru.mail.kievsan.cloud_storage_api.exception;

public class AuthNotAuthenticateException extends RuntimeException {

    public AuthNotAuthenticateException(String msg) {
        super(msg);
    }
}
