package com.ryanair.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Route {

    @NotNull
    String airportFrom;
    @NotNull
    String airportTo;
    String connectionAirport;
    String newRoute;
    String seasonalRoute;
    @NotNull
    String operator;
    String group;
}
