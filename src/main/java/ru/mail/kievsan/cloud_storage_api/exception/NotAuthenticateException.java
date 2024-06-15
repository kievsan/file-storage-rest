package ru.mail.kievsan.cloud_storage_api.exception;

import org.springframework.http.HttpStatus;

public class NotAuthenticateException extends AdviceException {

    public NotAuthenticateException() {
        this("Not authenticate exception");
    }

    public NotAuthenticateException(String message) {
        this(message, HttpStatus.UNAUTHORIZED);
    }

    public NotAuthenticateException(String message, HttpStatus httpStatus) {
        this(message, httpStatus, null);
    }

    public NotAuthenticateException(String message, HttpStatus httpStatus, String controller) {
        this(message, httpStatus, controller, null);
    }

    public NotAuthenticateException(String message, HttpStatus httpStatus, String controller, String entryPoint) {
        this(message, httpStatus, controller, entryPoint, null);
    }

    public NotAuthenticateException(String message, HttpStatus httpStatus, String controller, String entryPoint, String source) {
        super(message,
                httpStatus == null ? HttpStatus.UNAUTHORIZED : httpStatus,
                controller == null ? "AUTH" : controller,
                entryPoint == null ? "'/login'" : entryPoint,
                source == null ? "'authenticate service'" : source);
    }

    public NotAuthenticateException(AdviceException ex, String message) {
        super(ex, message);
    }
}
