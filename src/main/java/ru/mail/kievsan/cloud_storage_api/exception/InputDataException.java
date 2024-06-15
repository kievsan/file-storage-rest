package ru.mail.kievsan.cloud_storage_api.exception;

import org.springframework.http.HttpStatus;

public class InputDataException extends AdviceException {

    public InputDataException() {
        this("input data exception");
    }

    public InputDataException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }

    public InputDataException(String message, HttpStatus httpStatus) {
        this(message, httpStatus, null);
    }

    public InputDataException(String message, HttpStatus httpStatus, String controller) {
        this(message, httpStatus, controller, null);
    }

    public InputDataException(String message, HttpStatus httpStatus, String controller, String entryPoint) {
        this(message, httpStatus, controller, entryPoint, null);
    }

    public InputDataException(String message, HttpStatus httpStatus, String controller, String entryPoint, String source) {
        super(message,
                httpStatus == null ? HttpStatus.BAD_REQUEST : httpStatus,
                controller, entryPoint, source);
    }

    public InputDataException(AdviceException ex, String message) {
        super(ex, message);
    }
}
