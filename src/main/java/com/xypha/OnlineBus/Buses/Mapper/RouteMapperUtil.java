package com.xypha.OnlineBus.Buses.Mapper;

import com.xypha.OnlineBus.Routes.Dto.RouteResponse;
import com.xypha.OnlineBus.Routes.Entity.Route;

import java.util.List;

public class RouteMapperUtil {

    public static RouteResponse toResponse(Route r){
        RouteResponse res = new RouteResponse();
        res.setId(r.getId());
        res.setSource(r.getSource());
        res.setDestination(r.getDestination());
        res.setDepartureTime(r.getDepartureTime());
        res.setArrivalTime(r.getArrivalTime());
        res.setPrice(r.getPrice());
        return res;
    }

    public static List<RouteResponse> toList(List<Route> routes){
        return routes
                .stream()
                .map(RouteMapperUtil :: toResponse)
                .toList();
    }
}
