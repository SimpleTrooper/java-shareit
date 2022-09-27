package ru.practicum.shareit.base.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.base.exception.IllegalRequestStateException;
import ru.practicum.shareit.base.exception.NotFoundException;
import ru.practicum.shareit.base.exception.ResourceAccessException;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.exception.BookingCreationException;
import ru.practicum.shareit.booking.exception.BookingStatusException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.exception.CommentCreationException;
import ru.practicum.shareit.item.exception.ItemUnavailableException;
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
     * Обработчик исключений не найденной записи
     *
     * @param ex
     * @return описание ошибки, код 404
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> notFoundHandler(final NotFoundException ex) {
        String errorMessage = String.format("Entity is not found: %s", ex.getMessage());
        log.error(errorMessage);
        return new ResponseEntity<>(new ErrorResponse(errorMessage, HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
    }

    /**
     * Обработчик исключений при дублирующихся уникальных значениях для записей
     *
     * @param ex
     * @return описание ошибки, код 409
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> duplicateHandler(final DataIntegrityViolationException ex) {
        String errorMessage = String.format("Duplicate key conflict: %s", ex.getMostSpecificCause().getMessage());
        log.error(errorMessage);
        return new ResponseEntity<>(new ErrorResponse(errorMessage, HttpStatus.CONFLICT.value()), HttpStatus.CONFLICT);
    }

    /**
     * Обработчик исключении при запрете доступа к ресурсу
     *
     * @param ex
     * @return описание ошибки, код 404
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> accessHandler(final ResourceAccessException ex) {
        String errorMessage = String.format("Access is forbidden: %s", ex.getMessage());
        log.error(errorMessage);
        return new ResponseEntity<>(new ErrorResponse(errorMessage, HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
    }

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
     * Обработчик исключения при попытке забронировать недоступную вещь
     *
     * @return описание ошибки. код 400
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> unavailableHandler(final ItemUnavailableException ex) {
        String errorMessage = String.format("Item is unavailable error: %s", ex.getMessage());
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
     * Обработчик исключения при попытке подтвердить статус для не-WAITING бронирования
     *
     * @return описание ошибки. код 400
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> illegalBookingStatusChangeHandler(final BookingStatusException ex) {
        String errorMessage = String.format("Booking status error: %s", ex.getMessage());
        log.error(errorMessage);
        return new ResponseEntity<>(new ErrorResponse(errorMessage, HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработчик исключений при ошибках создания бронирования
     *
     * @return описание ошибки. код 404
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> bookingCreationHandler(final BookingCreationException ex) {
        String errorMessage = String.format("Booking creation error: %s", ex.getMessage());
        log.error(errorMessage);
        return new ResponseEntity<>(new ErrorResponse(errorMessage, HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND);
    }

    /**
     * Обработчик исключений при ошибках создания комментария
     *
     * @return описание ошибки. код 400
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> commentCreationHandler(final CommentCreationException ex) {
        String errorMessage = String.format("Comment creation error: %s", ex.getMessage());
        log.error(errorMessage);
        return new ResponseEntity<>(new ErrorResponse(errorMessage, HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
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
