package ru.mail.kievsan.cloud_storage_api.exception;

public class UserRegisterUserInUseException extends RuntimeException {

    public UserRegisterUserInUseException(String msg) {
        super(msg);
    }
}
