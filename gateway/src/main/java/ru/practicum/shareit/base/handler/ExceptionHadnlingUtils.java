package ru.practicum.shareit.base.handler;

/**
 * Утилитарный класс для работы с исключениями
 */
public class ExceptionHadnlingUtils {
    public static Throwable getRootCause(Throwable ex) {
        if (ex == null) {
            return null;
        }
        Throwable rootCause = ex;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }
}
