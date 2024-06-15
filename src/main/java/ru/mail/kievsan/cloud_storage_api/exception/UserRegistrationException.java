package ru.mail.kievsan.cloud_storage_api.exception;

import org.springframework.http.HttpStatus;

public class UserRegistrationException extends AdviceException {

    public UserRegistrationException() {
        this("User registration exception");
    }

    public UserRegistrationException(String message) {
        this(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    public UserRegistrationException(String message, HttpStatus httpStatus) {
        this(message, httpStatus, null);
    }

    public UserRegistrationException(String message, HttpStatus httpStatus, String controller) {
        this(message, httpStatus, controller, null);
    }

    public UserRegistrationException(String message, HttpStatus httpStatus, String controller, String entryPoint) {
        this(message, httpStatus, controller, entryPoint, null);
    }

    public UserRegistrationException(String message, HttpStatus httpStatus, String controller, String entryPoint, String source) {
        super(message,
                httpStatus == null ? HttpStatus.UNPROCESSABLE_ENTITY : httpStatus,
                controller == null ? "USER" : controller,
                entryPoint == null ? "'/user'" : entryPoint,
                source);
    }

    public UserRegistrationException(AdviceException ex, String message) {
        super(ex, message);
    }
}
