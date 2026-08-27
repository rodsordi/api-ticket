package br.com.cielo.commons.advice;

import br.com.cielo.commons.exception.AlreadyExistsException;
import br.com.cielo.commons.exception.BusinessException;
import br.com.cielo.commons.exception.InternalErrorException;
import br.com.cielo.commons.exception.ResourceNotFoundException;
import br.com.cielo.commons.exception.TooManyRequestsException;
import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_CONTENT;

@Slf4j
@Hidden
@Order(HIGHEST_PRECEDENCE)
@RestControllerAdvice
@Observed
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handle(ConstraintViolationException e) {
        log.warn(e.getMessage());
        var message = e.getConstraintViolations().stream()
                .map(v -> String.format("[%s]: '%s' is invalid. Reason: %s",
                        v.getPropertyPath(),
                        v.getInvalidValue(),
                        v.getMessage()))
                .findFirst()
                .orElse("Validation error in submitted data.");

        var problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, message);
        for (var violation : e.getConstraintViolations()) {
            problemDetail.setProperty(violation.getPropertyPath().toString(),
                    String.format("Sent value: '%s'. Error: %s", violation.getInvalidValue(), violation.getMessage()));
        }
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handle(MethodArgumentNotValidException e) {
        log.warn(e.getMessage());
        ProblemDetail problemDetail = null;

        var fieldErrors = e.getBindingResult().getFieldErrors();
        if (!fieldErrors.isEmpty()) {
            for (var fieldError : fieldErrors) {
                Object invalidValue = fieldError.getRejectedValue();

                if (problemDetail == null) {
                    var message = String.format("[%s]: '%s' is invalid. Reason: %s",
                            fieldError.getField(), invalidValue, fieldError.getDefaultMessage());
                    problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, message);
                }
                problemDetail.setProperty(fieldError.getField(), String.format("Sent value: '%s'. Error: %s", invalidValue, fieldError.getDefaultMessage()));
            }
        } else {
            for (var error : e.getBindingResult().getGlobalErrors()) {
                if (problemDetail == null) {
                    var message = String.format("[%s]: %s", error.getObjectName(), error.getDefaultMessage());
                    problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, message);
                }
                problemDetail.setProperty(error.getObjectName(), error.getDefaultMessage());
            }
        }

        if (problemDetail == null) {
            problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, "Validation error in submitted data.");
        }

        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handle(MethodArgumentTypeMismatchException e) {
        log.warn(e.getMessage());
        var problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
        if (e.getPropertyName() != null && e.getCause() != null) {
            problemDetail.setProperty(e.getPropertyName(), e.getCause().getMessage());
        }
        return problemDetail;
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ProblemDetail handle(MissingRequestHeaderException e) {
        log.warn(e.getMessage());
        return ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail handle(MissingServletRequestParameterException e) {
        log.warn(e.getMessage());
        return ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail handle(HttpRequestMethodNotSupportedException e) {
        log.warn(e.getMessage());
        return ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handle(HttpMessageNotReadableException e) {
        log.warn(e.getMessage());
        return ProblemDetail.forStatusAndDetail(BAD_REQUEST, "Malformed JSON request payload.");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ProblemDetail handle(HttpMediaTypeNotSupportedException e) {
        log.warn(e.getMessage());
        return ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handle(ResourceNotFoundException e) {
        log.warn(e.getMessage());
        return ProblemDetail.forStatusAndDetail(NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ProblemDetail handle(AlreadyExistsException e) {
        log.warn(e.getMessage());
        return ProblemDetail.forStatusAndDetail(UNPROCESSABLE_CONTENT, e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handle(BusinessException e) {
        if (e.getCause() != null)
            log.warn(e.getMessage(), e);
        else
            log.warn(e.getMessage());
        return ProblemDetail.forStatusAndDetail(UNPROCESSABLE_CONTENT, e.getMessage());
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ProblemDetail handle(TooManyRequestsException e) {
        log.warn(e.getMessage());
        return ProblemDetail.forStatusAndDetail(TOO_MANY_REQUESTS, e.getMessage());
    }

    @ExceptionHandler(InternalErrorException.class)
    public ProblemDetail handle(InternalErrorException e) {
        log.error(e.getMessage(), e);
        return ProblemDetail.forStatusAndDetail(INTERNAL_SERVER_ERROR, "Internal error");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handle(Exception e) {
        log.error(e.getMessage(), e);
        return ProblemDetail.forStatusAndDetail(INTERNAL_SERVER_ERROR, "Internal error");
    }
}
