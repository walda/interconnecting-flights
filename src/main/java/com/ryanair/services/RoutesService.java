package com.ryanair.services;

import com.ryanair.client.RoutesClient;
import com.ryanair.dto.Route;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class RoutesService {

    private final static String RYANAIR = "RYANAIR";

    private final RoutesClient routesClient;

    List<Route> getFilteredRoutes() {
        return routesClient.getRoutes()
                .stream()
                .filter(RoutesService::shouldFilter)
                .collect(Collectors.toList());
    }

    private static boolean shouldFilter(Route route) {
        return route.getConnectionAirport() == null && route.getOperator().equalsIgnoreCase(RYANAIR);
    }
}
