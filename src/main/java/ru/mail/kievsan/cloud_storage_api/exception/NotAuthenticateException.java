package ru.mail.kievsan.cloud_storage_api.exception;

import org.springframework.http.HttpStatus;

public class NotAuthenticateException extends AdviceException {

    public NotAuthenticateException() {
        super();
    }

    public NotAuthenticateException(String message) {
        super(message);
    }

    public NotAuthenticateException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public NotAuthenticateException(String message, HttpStatus httpStatus, String controller) {
        super(message, httpStatus, controller);
    }

    public NotAuthenticateException(String message, HttpStatus httpStatus, String controller, String entryPoint) {
        super(message, httpStatus, controller, entryPoint);
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
