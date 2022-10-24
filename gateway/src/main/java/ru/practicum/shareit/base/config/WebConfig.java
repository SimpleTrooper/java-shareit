package ru.practicum.shareit.base.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.practicum.shareit.booking.converter.StringToApprovedStateConverter;
import ru.practicum.shareit.booking.converter.StringToBookingRequestStateConverter;

/**
 * Дополнительная конфигурация Spring MVC
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToApprovedStateConverter());
        registry.addConverter(new StringToBookingRequestStateConverter());
    }
}
