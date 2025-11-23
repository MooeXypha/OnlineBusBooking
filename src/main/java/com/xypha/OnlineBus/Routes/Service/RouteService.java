package com.xypha.OnlineBus.Routes.Service;


import com.xypha.OnlineBus.Buses.Mapper.RouteMapperUtil;
import com.xypha.OnlineBus.Routes.Dto.RouteRequest;
import com.xypha.OnlineBus.Routes.Dto.RouteResponse;
import com.xypha.OnlineBus.Routes.Entity.Route;
import com.xypha.OnlineBus.Routes.Mapper.RouteMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RouteService {

    @Autowired
    private RouteMapper routeMapper;

    public RouteResponse mapToResponse(Route route){
        RouteResponse res = new RouteResponse();
        res.setId(route.getId());
        res.setSource(route.getSource());
        res.setPrice(route.getPrice());
        res.setDestination(route.getDestination());
        res.setDepartureTime(route.getDepartureTime());
        res.setArrivalTime(route.getArrivalTime());
        return res;
    }

    public RouteResponse addRoute(RouteRequest routeRequest){
        Route route = new Route();
        route.setSource(routeRequest.getSource());
        route.setPrice(route.getPrice());
        route.setDestination(routeRequest.getDestination());
        route.setDepartureTime(routeRequest.getDepartureTime());
        route.setArrivalTime(routeRequest.getArrivalTime());

        if (routeMapper.countDuplicateRoute(route) > 0){
            throw new RuntimeException("This route already exists at the same time");
        }
        routeMapper.insertRoute(route);
        return mapToResponse(route);
    }

    public List<RouteResponse> getAllRoute(){
        return routeMapper.getAllRoute()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public RouteResponse getRouteById(Long id){
        Route route = routeMapper.getRouteById(id);
        if (route == null) throw new RuntimeException("Route not found");
        return mapToResponse(route);
    }

    public RouteResponse updateRoute(Long id, RouteRequest request){
        Route route = routeMapper.getRouteById(id);
        if (route == null ) throw new RuntimeException("Route not found");

        route.setSource(request.getSource());
        route.setDestination(request.getDestination());
        route.setPrice(request.getPrice());
        route.setDepartureTime(request.getDepartureTime());
        route.setArrivalTime(request.getArrivalTime());

        routeMapper.updateRoute(route);
        return mapToResponse(route);
    }

    public void deleteRoute(Long id){
        routeMapper.deleteRoute(id);
    }


    //Search method
//   public List<Route> searchRoutes(
//           String source, String destination,
//           int page, int size){
//        int offset = (page - 1) *size;
//        return routeMapper.searchRoutes(source,destination,size,offset);
//   }

    public Map<String , Object>searchRoutes(
            String source, String destination,
            int page,  int size
    ){
        int offset = page * size;
        List<Route> routes = routeMapper.searchRoutes(source, destination, size, offset);
        int total = routeMapper.countSearchRoutes(source, destination);

        Map<String, Object> result = new HashMap<>();
        result.put("routes", RouteMapperUtil.toList(routes));
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;

    }



}
