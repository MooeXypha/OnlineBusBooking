package com.xypha.onlineBus.routes.Mapper;

import com.xypha.onlineBus.routes.Dto.RouteResponse;
import com.xypha.onlineBus.routes.Entity.Route;

import java.util.List;

public class RouteMapperUtil {

    public static RouteResponse toResponse(Route r){
        RouteResponse res = new RouteResponse();
        res.setId(r.getId());
        res.setSource(r.getSource());
        res.setDestination(r.getDestination());
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
