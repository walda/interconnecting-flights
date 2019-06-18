package com.ryanair.util;

import com.ryanair.exception.InvalidDateException;
import org.junit.Test;

import java.time.Instant;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.catchThrowable;

public class DateFormatterTest {

    @Test
    public void parseDate() {
        Instant instant = DateFormatter.parse("2019-09-01T10:00").toInstant();
        assertThat(instant.toString()).isEqualTo("2019-09-01T10:00:00Z");
    }

    @Test
    public void parseInvalidDate() {
        Throwable t = catchThrowable( () -> DateFormatter.parse("2019-09T10:00").toInstant());
        assertThat(t).isInstanceOf(InvalidDateException.class);
    }

    @Test
    public void parseDateErrorMessage() {
        Instant instant = DateFormatter.parse("2019-09-01T10:00", "message").toInstant();
        assertThat(instant.toString()).isEqualTo("2019-09-01T10:00:00Z");
    }

    @Test
    public void parseInvalidDateMessage() {
        Throwable t = catchThrowable( () -> DateFormatter.parse("2019-09T10:00", "message").toInstant());
        assertThat(t).isInstanceOf(InvalidDateException.class);
        assertThat(t.toString()).isEqualTo("message");
    }
}
