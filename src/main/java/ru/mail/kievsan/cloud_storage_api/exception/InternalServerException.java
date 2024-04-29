package ru.mail.kievsan.cloud_storage_api.exception;

public class InternalServerException extends RuntimeException {

    public InternalServerException(String msg) {
        super(msg);
    }
}