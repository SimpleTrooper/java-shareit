package ru.practicum.shareit.base.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.exception.IllegalRequestStateException;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.user.UserController;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;


/**
 * Обработчик ошибок контроллеров
 */
@Slf4j
@RestControllerAdvice(assignableTypes = {UserController.class, ItemRequestController.class,
        ItemController.class, BookingController.class})
public class ErrorHandler {

    /**
     * Обработчик исключения при неверной валидации для MethodArgumentNotValidException
     *
     * @return описание ошибки. код 400
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> validationHandler(final MethodArgumentNotValidException ex) {
        String errorMessage = String.format("Validation error: %s", ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() +
                        ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; ")));
        log.error(errorMessage);
        return new ResponseEntity<>(new ErrorResponse(errorMessage, HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработчик исключения при неверной валидации для ConstraintViolationException
     *
     * @return описание ошибки. код 400
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> validationHandler(final ConstraintViolationException ex) {
        String errorMessage = String.format("Constraint violation error: %s",
                ex.getConstraintViolations()
                        .stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining("; ")));
        log.error(errorMessage);
        return new ResponseEntity<>(new ErrorResponse(errorMessage, HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработчик исключения при ошибке конвертации строки запроса
     *
     * @return описание ошибки. код 400
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> illegalStateHandler(final MethodArgumentTypeMismatchException ex) {
        Throwable root = ExceptionHadnlingUtils.getRootCause(ex);
        String errorMessage = String.format("Argument type mismatch: %s", ex.getMessage());
        if (root.getClass() == IllegalRequestStateException.class) {
            errorMessage = root.getMessage();
        }
        log.error("Argument type mismatch:{}", errorMessage);
        return new ResponseEntity<>(new ErrorResponse(errorMessage, HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработчик остальных исключений
     *
     * @return описание ошибки. код 500
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> allHandler(final Throwable ex) {
        String errorMessage = String.format("Internal server error: %s", ex.getMessage());
        log.error(errorMessage);
        return new ResponseEntity<>(new ErrorResponse(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR.value()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
