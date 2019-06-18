package com.ryanair.controller;

import com.ryanair.dto.Itinerary;
import com.ryanair.exception.InvalidDateException;
import com.ryanair.services.InterconnectionsService;
import com.ryanair.validator.DatesValidator;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InterconnectionsControllerTest {

    @Mock
    private DatesValidator datesValidator;

    @Mock
    private InterconnectionsService interconnectionsService;

    @InjectMocks
    private InterconnectionsController interconnectionsController;

    @Test
    public void whenFlightsAreSearchInterconnectionsAreReturned() throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        sf.setTimeZone(TimeZone.getTimeZone("UTC"));

        when(interconnectionsService.searchInterconnections("AAA", "BBB", sf.parse("2019-09-01T05:00"), sf.parse("2019-09-01T07:00")))
        .thenReturn(Collections.singletonList(new Itinerary(0, Collections.emptyList())));

        interconnectionsController.searchInterconnections("AAA", "BBB", "2019-09-01T05:00", "2019-09-01T07:00");

        verify(datesValidator).checkDates(sf.parse("2019-09-01T05:00"), sf.parse("2019-09-01T07:00"));
        verify(interconnectionsService).searchInterconnections("AAA", "BBB", sf.parse("2019-09-01T05:00"), sf.parse("2019-09-01T07:00"));

    }

    @After
    public void tearDown() {
        verifyNoMoreInteractions(interconnectionsService, datesValidator);
    }

}
