package com.ryanair.services;

import com.ryanair.client.ScheduleClient;
import com.ryanair.dto.Flight;
import com.ryanair.dto.Schedule;
import com.ryanair.dto.ScheduleDay;
import com.ryanair.dto.ScheduledFlight;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleServiceTest {

    @Mock
    private ScheduleClient scheduleClient;

    @InjectMocks
    private ScheduleService scheduleService;

    @Test
    public void whenGetFlightSchedule() throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date departureDateTime = sf.parse("2019-09-01T10:00");
        Date arrivalDateTime = sf.parse("2019-09-01T20:00");

        when(scheduleClient.searchFlights("AAA", "BBB", "2019", "9"))
            .thenReturn(createSchedule());

        List<ScheduledFlight> scheduledFlights =
                scheduleService.getFlightSchedule("AAA", "BBB", departureDateTime, arrivalDateTime);

        assertThat(scheduledFlights).isNotNull().isNotEmpty()
                .isEqualTo(Collections.singletonList(
                        new ScheduledFlight("AAA", "BBB", sf.parse("2019-09-01T14:00"), sf.parse("2019-09-01T17:00")))
                );

        verify(scheduleClient).searchFlights("AAA", "BBB", "2019", "9");

    }

    @Test
    public void whenGetFlightScheduleForSeveralMonths() throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date departureDateTime = sf.parse("2019-12-01T10:00");
        Date arrivalDateTime = sf.parse("2020-02-01T20:00");

        when(scheduleClient.searchFlights(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(createSchedule());


        List<ScheduledFlight> scheduledFlights =
                scheduleService.getFlightSchedule("AAA", "BBB", departureDateTime, arrivalDateTime);

        assertThat(scheduledFlights).isNotNull().isNotEmpty()
                .isEqualTo(Arrays.asList(
                        new ScheduledFlight("AAA", "BBB", sf.parse("2019-12-01T13:00"), sf.parse("2019-12-01T16:00")),
                        new ScheduledFlight("AAA", "BBB", sf.parse("2020-01-01T13:00"), sf.parse("2020-01-01T16:00")),
                        new ScheduledFlight("AAA", "BBB", sf.parse("2020-02-01T13:00"), sf.parse("2020-02-01T16:00")))
                );

        verify(scheduleClient).searchFlights("AAA", "BBB", "2019", "12");
        verify(scheduleClient).searchFlights("AAA", "BBB", "2020", "1");
        verify(scheduleClient).searchFlights("AAA", "BBB", "2020", "2");

    }

    @Test
    public void whenGetFlightScheduleAndScheduleHaveNoDays() throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date departureDateTime = sf.parse("2019-09-01T10:00");
        Date arrivalDateTime = sf.parse("2019-09-01T20:00");

        when(scheduleClient.searchFlights("AAA", "BBB", "2019", "9"))
                .thenReturn(new Schedule());

        List<ScheduledFlight> scheduledFlights =
                scheduleService.getFlightSchedule("AAA", "BBB", departureDateTime, arrivalDateTime);

        assertThat(scheduledFlights).isNotNull().isEmpty();

        verify(scheduleClient).searchFlights("AAA", "BBB", "2019", "9");
    }

    @Test
    public void whenGetFlightScheduleAndDepartureTimeIsAfterScheduledFlight() throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date departureDateTime = sf.parse("2019-09-01T14:00");
        Date arrivalDateTime = sf.parse("2019-09-01T20:00");

        when(scheduleClient.searchFlights("AAA", "BBB", "2019", "9"))
                .thenReturn(createSchedule());

        List<ScheduledFlight> scheduledFlights =
                scheduleService.getFlightSchedule("AAA", "BBB", departureDateTime, arrivalDateTime);

        assertThat(scheduledFlights).isNotNull().isEmpty();

        verify(scheduleClient).searchFlights("AAA", "BBB", "2019", "9");
    }

    @Test
    public void whenGetFlightScheduleAndArrivalTimeIsBeforeScheduledFlight() throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date departureDateTime = sf.parse("2019-09-01T10:00");
        Date arrivalDateTime = sf.parse("2019-09-01T14:00");

        when(scheduleClient.searchFlights("AAA", "BBB", "2019", "9"))
                .thenReturn(createSchedule());

        List<ScheduledFlight> scheduledFlights =
                scheduleService.getFlightSchedule("AAA", "BBB", departureDateTime, arrivalDateTime);

        assertThat(scheduledFlights).isNotNull().isEmpty();

        verify(scheduleClient).searchFlights("AAA", "BBB", "2019", "9");
    }

    @After
    public void tearDown() {
        verifyNoMoreInteractions(scheduleClient);
    }

    private static Schedule createSchedule() {
        Schedule schedule = new Schedule();

        schedule.setDays(Collections.singletonList(createScheduleDay()));

        return schedule;
    }

    private static ScheduleDay createScheduleDay() {
        ScheduleDay scheduleDay = new ScheduleDay();
        scheduleDay.setDay(1);
        scheduleDay.setFlights(Collections.singletonList(createFlight()));

        return scheduleDay;
    }

    private static Flight createFlight() {
        Flight flight = new Flight();
        flight.setArrivalTime("15:00");
        flight.setDepartureTime("12:00");
        flight.setNumber("2310");

        return flight;
    }
}
