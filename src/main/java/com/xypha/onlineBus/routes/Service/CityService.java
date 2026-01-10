package com.xypha.onlineBus.routes.Service;

import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.routes.Dto.CityResponse;
import com.xypha.onlineBus.routes.mapper.CityMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {
    private final CityMapper cityMapper;

    public CityService(CityMapper cityMapper) {
        this.cityMapper = cityMapper;
    }

    public ApiResponse<List<CityResponse>> getAllCities (){
        List<CityResponse> responses = cityMapper.getAllCities();
        return new ApiResponse<>("SUCCESS", "Cities retrieved successfully", responses);

    }

}
