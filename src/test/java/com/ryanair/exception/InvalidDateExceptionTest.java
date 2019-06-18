package com.ryanair.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class InvalidDateExceptionTest {

    @Test
    public void whenInvalidExceptionIsCreatedToStringIsOverriden() {
        InvalidDateException invalidDateException = new InvalidDateException("message");

        assertThat(invalidDateException.toString()).isEqualTo("message");
    }

}
