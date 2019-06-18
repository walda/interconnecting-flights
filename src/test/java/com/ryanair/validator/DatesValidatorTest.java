package com.ryanair.validator;

import com.ryanair.exception.InvalidDateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.catchThrowable;

@RunWith(MockitoJUnitRunner.class)
public class DatesValidatorTest {

    @InjectMocks
    private DatesValidator datesValidator;

    @Test
    public void checkDates() throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        datesValidator.checkDates(sf.parse("2019-09-01T10:00"), sf.parse("2019-09-01T16:00"));
    }

    @Test
    public void checkInvalidDates() throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Throwable t = catchThrowable( () -> datesValidator.checkDates(sf.parse("2019-09-05T10:00"), sf.parse("2019-09-01T16:00")));

        assertThat(t).isInstanceOf(InvalidDateException.class);
        assertThat(t.toString()).isEqualTo("Arrival date cannot be before departure date");
    }

}
