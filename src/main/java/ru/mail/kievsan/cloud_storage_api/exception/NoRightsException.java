package ru.mail.kievsan.cloud_storage_api.exception;

import org.springframework.http.HttpStatus;

public class NoRightsException extends AdviceException {

    public NoRightsException() {
        this("No rights exception");
    }

    public NoRightsException(String message) {
        this(message, HttpStatus.FORBIDDEN);
    }

    public NoRightsException(String message, HttpStatus httpStatus) {
        this(message, httpStatus, null);
    }

    public NoRightsException(String message, HttpStatus httpStatus, String controller) {
        this(message, httpStatus, controller, null);
    }

    public NoRightsException(String message, HttpStatus httpStatus, String controller, String entryPoint) {
        this(message, httpStatus, controller, entryPoint, null);
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
