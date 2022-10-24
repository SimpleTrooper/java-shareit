package ru.practicum.shareit.base.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Формат ошибки для ответа
 */
@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private int statusCode;
}
