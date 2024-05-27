package ru.mail.kievsan.cloud_storage_api.exception;

import org.springframework.http.HttpStatus;

public class InternalServerException extends AdviceException {

    public InternalServerException() {
    }

    public InternalServerException(String message) {
        super(message);
    }

    public InternalServerException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public InternalServerException(String message, HttpStatus httpStatus, String controller) {
        super(message, httpStatus, controller);
    }

    public InternalServerException(String message, HttpStatus httpStatus, String controller, String entryPoint) {
        super(message, httpStatus, controller, entryPoint);
    }

    public InternalServerException(String message, HttpStatus httpStatus, String controller, String entryPoint, String source) {
        super(message, httpStatus == null ? HttpStatus.INTERNAL_SERVER_ERROR : httpStatus, controller, entryPoint, source);
    }

    public InternalServerException(AdviceException ex, String message) {
        super(ex, message);
    }
}
