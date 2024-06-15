package ru.mail.kievsan.cloud_storage_api.controller.exception_handler_advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.mail.kievsan.cloud_storage_api.exception.*;
import ru.mail.kievsan.cloud_storage_api.model.dto.err.ErrResponse;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    public ErrResponse errResp(AdviceException ex) {
        log.error(ex.log());
        return new ErrResponse(ex.getMessage(), 0);
    }

    @ExceptionHandler(InputDataException.class)         // 400
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrResponse> handlerErrInputData(InputDataException ex) {
        return new ResponseEntity<>(errResp(ex), ex.getHttpStatus());
    }

    @ExceptionHandler(UnauthorizedUserException.class)   // 401
    public ResponseEntity<ErrResponse> handlerUnauthorizedUser(UnauthorizedUserException ex) {
        return new ResponseEntity<>(errResp(ex), ex.getHttpStatus());
    }

    @ExceptionHandler(NoRightsException.class)   // 403
    public ResponseEntity<ErrResponse> handlerNoRightsErr(NoRightsException ex) {
        return new ResponseEntity<>(errResp(ex), ex.getHttpStatus());
    }

    @ExceptionHandler(UserNotFoundException.class)      // 404
    public ResponseEntity<ErrResponse> handlerUserNotFound(UserNotFoundException ex) {
        return new ResponseEntity<>(errResp(ex), ex.getHttpStatus());
    }

    @ExceptionHandler(UserRegistrationException.class) // 422, 500
    public ResponseEntity<ErrResponse> handlerUserRegistrationErr(UserRegistrationException ex) {
        return new ResponseEntity<>(errResp(ex), ex.getHttpStatus());
    }

    @ExceptionHandler(InternalServerException.class)    // 500
    public ResponseEntity<ErrResponse> handlerServerErr(InternalServerException ex) {
        return new ResponseEntity<>(errResp(ex), ex.getHttpStatus());
    }

    @ExceptionHandler(NotAuthenticateException.class)    // 500
    public ResponseEntity<ErrResponse> handlerServerErr(NotAuthenticateException ex) {
        return new ResponseEntity<>(errResp(ex), ex.getHttpStatus());
    }
}
