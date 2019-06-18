package com.ryanair.validator;

import com.ryanair.exception.InvalidDateException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DatesValidator {

    public void checkDates(Date departureDate, Date arrivalDate) throws InvalidDateException {
        if(arrivalDate.toInstant().isBefore(departureDate.toInstant())) {
            throw new InvalidDateException("Arrival date cannot be before departure date");
        }
    }
}
