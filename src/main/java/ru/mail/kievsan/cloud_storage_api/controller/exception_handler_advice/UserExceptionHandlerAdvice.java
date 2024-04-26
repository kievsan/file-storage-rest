package ru.mail.kievsan.cloud_storage_api.controller.exception_handler_advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.mail.kievsan.cloud_storage_api.exception.UserRegisterUserInUseException;
import ru.mail.kievsan.cloud_storage_api.exception.UserSignupIncompleteTransactionException;
import ru.mail.kievsan.cloud_storage_api.model.dto.err.ErrResponse;
import ru.mail.kievsan.cloud_storage_api.exception.UserNotFoundException;

@Slf4j
@RestControllerAdvice
public class UserExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    public ErrResponse errResp(Exception ex) {
        ErrResponse errResp = new ErrResponse(ex.getMessage(), 1);
        log.info("[USER controller error] {}", errResp);
        return errResp;
    }

    @ExceptionHandler(UserSignupIncompleteTransactionException.class)
    public ResponseEntity<ErrResponse> handlerDBTransaction(UserSignupIncompleteTransactionException ex) {
        return new ResponseEntity<>(errResp(ex), HttpStatusCode.valueOf(500));
    }

    @ExceptionHandler(UserRegisterUserInUseException.class)
    public ResponseEntity<ErrResponse> handlerUserInUse(UserRegisterUserInUseException ex) {
        return new ResponseEntity<>(errResp(ex), HttpStatusCode.valueOf(422));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrResponse> handlerUserNotFound(UserNotFoundException ex) {
        return new ResponseEntity<>(errResp(ex), HttpStatusCode.valueOf(404));
    }
}
