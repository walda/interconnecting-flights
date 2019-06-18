package com.ryanair;

import com.ryanair.dto.Itinerary;
import com.ryanair.exception.InvalidDateException;
import com.ryanair.services.InterconnectionsService;
import com.ryanair.validator.DatesValidator;
import com.ryanair.controller.InterconnectionsController;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.TimeZone;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(InterconnectionsController.class)
public class MvcTest {

    @MockBean
    private InterconnectionsService interconnectionsService;

    @MockBean
    private DatesValidator datesValidator;

    @Autowired
    private MockMvc mvc;

    @Test
    public void whenFlightsAreSearch200IsReturned() throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        when(interconnectionsService.searchInterconnections("ALC", "STN", simpleDateFormat.parse("2019-09-01T05:00"), simpleDateFormat.parse("2019-09-01T21:30")))
                .thenReturn(Collections.singletonList(new Itinerary(0, Collections.emptyList())));

        mvc.perform(get("/interconnections?departure=ALC&arrival=STN&departureDateTime=2019-09-01T05:00&arrivalDateTime=2019-09-01T21:30"))
            .andExpect(status().isOk())
            .andExpect(content().json("[{ \"stops\": 0, legs: [] }]"));

        verify(interconnectionsService).searchInterconnections("ALC", "STN", simpleDateFormat.parse("2019-09-01T05:00"), simpleDateFormat.parse("2019-09-01T21:30"));
        verify(datesValidator).checkDates(simpleDateFormat.parse("2019-09-01T05:00"), simpleDateFormat.parse("2019-09-01T21:30"));
    }

    @Test
    public void whenInvalidDepartureDate400isReturned() throws Exception {
        mvc.perform(get("/interconnections?departure=ALC&arrival=STN&departureDateTime=2019-09T05:00&arrivalDateTime=2019-09-01T21:30"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid departureDateTime format"));
    }

    @Test
    public void whenInvalidArrivalDate400isReturned() throws Exception {
        mvc.perform(get("/interconnections?departure=ALC&arrival=STN&departureDateTime=2019-09-01T05:00&arrivalDateTime=2019-09T21:30"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid arrivalDateTime format"));
    }

    @Test
    public void whenDepartureTimeIsAfterArrivalTime400isReturned() throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        doThrow(new InvalidDateException("Arrival date cannot be before departure date"))
                .when(datesValidator).checkDates(simpleDateFormat.parse("2019-09-01T05:00"), simpleDateFormat.parse("2019-09-01T04:30"));

        mvc.perform(get("/interconnections?departure=ALC&arrival=STN&departureDateTime=2019-09-01T05:00&arrivalDateTime=2019-09-01T04:30"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Arrival date cannot be before departure date"));

        verify(datesValidator).checkDates(simpleDateFormat.parse("2019-09-01T05:00"), simpleDateFormat.parse("2019-09-01T04:30"));
    }

    @After
    public void tearDown() {
        verifyNoMoreInteractions(interconnectionsService, datesValidator);
    }
}
