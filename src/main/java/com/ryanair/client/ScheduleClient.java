package com.ryanair.client;

import com.ryanair.dto.Schedule;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "schedules" ,url = "https://services-api.ryanair.com", decode404 = true)
public interface ScheduleClient {

    @RequestMapping(method = RequestMethod.GET, value = "/timtbl/3/schedules/{departure}/{arrival}/years/{year}/months/{month}")
    Schedule searchFlights(@PathVariable("departure") String departure,
                           @PathVariable("arrival") String arrival,
                           @PathVariable("year") String year,
                           @PathVariable("month") String month);
}
