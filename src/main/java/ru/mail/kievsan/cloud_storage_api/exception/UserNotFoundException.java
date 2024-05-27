package ru.mail.kievsan.cloud_storage_api.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends AdviceException {

    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public UserNotFoundException(String message, HttpStatus httpStatus, String controller) {
        super(message, httpStatus, controller);
    }

    public UserNotFoundException(String message, HttpStatus httpStatus, String controller, String entryPoint) {
        super(message, httpStatus, controller, entryPoint);
    }

    public UserNotFoundException(String message, HttpStatus httpStatus, String controller, String entryPoint, String source) {
        super(message, httpStatus == null ? HttpStatus.NOT_FOUND : httpStatus, controller, entryPoint, source);
    }

    public UserNotFoundException(AdviceException ex, String message) {
        super(ex, message);
    }
}
