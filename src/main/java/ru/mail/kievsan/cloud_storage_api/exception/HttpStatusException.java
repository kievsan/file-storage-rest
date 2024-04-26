package ru.mail.kievsan.cloud_storage_api.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@RequiredArgsConstructor
public class HttpStatusException extends RuntimeException {

    private final String message;
    private final HttpStatus httpStatus;
}
