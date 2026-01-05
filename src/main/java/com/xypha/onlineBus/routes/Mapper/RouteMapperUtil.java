package com.xypha.onlineBus.routes.Mapper;

import com.xypha.onlineBus.routes.Dto.RouteResponse;
import com.xypha.onlineBus.routes.Entity.Route;

import java.util.List;

public class RouteMapperUtil {

    private static CityMapper cityMapper;

    public static void setCityMapper(CityMapper mapper) {
        cityMapper = mapper;
    }

    public static RouteResponse toResponse(Route r){

        if (cityMapper == null) {
            throw new IllegalStateException("CityMapper is not initialized");
        }
        String sourceCity = cityMapper.getCityNameById(r.getSourceCityId()).toUpperCase();
        String destinationCity = cityMapper.getCityNameById(r.getDestinationCityId()).toUpperCase();

        RouteResponse res = new RouteResponse();
        res.setId(r.getId());
        res.setSource(sourceCity);
        res.setDestination(destinationCity);
        res.setDistance(r.getDistance());
        res.setCreatedAt(r.getCreatedAt());
        res.setUpdatedAt(r.getUpdatedAt());
        return res;
    }

    public static List<RouteResponse> toList(List<Route> routes){
        return routes
                .stream()
                .map(RouteMapperUtil :: toResponse)
                .toList();
    }
}
