package ru.mail.kievsan.cloud_storage_api.controller.exception_handler_advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.mail.kievsan.cloud_storage_api.exception.InputDataException;
import ru.mail.kievsan.cloud_storage_api.model.dto.err.ErrResponse;

@Slf4j
@RestControllerAdvice
public class FileExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    public ErrResponse errResp(Exception ex, int id) {
        ErrResponse errResp = new ErrResponse(ex.getMessage(), id);
        log.info("[FILE controller error] {}", errResp);
        return errResp;
    }
}
