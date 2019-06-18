package com.ryanair.services;

import com.ryanair.client.RoutesClient;
import com.ryanair.dto.Route;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RoutesServiceTest {

    @Mock
    private RoutesClient routesClient;

    @InjectMocks
    private RoutesService routesService;

    @Test
    public void whenGetFilteredRoutes() {
        Route route = new Route();
        route.setOperator("RYANAIR");
        when(routesClient.getRoutes()).thenReturn(Collections.singletonList(route));

        List<Route>  routes = routesService.getFilteredRoutes();

        assertThat(routes).isNotNull().isNotEmpty().isEqualTo(Collections.singletonList(route));
    }

    @Test
    public void whenGetFilteredRoutesFromRandomOperatorRoutesAreFiltered() {
        Route route = new Route();
        route.setOperator("RANDOM");
        when(routesClient.getRoutes()).thenReturn(Collections.singletonList(route));

        List<Route>  routes = routesService.getFilteredRoutes();

        assertThat(routes).isNotNull().isEmpty();
    }

    @Test
    public void whenGetFilteredRoutesWithConnectingAirportRoutesAreFiltered() {
        Route route = new Route();
        route.setConnectionAirport("AIRPORT");
        when(routesClient.getRoutes()).thenReturn(Collections.singletonList(route));

        List<Route>  routes = routesService.getFilteredRoutes();

        assertThat(routes).isNotNull().isEmpty();
    }

    @Test
    public void whenGetFilteredRoutesWithConnectingAirportAndRandomOperatorRoutesAreFiltered() {
        Route route = new Route();
        route.setConnectionAirport("AIRPORT");
        route.setOperator("RANDOM");
        when(routesClient.getRoutes()).thenReturn(Collections.singletonList(route));

        List<Route>  routes = routesService.getFilteredRoutes();

        assertThat(routes).isNotNull().isEmpty();
    }

}
