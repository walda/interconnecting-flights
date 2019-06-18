package com.ryanair.services;

import com.ryanair.dto.Itinerary;
import com.ryanair.dto.Route;
import com.ryanair.dto.ScheduledFlight;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterconnectionsService {

    private static final int MINIMUM_HOURS_BETWEEN_FLIGHTS = 2;
    private static final int MAXIMUM_HOURS_BETWEEN_FLIGHTS = 6;

    private final RoutesService routesService;
    private final ScheduleService scheduleService;

    public List<Itinerary> searchInterconnections(
            String departure,
            String arrival,
            Date departureDateTime,
            Date arrivalDateTime) {

        List<Route> routes = routesService.getFilteredRoutes();
        List<Itinerary> itineraries =
                existsDirectRoute(routes, departure, arrival) ?
                        createItineraryForDirectFlights(departure, arrival, departureDateTime, arrivalDateTime)
                        :
                        new ArrayList<>();

        itineraries.addAll(createItineraryForInterconnectingFlights(departure, arrival, departureDateTime, arrivalDateTime, routes));

        return itineraries;
    }

    private List<Itinerary> createItineraryForInterconnectingFlights(String departure,
                                                      String arrival,
                                                      Date departureDateTime,
                                                      Date arrivalDateTime,
                                                      List<Route> routes) {
        List<Route> secondLegs = geSecondLegs(departure, arrival, routes);

        return secondLegs.stream()
                .map(route -> getItinerary(departure, route.getAirportFrom(), arrival, departureDateTime, arrivalDateTime))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

    }

    private List<Itinerary> createItineraryForDirectFlights(String departure,
                                                            String arrival,
                                                            Date departureDateTime,
                                                            Date arrivalDateTime) {
        return scheduleService.getFlightSchedule(departure, arrival, departureDateTime, arrivalDateTime).stream()
                .map(scheduledFlight -> new Itinerary(0, Collections.singletonList(scheduledFlight)))
                .collect(Collectors.toList());
    }

    private List<Itinerary> getItinerary(String departure,
                                           String firstStop,
                                           String destination,
                                           Date departureDateTime,
                                           Date arrivalDateTime) {
        List<ScheduledFlight> firstLegs = scheduleService.getFlightSchedule(departure, firstStop, departureDateTime, arrivalDateTime);
        List<ScheduledFlight> secondLegs = scheduleService.getFlightSchedule(firstStop, destination, departureDateTime, arrivalDateTime);

        return firstLegs.stream()
                .map(scheduledFlight -> createItinerary(scheduledFlight, secondLegs, MINIMUM_HOURS_BETWEEN_FLIGHTS, MAXIMUM_HOURS_BETWEEN_FLIGHTS))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<Itinerary> createItinerary(ScheduledFlight firstLeg,
                                            List<ScheduledFlight> secondLeg,
                                            int minimumHoursBetweenFlights,
                                            int maximumHoursBetweenFlights) {
        return secondLeg.stream()
                .filter(scheduledFlight ->
                        isDepartureTimeAfterArrivalMinimumThreshold(scheduledFlight.getDepartureDateTime(), firstLeg.getArrivalDateTime(), minimumHoursBetweenFlights)
                                &&
                                    isDepartureTimeBeforeArrivalMaximunThreshold(scheduledFlight.getDepartureDateTime(), firstLeg.getArrivalDateTime(), maximumHoursBetweenFlights)

                )
                .map(scheduledFlight -> new Itinerary(1, Arrays.asList(firstLeg, scheduledFlight)))
                .collect(Collectors.toList());
    }

    private static boolean isDepartureTimeAfterArrivalMinimumThreshold(Date departure, Date arrival, int threshold) {
        return departure.toInstant()
                .isAfter(arrival.toInstant().plus(threshold, ChronoUnit.HOURS));
    }

    private static boolean isDepartureTimeBeforeArrivalMaximunThreshold(Date departure, Date arrival, int threshold) {
        return departure.toInstant()
                .isBefore(arrival.toInstant().plus(threshold, ChronoUnit.HOURS));
    }

    private static List<Route> geSecondLegs(String departure, String arrival, List<Route> routes) {
        return routes.stream()
                .filter(route -> route.getAirportTo().equalsIgnoreCase(arrival))
                .filter(route -> existsDirectRoute(routes, departure, route.getAirportFrom()))
                .collect(Collectors.toList());
    }

    private static boolean existsDirectRoute(List<Route> routes, String departure, String arrival) {
        return !routes.stream()
                .filter(route -> isDirectRoute(route, departure, arrival))
                .collect(Collectors.toList()).isEmpty();
    }

    private static boolean isDirectRoute(Route route, String departure, String arrival) {
        return route.getAirportFrom().equalsIgnoreCase(departure) && route.getAirportTo().equalsIgnoreCase(arrival);
    }

}
