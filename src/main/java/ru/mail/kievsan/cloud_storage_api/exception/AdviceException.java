package ru.mail.kievsan.cloud_storage_api.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
public class AdviceException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String controller;
    private final String entryPoint;
    private final String source;

    public AdviceException() {
        this("Unknown mistake...");
    }

    public AdviceException(String message) {
        this(message, null);
    }

    public AdviceException(String message, HttpStatus httpStatus) {
        this(message, httpStatus, null);
    }

    public AdviceException(String message, HttpStatus httpStatus, String controller) {
        this(message, httpStatus, controller, null);
    }

    public AdviceException(String message, HttpStatus httpStatus, String controller, String entryPoint) {
        this(message, httpStatus, controller, entryPoint, null);
    }

    public AdviceException(String message, HttpStatus httpStatus, String controller, String entryPoint, String source) {
        super(message);
        this.httpStatus = httpStatus;
        this.controller = controller;
        this.entryPoint = entryPoint;
        this.source = source;
    }

    public AdviceException(AdviceException ex, String message) {
        this(message, ex.httpStatus, ex.controller, ex.entryPoint, ex.source);
    }

    public String log() {
        String caption = getClass().getName();
        return String.format("[%s %s %s (%s)] %s  %s",
                controller.isBlank() ? "" : controller.toUpperCase(),
                entryPoint.isBlank() ? "" : entryPoint + " err",
                httpStatus,
                List.of(caption.split("\\.")).getLast(),
                source.isBlank() ? "" : source + ": ",
                getMessage());
    }
}
