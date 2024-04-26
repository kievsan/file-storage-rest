package ru.mail.kievsan.cloud_storage_api.controller.exception_handler_advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.mail.kievsan.cloud_storage_api.exception.AuthNotAuthenticateException;
import ru.mail.kievsan.cloud_storage_api.exception.AuthUserNotFoundException;
import ru.mail.kievsan.cloud_storage_api.model.dto.err.ErrResponse;

@Slf4j
@RestControllerAdvice
public class AuthExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    public ErrResponse errResp(Exception ex) {
        ErrResponse errResp = new ErrResponse(ex.getMessage(), 0);
        log.info("[AUTH controller error] {}", errResp);
        return errResp;
    }

    @ExceptionHandler(AuthUserNotFoundException.class)
    public ResponseEntity<ErrResponse> handlerUserNotFound(AuthUserNotFoundException ex) {
        return new ResponseEntity<>(errResp(ex), HttpStatusCode.valueOf(404));
    }

    @ExceptionHandler(AuthNotAuthenticateException.class)
    public ResponseEntity<ErrResponse> handlerUserNotAuthenticate(AuthNotAuthenticateException ex) {
        return new ResponseEntity<>(errResp(ex), HttpStatusCode.valueOf(401));
    }
}
