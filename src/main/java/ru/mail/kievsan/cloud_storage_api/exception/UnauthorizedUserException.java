package ru.mail.kievsan.cloud_storage_api.exception;

public class UnauthorizedUserException extends RuntimeException {

    public UnauthorizedUserException(String msg) {
        super(msg);
    }
}