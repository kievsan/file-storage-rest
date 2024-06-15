package ru.mail.kievsan.cloud_storage_api.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends AdviceException {

    public UserNotFoundException() {
        this("User not found exception");
    }

    public UserNotFoundException(String message) {
        this(message, HttpStatus.NOT_FOUND);
    }

    public UserNotFoundException(String message, HttpStatus httpStatus) {
        this(message, httpStatus, null);
    }

    public UserNotFoundException(String message, HttpStatus httpStatus, String controller) {
        this(message, httpStatus, controller, null);
    }

    public UserNotFoundException(String message, HttpStatus httpStatus, String controller, String entryPoint) {
        this(message, httpStatus, controller, entryPoint, null);
    }

    public UserNotFoundException(String message, HttpStatus httpStatus, String controller, String entryPoint, String source) {
        super(message, httpStatus == null ? HttpStatus.NOT_FOUND : httpStatus, controller, entryPoint, source);
    }

    public UserNotFoundException(AdviceException ex, String message) {
        super(ex, message);
    }
}
