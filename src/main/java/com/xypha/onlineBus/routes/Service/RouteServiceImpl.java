package com.xypha.onlineBus.routes.Service;

import ch.qos.logback.core.joran.conditional.IfAction;
import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.buses.mapper.BusMapper;
import com.xypha.onlineBus.routes.Dto.RouteRequest;
import com.xypha.onlineBus.routes.Dto.RouteResponse;
import com.xypha.onlineBus.routes.Dto.RouteWithCity;
import com.xypha.onlineBus.routes.Entity.Route;
import com.xypha.onlineBus.routes.Mapper.CityMapper;
import com.xypha.onlineBus.routes.Mapper.RouteMapper;
import com.xypha.onlineBus.routes.googleService.GoogleDistanceService;
import com.xypha.onlineBus.staffs.Service.StaffService;
import com.xypha.onlineBus.trip.mapper.TripMapper;
import com.xypha.onlineBus.trip.services.TripServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl {

    private final RouteMapper routeMapper;
    private final BusMapper busMapper;
    private final StaffService staffService;
    private final TripServiceImpl tripService;
    private final TripMapper tripMapper;
    private final GoogleDistanceService googleDistanceService;

    private final CityMapper cityMapper;

    public RouteServiceImpl(RouteMapper routeMapper, BusMapper busMapper, StaffService staffService, TripServiceImpl tripService, TripMapper tripMapper, GoogleDistanceService googleDistanceService, CityMapper cityMapper) {
        this.routeMapper = routeMapper;
        this.busMapper = busMapper;
        this.staffService = staffService;
        this.tripService = tripService;
        this.tripMapper = tripMapper;
        this.googleDistanceService = googleDistanceService;
        this.cityMapper = cityMapper;
    }


    public RouteResponse mapToResponse(RouteWithCity route) {
        RouteResponse res = new RouteResponse();
        res.setId(route.getId());
        res.setSource(route.getSourceName());
        res.setDistance(route.getDistance());
        res.setDestination(route.getDestinationName());
        res.setCreatedAt(route.getCreatedAt());
        res.setUpdatedAt(route.getUpdatedAt());

        return res;
    }

    public ApiResponse<RouteResponse> addRoute(RouteRequest routeRequest) {

        Long sourceId = routeRequest.getSourceCityId();
        Long destId = routeRequest.getDestinationCityId();
        LocalDateTime now = LocalDateTime.now();

        // 1️⃣ Check IDs are not null
        if (sourceId == null || destId == null) {
            throw new RuntimeException("Source and Destination city IDs must not be null.");
        }
        // 2️⃣ Fetch city names from DB
        String sourceCity = cityMapper.getCityNameById(sourceId);
        String destCity = cityMapper.getCityNameById(destId);

        // 3️⃣ Check city names exist
        if (sourceCity == null) {
            throw new RuntimeException("Source city not found for ID: " + sourceId);
        }
        if (destCity == null) {
            throw new RuntimeException("Destination city not found for ID: " + destId);
        }

        // Check duplicate PAIR (source + destination)
        if (routeMapper.countDuplicateRoute(sourceId, destId) > 0
            || routeMapper.countDuplicateRoute(destId, sourceId) > 0) {
            throw new RuntimeException("This route already exists.");
        }

        double distanceKm = googleDistanceService.getDistanceKm(sourceCity, destCity);

        Route route = new Route();
        route.setSourceCityId(sourceId);
        route.setDestinationCityId(destId);
        route.setDistance(distanceKm);
        route.setCreatedAt(now);
        route.setUpdatedAt(now);

        routeMapper.insertRoute(route);

        RouteWithCity savedRoute = routeMapper.getRouteWithCityById(route.getId());

        return new ApiResponse<>("SUCCESS", "Route created successfully", mapToResponse(savedRoute));
    }

    // --------------------------------------------------------------------------------
    // UPDATE ROUTE
    // --------------------------------------------------------------------------------
    public ApiResponse<RouteResponse> updateRoute(Long id, RouteRequest request) {
        Route route = routeMapper.getRouteById(id);
        if (route == null) throw new RuntimeException("Route not found");

        Long normalizedSource = request.getSourceCityId();
        Long normalizedDestination = request.getDestinationCityId();

        // Check duplicate pair excluding current route (IMPORTANT)
      if (normalizedSource == null || normalizedDestination == null){
          throw new RuntimeException("Source and Destination city IDs must not be null.");
        }

      String sourceCity = cityMapper.getCityNameById(normalizedSource);
        String destCity = cityMapper.getCityNameById(normalizedDestination);

        if(sourceCity == null) throw new RuntimeException("Source city not found for ID: " + normalizedSource);
        if (destCity == null) throw new RuntimeException("Destination city not found for ID: " + normalizedDestination);

        if (routeMapper.countDuplicateRouteExcludingId(id, normalizedSource, normalizedDestination) > 0) {
            throw new RuntimeException("This route already exists.");
        }


        double distanceKm = googleDistanceService.getDistanceKm(sourceCity, destCity
        );

        // Save original input
        route.setSourceCityId(normalizedSource);
        route.setDestinationCityId(normalizedDestination);
        route.setDistance(distanceKm);
        route.setUpdatedAt(LocalDate.now().atStartOfDay());

        routeMapper.updateRoute(route);
        RouteWithCity updated = routeMapper.getRouteWithCityById (route.getId());

        return new ApiResponse<>("SUCCESS", "Route updated successfully", mapToResponse(updated));
    }

    // --------------------------------------------------------------------------------
    // Other methods unchanged
    // --------------------------------------------------------------------------------

    public ApiResponse<PaginatedResponse<RouteResponse>> getAllRoute(int offset, int limit) {
        if (offset < 0) offset = 0;
        if (limit < 1) limit = 10;


        List<RouteWithCity> routeEntities = routeMapper.getAllPaginated(offset, limit);
        List<RouteResponse> routes = routeEntities.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        int total = routeMapper.countRoutes();

        PaginatedResponse<RouteResponse> paginatedResponse =
                new PaginatedResponse<>(offset, limit, total, routes);

        return new ApiResponse<>("SUCCESS", "Routes retrieved successfully", paginatedResponse);
    }

    public ApiResponse<RouteResponse> getRouteById(Long id) {
        RouteWithCity route = routeMapper.getRouteWithCityById(id);
        if (route == null) throw new RuntimeException("Route not found");
        return new ApiResponse<>("SUCCESS", "Route retrieved successfully", mapToResponse(route));
    }

    public ApiResponse<Void> deleteRoute(Long id) {

        int tripCount = tripMapper.countTripsByRouteId(id);
        if (tripCount > 0){
            return new ApiResponse<>("FAILURE","Cannot delete route : active trips exist", null);
        }

        int deleted = routeMapper.deleteRoute(id);
        if (deleted == 0){
            return new ApiResponse<>("FAILURE", "Route not found",null);
        }

        return new ApiResponse<>("SUCCESS","Route deleted successfully: " +id, null);
    }



   public ApiResponse<PaginatedResponse<RouteResponse>>searchRoutes (String source,String destination, int offset, int limit){
        if (offset < 0) offset = 0;
        if (limit < 1) limit = 10;

        List<RouteWithCity> route = routeMapper.searchRoutesWithCity(
                source,
                destination,
                limit,
                offset
        );
      List<RouteResponse> routes = route.stream()
              .map(this::mapToResponse)
              .toList();
      int total = routeMapper.countRoutes();
      PaginatedResponse<RouteResponse> paginatedResponse =
              new PaginatedResponse<>(offset, limit, total, routes);
      return new ApiResponse<>("SUCCESS", "Routes retrieved successfully", paginatedResponse);

    }
}
