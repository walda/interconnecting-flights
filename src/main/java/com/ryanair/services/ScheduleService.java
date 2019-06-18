package com.ryanair.services;

import com.ryanair.client.ScheduleClient;
import com.ryanair.dto.Flight;
import com.ryanair.dto.Schedule;
import com.ryanair.dto.ScheduledFlight;
import com.ryanair.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class ScheduleService {

    private final ScheduleClient scheduleClient;

    List<ScheduledFlight> getFlightSchedule(String departureAirport,
                                                    String arrivalAirport,
                                                    Date departureDateTime,
                                                    Date arrivalDateTime) {

        return  calculateSearchableDates(departureDateTime, arrivalDateTime).stream()
                .map(period -> searchScheduledFlights(departureAirport, arrivalAirport, period.getLeft(), period.getRight()))
                .flatMap(Collection::stream)
                .filter(scheduledFlight -> scheduledFlight.getDepartureDateTime().toInstant().isAfter(departureDateTime.toInstant()))
                .filter(scheduledFlight -> scheduledFlight.getArrivalDateTime().toInstant().isBefore(arrivalDateTime.toInstant()))
                .collect(Collectors.toList());
    }

    private List<ScheduledFlight> searchScheduledFlights(String departureAirport,
                                                                String arrivalAirport,
                                                                Integer year,
                                                                Integer month) {
        Schedule schedule = scheduleClient.searchFlights(departureAirport, arrivalAirport, year.toString(), month.toString());

        if (schedule.getDays() == null) {
            return Collections.emptyList();
        }

        return schedule.getDays().stream()
                .map(scheduleDay -> getScheduledFlights(departureAirport, arrivalAirport, year, month, scheduleDay.getDay(), scheduleDay.getFlights()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static List<Pair<Integer, Integer>> calculateSearchableDates(Date departureDateTime, Date arrivalDateTime) {
        List<Pair<Integer, Integer>> dates = new ArrayList<>();
        LocalDateTime departureLocalTime = departureDateTime.toInstant().atZone(DateFormatter.defaultTimeZone.toZoneId()).toLocalDateTime();
        LocalDateTime arrivalLocalTime = arrivalDateTime.toInstant().atZone(DateFormatter.defaultTimeZone.toZoneId()).toLocalDateTime();

        while(departureLocalTime.isBefore(arrivalLocalTime)) {
            dates.add(Pair.of(departureLocalTime.getYear(), departureLocalTime.getMonthValue()));
            departureLocalTime = departureLocalTime.plus(1, ChronoUnit.MONTHS);
        }

        return dates;
    }

    private static List<ScheduledFlight> getScheduledFlights(String departureAirport,
                                                            String arrivalAirport,
                                                            Integer year,
                                                            Integer month,
                                                            Integer day,
                                                            List<Flight> flights) {
        return flights.stream()
                .map(flight -> ScheduledFlight.builder()
                        .departureAirport(departureAirport)
                        .arrivalAirport(arrivalAirport)
                        .departureDateTime(toISODate(year, month, day, flight.getDepartureTime()))
                        .arrivalDateTime(toISODate(year, month, day, flight.getArrivalTime()))
                        .build())
                .collect(Collectors.toList());
    }

    private static Date toISODate(Integer year, Integer month, Integer day, String time) {
        return DateFormatter.parse(String.format("%s-%02d-%02dT%s", year, month, day, time));
    }
}
