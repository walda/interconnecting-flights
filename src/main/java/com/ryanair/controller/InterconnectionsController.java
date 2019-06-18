package com.ryanair.controller;

import com.ryanair.dto.Itinerary;
import com.ryanair.services.InterconnectionsService;
import com.ryanair.util.DateFormatter;
import com.ryanair.validator.DatesValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class InterconnectionsController {

    private final InterconnectionsService interconnectionsService;
    private final DatesValidator datesValidator;

    @GetMapping(value = "/interconnections")
    public List<Itinerary> searchInterconnections(
            @RequestParam String departure,
            @RequestParam String arrival,
            @RequestParam String departureDateTime,
            @RequestParam String arrivalDateTime) {

        Date departureDate = parseDepartureDateTime(departureDateTime);
        Date arrivalDate = parseArrivalDateTime(arrivalDateTime);

        datesValidator.checkDates(departureDate, arrivalDate);

        return interconnectionsService.searchInterconnections(departure, arrival, departureDate, arrivalDate);
    }

    private static Date parseDepartureDateTime(String departureDateTime) {
        return DateFormatter.parse(departureDateTime, "Invalid departureDateTime format");
    }

    private static Date parseArrivalDateTime(String arrivalDateTime) {
        return DateFormatter.parse(arrivalDateTime, "Invalid arrivalDateTime format");
    }
}
