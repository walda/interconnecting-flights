package com.ryanair.services;

import com.ryanair.dto.Flight;
import com.ryanair.dto.Itinerary;
import com.ryanair.dto.Route;
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
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InterconnectionsServiceTest {

    @Mock
    private RoutesService routesService;
    @Mock
    private ScheduleService scheduleService;
    @InjectMocks
    private InterconnectionsService interconnectionsService;

    @Test
    public void whenSearchInterconnections() throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date departureDateTime = sf.parse("2019-09-01T10:00");
        Date arrivalDateTime = sf.parse("2019-09-01T20:00");

        ScheduledFlight scheduledFlightDirect = new ScheduledFlight("AAA", "CCC", departureDateTime, arrivalDateTime);
        ScheduledFlight scheduledFlightFirstLeg = new ScheduledFlight("AAA", "BBB", sf.parse("2019-09-01T10:00"), sf.parse("2019-09-01T11:00"));
        ScheduledFlight scheduledFlightSecondLeg = new ScheduledFlight("BBB", "CCC", sf.parse("2019-09-01T15:00"), sf.parse("2019-09-01T18:00"));

        when(routesService.getFilteredRoutes()).thenReturn(Arrays.asList(createFirstRoute(), createSecondRoute(), createThirdRoute()));
        when(scheduleService.getFlightSchedule(anyString(), anyString(), any(), any()))
                .thenReturn(Collections.singletonList(scheduledFlightDirect), Collections.singletonList(scheduledFlightFirstLeg), Collections.singletonList(scheduledFlightSecondLeg));

        List<Itinerary> itineraries =
            interconnectionsService.searchInterconnections("AAA", "CCC", departureDateTime, arrivalDateTime);

        assertThat(itineraries).isNotNull().isNotEmpty().hasSize(2)
                .isEqualTo(
                        Arrays.asList(
                                new Itinerary(0, Collections.singletonList(scheduledFlightDirect)),
                                new Itinerary(1, Arrays.asList(scheduledFlightFirstLeg, scheduledFlightSecondLeg)))
                        );

        verifyCalls(departureDateTime, arrivalDateTime);
    }

    @Test
    public void whenSearchInterconnectionsOnlyDirectFlight() throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date departureDateTime = sf.parse("2019-09-01T10:00");
        Date arrivalDateTime = sf.parse("2019-09-01T20:00");

        ScheduledFlight scheduledFlightDirect = new ScheduledFlight("AAA", "CCC", departureDateTime, arrivalDateTime);

        when(routesService.getFilteredRoutes()).thenReturn(Arrays.asList(createFirstRoute(), createSecondRoute(), createThirdRoute()));
        when(scheduleService.getFlightSchedule(anyString(), anyString(), any(), any()))
                .thenReturn(Collections.singletonList(scheduledFlightDirect), Collections.emptyList());

        List<Itinerary> itineraries =
                interconnectionsService.searchInterconnections("AAA", "CCC", departureDateTime, arrivalDateTime);

        assertThat(itineraries).isNotNull().isNotEmpty().hasSize(1)
                .isEqualTo(
                        Arrays.asList(
                                new Itinerary(0, Collections.singletonList(scheduledFlightDirect)))
                );

        verifyCalls(departureDateTime, arrivalDateTime);
    }

    @Test
    public void whenSearchInterconnectionsOnlyInterconnectionFlight() throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date departureDateTime = sf.parse("2019-09-01T10:00");
        Date arrivalDateTime = sf.parse("2019-09-01T20:00");

        ScheduledFlight scheduledFlightFirstLeg = new ScheduledFlight("AAA", "BBB", sf.parse("2019-09-01T10:00"), sf.parse("2019-09-01T11:00"));
        ScheduledFlight scheduledFlightSecondLeg = new ScheduledFlight("BBB", "CCC", sf.parse("2019-09-01T15:00"), sf.parse("2019-09-01T18:00"));

        when(routesService.getFilteredRoutes()).thenReturn(Arrays.asList(createFirstRoute(), createSecondRoute(), createThirdRoute()));
        when(scheduleService.getFlightSchedule(anyString(), anyString(), any(), any()))
                .thenReturn(Collections.emptyList(), Collections.singletonList(scheduledFlightFirstLeg), Collections.singletonList(scheduledFlightSecondLeg));

        List<Itinerary> itineraries =
                interconnectionsService.searchInterconnections("AAA", "CCC", departureDateTime, arrivalDateTime);

        assertThat(itineraries).isNotNull().isNotEmpty().hasSize(1)
                .isEqualTo(
                        Arrays.asList(
                                new Itinerary(1, Arrays.asList(scheduledFlightFirstLeg, scheduledFlightSecondLeg)))
                );

        verifyCalls(departureDateTime, arrivalDateTime);
    }

    @Test
    public void whenSearchInterconnectionsOnlyInterconnectionFlightWithLessThan2Hours() throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date departureDateTime = sf.parse("2019-09-01T10:00");
        Date arrivalDateTime = sf.parse("2019-09-01T20:00");

        ScheduledFlight scheduledFlightFirstLeg = new ScheduledFlight("AAA", "BBB", sf.parse("2019-09-01T10:00"), sf.parse("2019-09-01T11:00"));
        ScheduledFlight scheduledFlightSecondLeg = new ScheduledFlight("BBB", "CCC", sf.parse("2019-09-01T11:00"), sf.parse("2019-09-01T18:00"));

        when(routesService.getFilteredRoutes()).thenReturn(Arrays.asList(createFirstRoute(), createSecondRoute(), createThirdRoute()));
        when(scheduleService.getFlightSchedule(anyString(), anyString(), any(), any()))
                .thenReturn(Collections.emptyList(), Collections.singletonList(scheduledFlightFirstLeg), Collections.singletonList(scheduledFlightSecondLeg));

        List<Itinerary> itineraries =
                interconnectionsService.searchInterconnections("AAA", "CCC", departureDateTime, arrivalDateTime);

        assertThat(itineraries).isNotNull().isEmpty();
        verifyCalls(departureDateTime, arrivalDateTime);

    }

    @Test
    public void whenSearchInterconnectionsOnlyInterconnectionFlightWithMoreThan6Hours() throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date departureDateTime = sf.parse("2019-09-01T10:00");
        Date arrivalDateTime = sf.parse("2019-09-01T20:00");

        ScheduledFlight scheduledFlightFirstLeg = new ScheduledFlight("AAA", "BBB", sf.parse("2019-09-01T10:00"), sf.parse("2019-09-01T11:00"));
        ScheduledFlight scheduledFlightSecondLeg = new ScheduledFlight("BBB", "CCC", sf.parse("2019-09-01T17:00"), sf.parse("2019-09-01T18:00"));

        when(routesService.getFilteredRoutes()).thenReturn(Arrays.asList(createFirstRoute(), createSecondRoute(), createThirdRoute()));
        when(scheduleService.getFlightSchedule(anyString(), anyString(), any(), any()))
                .thenReturn(Collections.emptyList(), Collections.singletonList(scheduledFlightFirstLeg), Collections.singletonList(scheduledFlightSecondLeg));

        List<Itinerary> itineraries =
                interconnectionsService.searchInterconnections("AAA", "CCC", departureDateTime, arrivalDateTime);

        assertThat(itineraries).isNotNull().isEmpty();
        verifyCalls(departureDateTime, arrivalDateTime);

    }

    @Test
    public void whenSearchInterconnectionsNoScheduledFlights() throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date departureDateTime = sf.parse("2019-09-01T10:00");
        Date arrivalDateTime = sf.parse("2019-09-01T20:00");

        when(routesService.getFilteredRoutes()).thenReturn(Arrays.asList(createFirstRoute(), createSecondRoute(), createThirdRoute()));
        when(scheduleService.getFlightSchedule(anyString(), anyString(), any(), any()))
                .thenReturn(Collections.emptyList());

        List<Itinerary> itineraries =
                interconnectionsService.searchInterconnections("AAA", "CCC", departureDateTime, arrivalDateTime);

        assertThat(itineraries).isNotNull().isEmpty();
        verifyCalls(departureDateTime, arrivalDateTime);

    }

    @Test
    public void whenSearchInterconnectionsNoRoutes() throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date departureDateTime = sf.parse("2019-09-01T10:00");
        Date arrivalDateTime = sf.parse("2019-09-01T20:00");

        when(routesService.getFilteredRoutes()).thenReturn(Collections.emptyList());

        List<Itinerary> itineraries =
                interconnectionsService.searchInterconnections("AAA", "CCC", departureDateTime, arrivalDateTime);

        assertThat(itineraries).isNotNull().isEmpty();
        verify(routesService).getFilteredRoutes();

    }

    private void verifyCalls(Date departureDateTime, Date arrivalDateTime) {
        verify(routesService).getFilteredRoutes();
        verify(scheduleService).getFlightSchedule("AAA", "CCC", departureDateTime, arrivalDateTime);
        verify(scheduleService).getFlightSchedule("AAA", "BBB", departureDateTime, arrivalDateTime);
        verify(scheduleService).getFlightSchedule("BBB", "CCC", departureDateTime, arrivalDateTime);
    }

    @After
    public void tearDown() {
        verifyNoMoreInteractions(routesService, scheduleService);
    }

    private static Route createFirstRoute() {
        return Route.builder()
                .airportFrom("AAA")
                .airportTo("CCC")
                .operator("RYANAIR")
                .build();
    }

    private static Route createSecondRoute() {
        return Route.builder()
                .airportFrom("AAA")
                .airportTo("BBB")
                .operator("RYANAIR")
                .build();
    }

    private static Route createThirdRoute() {
        return Route.builder()
                .airportFrom("BBB")
                .airportTo("CCC")
                .operator("RYANAIR")
                .build();
    }

}
