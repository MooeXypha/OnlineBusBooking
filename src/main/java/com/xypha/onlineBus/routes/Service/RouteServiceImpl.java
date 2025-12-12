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
        res.setSource(route.getSource());
        res.setDistance(route.getDistance());
        res.setDestination(route.getDestination());
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

        // 1️⃣ Normalize input for DB checks
        String normalizedSource = routeRequest.getSource().toUpperCase().replaceAll("\\s+", "");
        String normalizedDestination = routeRequest.getDestination().toUpperCase().replaceAll("\\s+", "");

        // 2️⃣ Check for duplicates (source + destination together)
        if (routeMapper.countDuplicateNormalizeRoute(normalizedSource, normalizedDestination) > 0) {
            throw new RuntimeException("This route already exists.");
        }

        // ✅ For new route, no need to call countByNormalizedSourceOrDestinationExcludingId

        // 3️⃣ Get real distance from Google API
        double distanceKm = googleDistanceService.getDistanceKm(
                routeRequest.getSource(),
                routeRequest.getDestination()
        );

        // 4️⃣ Create and populate Route entity
        Route route = new Route();
        route.setSource(normalizedSource);  // save normalized uppercase
        route.setDestination(normalizedDestination);
        route.setDistance(distanceKm);
        route.setCreatedAt(LocalDate.now().atStartOfDay());
        route.setUpdatedAt(LocalDate.now().atStartOfDay());

        // 5️⃣ Save to DB
        routeMapper.insertRoute(route);

        // 6️⃣ Return response
        return new ApiResponse<>("SUCCESS", "Route created successfully", mapToResponse(route));
    }

    public ApiResponse<PaginatedResponse<RouteResponse>> getAllRoute(int page, int size) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        int offset = (page - 1) * size;

        // Step 1: fetch Route entities
        List<Route> routeEntities = routeMapper.getAllPaginated(offset, size);

        // Step 2: map to DTO
        List<RouteResponse> routes = routeEntities.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        // Step 3: get total count
        int total = routeMapper.countRoutes();

        // Step 4: wrap in PaginatedResponse
        PaginatedResponse<RouteResponse> paginatedResponse =
                new PaginatedResponse<>(offset, size, total, routes);

        // Step 5: return ApiResponse
        return new ApiResponse<>("SUCCESS", "Routes retrieved successfully", paginatedResponse);
    }


    private int countRoutes(){
        return  busMapper.countBuses();
    }

    public ApiResponse<RouteResponse> getRouteById(Long id) {
        Route route = routeMapper.getRouteById(id);
        if (route == null)
            throw new RuntimeException("Route not found");
        RouteResponse routeResponse = mapToResponse(route);
        return new ApiResponse<>("SUCCESS", "Route retrieved successfully", routeResponse);

    }

    public ApiResponse<RouteResponse> updateRoute(Long id, RouteRequest request) {
        Route route = routeMapper.getRouteById(id);
        if (route == null) throw new RuntimeException("Route not found");

        // 2️⃣ Normalize input
        String normalizedSource = request.getSource().toUpperCase().replaceAll("\\s+", "");
        String normalizedDestination = request.getDestination().toUpperCase().replaceAll("\\s+", "");

        // 3️⃣ Check duplicates excluding current route
        if (routeMapper.countByNormalizedSourceOrDestinationExcludingId(normalizedSource, normalizedDestination, id) > 0) {
            throw new RuntimeException("Source or Destination name already exists.");
        }

        // 4️⃣ Update distance using Google API
        double distanceKm = googleDistanceService.getDistanceKm(
                request.getSource(),
                request.getDestination()
        );

        // 5️⃣ Update route entity
        route.setSource(normalizedSource);
        route.setDestination(normalizedDestination);
        route.setDistance(distanceKm);
        route.setUpdatedAt(LocalDate.now().atStartOfDay());

        // 6️⃣ Save update
        routeMapper.updateRoute(route);

        return new ApiResponse<>("SUCCESS", "Route updated successfully", mapToResponse(route));
    }

    public ApiResponse<Void> deleteRoute(Long id) {
        routeMapper.deleteRoute(id);
        return new ApiResponse<>("SUCCESS","Route deleted successfully: " +id, null);
    }

    // Search method
    // public List<Route> searchRoutes(
    // String source, String destination,
    // int page, int size){
    // int offset = (page - 1) *size;
    // return routeMapper.searchRoutes(source,destination,size,offset);
    // }

    public Map<String, Object> searchRoutes(
            String source,
            String destination,
            LocalDate departureDate,
            int page,
            int size
    ){
        int offset = (page - 1) * size;
        List<RouteResponse> routes = routeMapper.searchRoutes(source, destination, departureDate, size, offset);

        int total =  routeMapper.countSearchRoutes(source,destination,departureDate);

        Map<String, Object> result = new HashMap<>();
        result.put("contents", routes);
        result.put("totalItems", total);
        result.put("page", page);
        result.put("size", size);

        return result;
    }


}
