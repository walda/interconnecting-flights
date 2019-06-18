package com.ryanair.controller;

import com.ryanair.exception.InvalidDateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Java6Assertions.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class InterconnectionsControllerAdviceTest {

    @Test
    public void whenInvalidDateExceptionThenResponseIsReturned() {
        InterconnectionsControllerAdvice interconnectionsControllerAdvice =
                new InterconnectionsControllerAdvice();

        ResponseEntity<Object> responseEntity =
                interconnectionsControllerAdvice.invalidDate(new InvalidDateException("Message"));

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo("Message");
    }

}
