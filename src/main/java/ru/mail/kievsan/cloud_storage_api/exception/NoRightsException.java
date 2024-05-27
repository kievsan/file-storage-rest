package ru.mail.kievsan.cloud_storage_api.exception;

import org.springframework.http.HttpStatus;

public class NoRightsException extends AdviceException {

    public NoRightsException() {
        super();
    }

    public NoRightsException(String message) {
        super(message);
    }

    public NoRightsException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public NoRightsException(String message, HttpStatus httpStatus, String controller) {
        super(message, httpStatus, controller);
    }

    public NoRightsException(String message, HttpStatus httpStatus, String controller, String entryPoint) {
        super(message, httpStatus, controller, entryPoint);
    }

    public NoRightsException(String message, HttpStatus httpStatus, String controller, String entryPoint, String source) {
        super(message,
                httpStatus == null ? HttpStatus.FORBIDDEN : httpStatus,
                controller, entryPoint, source);
    }

    public NoRightsException(AdviceException ex, String message) {
        super(ex, message);
    }
}