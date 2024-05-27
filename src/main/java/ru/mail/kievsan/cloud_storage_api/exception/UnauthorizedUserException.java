package ru.mail.kievsan.cloud_storage_api.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedUserException extends AdviceException {

    public UnauthorizedUserException() {
        super();
    }

    public UnauthorizedUserException(String message) {
        super(message);
    }

    public UnauthorizedUserException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public UnauthorizedUserException(String message, HttpStatus httpStatus, String controller) {
        super(message, httpStatus, controller);
    }

    public UnauthorizedUserException(String message, HttpStatus httpStatus, String controller, String entryPoint) {
        super(message, httpStatus, controller, entryPoint);
    }

    public UnauthorizedUserException(String message, HttpStatus httpStatus, String controller, String entryPoint, String source) {
        super(message, httpStatus == null ? HttpStatus.UNAUTHORIZED : httpStatus, controller, entryPoint, source);
    }

    public UnauthorizedUserException(AdviceException ex, String message) {
        super(ex, message);
    }
}