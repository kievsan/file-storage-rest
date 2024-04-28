package ru.mail.kievsan.cloud_storage_api.controller.exception_handler_advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.mail.kievsan.cloud_storage_api.exception.InputDataException;
import ru.mail.kievsan.cloud_storage_api.exception.UnauthorizedUserException;
import ru.mail.kievsan.cloud_storage_api.model.dto.err.ErrResponse;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    public ErrResponse errResp(Exception ex, int id) {
        ErrResponse errResp = new ErrResponse(ex.getMessage(), id);
        log.error("[Runtime exception] {}", errResp);
        return errResp;
    }

    @ExceptionHandler(InputDataException.class)
    public ResponseEntity<ErrResponse> handlerErrInputData(InputDataException ex) {
        return new ResponseEntity<>(errResp(ex, 400), HttpStatusCode.valueOf(400));
    }

    @ExceptionHandler(UnauthorizedUserException.class)
    public ResponseEntity<ErrResponse> handlerUnauthorizedUser(UnauthorizedUserException ex) {
        return new ResponseEntity<>(errResp(ex, 401), HttpStatusCode.valueOf(401));
    }
}
