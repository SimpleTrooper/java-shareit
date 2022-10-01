package ru.practicum.shareit.booking.validator;

import ru.practicum.shareit.booking.dto.BookingReceivingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BookingTimeValidator implements ConstraintValidator<BookingTimeValid, BookingReceivingDto> {

    @Override
    public void initialize(BookingTimeValid bookingTimeConstraint) {
    }

    @Override
    public boolean isValid(BookingReceivingDto value, ConstraintValidatorContext constraintValidatorContext) {
        return value.getStart().isBefore(value.getEnd());
    }
}
