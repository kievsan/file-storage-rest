package ru.mail.kievsan.cloud_storage_api.exception;

public class UserSignupIncompleteTransactionException extends RuntimeException {

    public UserSignupIncompleteTransactionException(String msg) {
        super(msg);
    }
}
