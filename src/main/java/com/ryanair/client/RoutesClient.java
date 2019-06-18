package com.ryanair.client;

import com.ryanair.dto.Route;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "routes" ,url = "https://services-api.ryanair.com")
public interface RoutesClient {

    @RequestMapping(method = RequestMethod.GET, value = "/locate/3/routes")
    List<Route> getRoutes();
}
