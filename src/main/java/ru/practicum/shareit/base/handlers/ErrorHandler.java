package ru.practicum.shareit.base.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.base.exceptions.DuplicateKeyException;
import ru.practicum.shareit.base.exceptions.NotFoundException;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.item.exception.InvalidItemOwnerException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.user.controller.UserController;

import javax.validation.ConstraintViolationException;

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
    public ResponseEntity<String> notFoundHandler(final NotFoundException ex) {
        String errorMessage = String.format("Entity is not found: %s", ex.getMessage());
        log.error(errorMessage);
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    /**
     * Обработчик исключений при дублирующихся уникальных значениях для записей
     *
     * @param ex
     * @return описание ошибки, код 409
     */
    @ExceptionHandler
    public ResponseEntity<String> duplicateHandler(final DuplicateKeyException ex) {
        String errorMessage = String.format("Duplicate key conflict: %s", ex.getMessage());
        log.error(errorMessage);
        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
    }

    /**
     * Обработчик исключении при запрете доступа к ресурсу
     *
     * @param ex
     * @return описание ошибки, код 403
     */
    @ExceptionHandler
    public ResponseEntity<String> accessHandler(final InvalidItemOwnerException ex) {
        String errorMessage = String.format("Access is forbidden: %s", ex.getMessage());
        log.error(errorMessage);
        return new ResponseEntity<>(errorMessage, HttpStatus.FORBIDDEN);
    }

    /**
     * Обработчик исключения при неверной валидации
     *
     * @return описание ошибки. код 400
     */
    @ExceptionHandler
    public ResponseEntity<String> validationHandler(ConstraintViolationException ex) {
        String errorMessage = String.format("Validation error: %s", ex.getMessage());
        log.error(errorMessage);
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработчик остальных Runtime исключений
     *
     * @return описание ошибки. код 500
     */
    @ExceptionHandler
    public ResponseEntity<String> runtimeHandler(RuntimeException ex) {
        String errorMessage = String.format("Runtime error: %s", ex.getMessage());
        log.error(errorMessage);
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
