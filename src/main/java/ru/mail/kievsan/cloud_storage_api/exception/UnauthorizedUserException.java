package ru.mail.kievsan.cloud_storage_api.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedUserException extends AdviceException {

    public UnauthorizedUserException() {
        this("Unauthorized user exception");
    }

    public UnauthorizedUserException(String message) {
        this(message, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedUserException(String message, HttpStatus httpStatus) {
        this(message, httpStatus, null);
    }

    public UnauthorizedUserException(String message, HttpStatus httpStatus, String controller) {
        this(message, httpStatus, controller, null);
    }

    public UnauthorizedUserException(String message, HttpStatus httpStatus, String controller, String entryPoint) {
        this(message, httpStatus, controller, entryPoint, null);
    }

    public UnauthorizedUserException(String message, HttpStatus httpStatus, String controller, String entryPoint, String source) {
        super(message, httpStatus == null ? HttpStatus.UNAUTHORIZED : httpStatus, controller, entryPoint, source);
    }

    public UnauthorizedUserException(AdviceException ex, String message) {
        super(ex, message);
    }
}
