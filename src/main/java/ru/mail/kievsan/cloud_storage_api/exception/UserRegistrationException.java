package ru.mail.kievsan.cloud_storage_api.exception;

import org.springframework.http.HttpStatus;

public class UserRegistrationException extends AdviceException {

    public UserRegistrationException() {
        super();
    }

    public UserRegistrationException(String message) {
        super(message);
    }

    public UserRegistrationException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public UserRegistrationException(String message, HttpStatus httpStatus, String controller) {
        super(message, httpStatus, controller);
    }

    public UserRegistrationException(String message, HttpStatus httpStatus, String controller, String entryPoint) {
        super(message, httpStatus, controller, entryPoint);
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
