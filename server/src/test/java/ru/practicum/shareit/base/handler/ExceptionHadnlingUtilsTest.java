package ru.practicum.shareit.base.handler;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.base.exception.IllegalRequestStateException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Юнит тесты для утилитарного класса по работе с исключениями
 */
class ExceptionHadnlingUtilsTest {

    /**
     * Стандартное поведение
     */
    @Test
    void shouldReturnRootCause() {
        Exception cause = new Exception();
        IllegalRequestStateException requestStateException = new IllegalRequestStateException("test", cause);

        Throwable actual = ExceptionHadnlingUtils.getRootCause(requestStateException);

        assertThat(actual, equalTo(cause));
    }

    /**
     * Поведение при root = exception
     */
    @Test
    void shouldReturnNullWhenRootIsNull() {
        IllegalRequestStateException requestStateException = new IllegalRequestStateException("test");

        Throwable actual = ExceptionHadnlingUtils.getRootCause(requestStateException);

        assertThat(actual, equalTo(requestStateException));
    }
}