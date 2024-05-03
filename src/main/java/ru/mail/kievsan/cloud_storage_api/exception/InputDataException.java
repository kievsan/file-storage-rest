package ru.mail.kievsan.cloud_storage_api.exception;

import org.springframework.http.HttpStatus;

public class InputDataException extends AdviceException {

    public InputDataException() {
        super();
    }

    public InputDataException(String message) {
        super(message);
    }

    public InputDataException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public InputDataException(String message, HttpStatus httpStatus, String controller) {
        super(message, httpStatus, controller);
    }

    public InputDataException(String message, HttpStatus httpStatus, String controller, String entryPoint) {
        super(message, httpStatus, controller, entryPoint);
    }

    public InputDataException(String message, HttpStatus httpStatus, String controller, String entryPoint, String source) {
        super(message,
                httpStatus == null ? HttpStatus.BAD_REQUEST : httpStatus,
                controller == null ? "FILE" : controller,
                entryPoint == null ? "'/file'" : entryPoint,
                source);
    }

    public InputDataException(AdviceException ex, String message) {
        super(ex, message);
    }
}