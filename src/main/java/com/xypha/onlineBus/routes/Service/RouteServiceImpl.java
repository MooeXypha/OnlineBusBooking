package com.xypha.onlineBus.routes.Service;

import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.buses.mapper.BusMapper;
import com.xypha.onlineBus.routes.Dto.RouteRequest;
import com.xypha.onlineBus.routes.Dto.RouteResponse;
import com.xypha.onlineBus.routes.Entity.Route;
import com.xypha.onlineBus.routes.Mapper.RouteMapper;
import com.xypha.onlineBus.routes.googleService.GoogleDistanceService;
import com.xypha.onlineBus.staffs.Service.StaffService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl {

    private final RouteMapper routeMapper;
    private final BusMapper busMapper;
    private final StaffService staffService;
    private final GoogleDistanceService googleDistanceService;


    public RouteServiceImpl(RouteMapper routeMapper, BusMapper busMapper, StaffService staffService, GoogleDistanceService googleDistanceService) {
        this.routeMapper = routeMapper;
        this.busMapper = busMapper;
        this.staffService = staffService;
        this.googleDistanceService = googleDistanceService;
    }





    public RouteResponse mapToResponse(Route route) {
        RouteResponse res = new RouteResponse();
        res.setId(route.getId());
        res.setSource(route.getSource().toUpperCase());
        res.setDistance(route.getDistance());
        res.setDestination(route.getDestination().toUpperCase());
        res.setCreatedAt(route.getCreatedAt());
        res.setUpdatedAt(route.getUpdatedAt());

        // Map bus-related fields if present
//        if (route.getBusId() != null){
//            Bus bus = busMapper.getBusById(route.getBusId());
//            if (bus != null){
//                BusResponse busResponse = new BusResponse();
//                busResponse.setId(bus.getId());
//                busResponse.setBusNumber(bus.getBusNumber());
//                busResponse.setBusType(bus.getBusType());
//                busResponse.setTotalSeats(bus.getTotalSeats());
//                busResponse.setHasAC(bus.getHasAC());
//                busResponse.setHasWifi(bus.getHasWifi());
//                busResponse.setImgUrl(bus.getImgUrl());
//                busResponse.setDescription(bus.getDescription());
//                busResponse.setCreatedAt(bus.getCreatedAt());
//                busResponse.setUpdatedAt(bus.getUpdatedAt());
//
//                if (bus.getDriverId() != null )
//                    busResponse.setDriver(
//                            staffService.getDriverById(bus.getDriverId())
//                    );
//                if (bus.getAssistantId() != null)
//                    busResponse.setAssistant(
//                            staffService.getAssistantById(bus.getAssistantId())
//                    );
//                res.setBus(busResponse);
//            }
//        }

        return res;
    }

    public ApiResponse<RouteResponse> addRoute(RouteRequest routeRequest) {

        String normalizedSource = routeRequest.getSource().toUpperCase().replaceAll("\\s+", "");
        String normalizedDestination = routeRequest.getDestination().toUpperCase().replaceAll("\\s+", "");

        // Check duplicate PAIR (source + destination)
        if (routeMapper.countDuplicateNormalizeCreateRoute(normalizedSource, normalizedDestination) > 0) {
            throw new RuntimeException("This route already exists.");
        }

        double distanceKm = googleDistanceService.getDistanceKm(
                routeRequest.getSource(),
                routeRequest.getDestination()
        );

        Route route = new Route();
        route.setSource(routeRequest.getSource().toUpperCase());  // KEEP ORIGINAL
        route.setDestination(routeRequest.getDestination().toUpperCase());
        route.setDistance(distanceKm);
        route.setCreatedAt(LocalDate.now().atStartOfDay());
        route.setUpdatedAt(LocalDate.now().atStartOfDay());

        routeMapper.insertRoute(route);

        return new ApiResponse<>("SUCCESS", "Route created successfully", mapToResponse(route));
    }

    // --------------------------------------------------------------------------------
    // UPDATE ROUTE
    // --------------------------------------------------------------------------------
    public ApiResponse<RouteResponse> updateRoute(Long id, RouteRequest request) {
        Route route = routeMapper.getRouteById(id);
        if (route == null) throw new RuntimeException("Route not found");

        String normalizedSource = request.getSource().toUpperCase().replaceAll("\\s+", "");
        String normalizedDestination = request.getDestination().toUpperCase().replaceAll("\\s+", "");

        // Check duplicate pair excluding current route (IMPORTANT)
        if (routeMapper.countDuplicateNormalizeRouteExcludingId(
                id,
                normalizedSource,
                normalizedDestination
        ) > 0) {
            throw new RuntimeException("This route already exists.");
        }

        double distanceKm = googleDistanceService.getDistanceKm(
                request.getSource(),
                request.getDestination()
        );

        // Save original input
        route.setSource(request.getSource().toUpperCase().trim());
        route.setDestination(request.getDestination().toUpperCase().trim());
        route.setDistance(distanceKm);
        route.setUpdatedAt(LocalDate.now().atStartOfDay());

        routeMapper.updateRoute(route);

        return new ApiResponse<>("SUCCESS", "Route updated successfully", mapToResponse(route));
    }

    // --------------------------------------------------------------------------------
    // Other methods unchanged
    // --------------------------------------------------------------------------------

    public ApiResponse<PaginatedResponse<RouteResponse>> getAllRoute(int offset, int limit) {
        if (offset < 1) offset = 1;
        if (limit < 1) limit = 10;

        int page = (offset - 1) * limit;

        List<Route> routeEntities = routeMapper.getAllPaginated(page, limit);
        List<RouteResponse> routes = routeEntities.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        int total = routeMapper.countRoutes();

        PaginatedResponse<RouteResponse> paginatedResponse =
                new PaginatedResponse<>(offset, limit, total, routes);

        return new ApiResponse<>("SUCCESS", "Routes retrieved successfully", paginatedResponse);
    }

    public ApiResponse<RouteResponse> getRouteById(Long id) {
        Route route = routeMapper.getRouteById(id);
        if (route == null) throw new RuntimeException("Route not found");
        return new ApiResponse<>("SUCCESS", "Route retrieved successfully", mapToResponse(route));
    }

    public ApiResponse<Void> deleteRoute(Long id) {
        routeMapper.deleteRoute(id);
        return new ApiResponse<>("SUCCESS","Route deleted successfully: " +id, null);
    }

    public Map<String, Object> searchRoutes(
            String source,
            String destination,
            int page,
            int size
    ){
        int offset = (page - 1) * size;
        List<RouteResponse> routes = routeMapper.searchRoutes(source, destination, size, offset);
        int total =  routeMapper.countSearchRoutes(source,destination);

        Map<String, Object> result = new HashMap<>();
        result.put("contents", routes);
        result.put("totalItems", total);
        result.put("page", page);
        result.put("size", size);

        return result;
    }
}
