package com.xypha.onlineBus.routes.Controller;

import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.routes.Dto.CityResponse;
import com.xypha.onlineBus.routes.Service.CityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping ("/api/city")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public ApiResponse<List<CityResponse>> getAllCities (){
       return cityService.getAllCities();
    }
}
