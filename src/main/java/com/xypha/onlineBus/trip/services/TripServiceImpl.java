package com.xypha.onlineBus.trip.services;

import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.buses.Dto.BusResponse;
import com.xypha.onlineBus.buses.Entity.Bus;
import com.xypha.onlineBus.buses.Mapper.BusMapper;
import com.xypha.onlineBus.routes.Dto.RouteResponse;
import com.xypha.onlineBus.routes.Entity.Route;
import com.xypha.onlineBus.routes.Mapper.RouteMapper;
import com.xypha.onlineBus.staffs.Service.StaffService;
import com.xypha.onlineBus.trip.dto.TripRequest;
import com.xypha.onlineBus.trip.dto.TripResponse;
import com.xypha.onlineBus.trip.entity.Trip;
import com.xypha.onlineBus.trip.mapper.TripMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TripServiceImpl {

    private final RouteMapper routeMapper;
    private final TripMapper tripMapper;
    private final BusMapper busMapper;
    private final StaffService staffService;

    public TripServiceImpl(RouteMapper routeMapper, TripMapper tripMapper, BusMapper busMapper, StaffService staffService) {
        this.routeMapper = routeMapper;
        this.tripMapper = tripMapper;
        this.busMapper = busMapper;
        this.staffService = staffService;
    }

    public TripResponse mapToResponse (Trip trip){
        TripResponse response = new TripResponse();

        response.setId(trip.getId());
        response.setRouteId(trip.getRouteId());
        response.setBusId(trip.getBusId());

        response.setDepartureDate(trip.getDepartureDate());
        response.setArrivalDate(trip.getArrivalDate());
        response.setFare(trip.getFare());
        response.setCreatedAt(trip.getCreatedAt());
        response.setUpdatedAt(trip.getUpdatedAt());

        // === Route ===
        if (trip.getRouteId() != null){
        Route route = routeMapper.getRouteById(trip.getRouteId());
        if (route != null) {
            RouteResponse r = new RouteResponse();
            r.setId(route.getId());
            r.setSource(route.getSource());
            r.setDestination(route.getDestination());
            r.setDistance(route.getDistance());
            r.setDuration(route.getDuration());
            r.setCreatedAt(route.getCreatedAt());
            r.setUpdatedAt(route.getUpdatedAt());

            response.setRoute(r);
        }
            response.setRouteId(trip.getRouteId());
        }

        // === Bus ===

        if (trip.getBusId() != null){
        Bus bus = busMapper.getBusById(trip.getBusId());
        if (bus != null) {
            BusResponse b = new BusResponse();
            b.setId(bus.getId());
            b.setBusNumber(bus.getBusNumber());
            b.setBusType(bus.getBusType());
            b.setTotalSeats(bus.getTotalSeats());
            b.setHasAC(bus.getHasAC());
            b.setHasWifi(bus.getHasWifi());
            b.setImgUrl(bus.getImgUrl());
            b.setDescription(bus.getDescription());
            b.setPricePerKm(bus.getPricePerKm());
            b.setCreatedAt(bus.getCreatedAt());
            b.setUpdatedAt(bus.getUpdatedAt());

            // Drivers & assistants
            if (bus.getDriverId() != null)
                b.setDriver(staffService.getDriverById(bus.getDriverId()));

            if (bus.getAssistantId() != null)
                b.setAssistant(staffService.getAssistantById(bus.getAssistantId()));

            response.setBus(b);
        }
            response.setBusId(trip.getBusId());
        }

        return response;
    }


    public ApiResponse<TripResponse> createTrip(TripRequest tripRequest){
        Bus bus = busMapper.getBusById(tripRequest.getBusId());
        LocalDateTime now = LocalDateTime.now();
        if(bus == null)
            return new ApiResponse<>("FAILURE", "Bus not found with id: " + tripRequest.getBusId(), null);

        Route route = routeMapper.getRouteById(tripRequest.getRouteId());
        if(route == null)
            return new ApiResponse<>("FAILURE", "Route not found with id: " + tripRequest.getRouteId(), null);

        if (tripRequest.getDepartureDate().isBefore(now) && tripRequest.getArrivalDate().isBefore(now)){
            return new ApiResponse<>("FAILURE", "Both departure and arrival date must be in the future", null);
        }

        double fare = bus.getPricePerKm() * route.getDistance();
        fare = Math.ceil(fare / 100) * 100; // Round up to nearest 100

        Trip trip = new Trip();
        trip.setRouteId(tripRequest.getRouteId());
        trip.setBusId(tripRequest.getBusId());
        trip.setDepartureDate(tripRequest.getDepartureDate());
        // Auto-calculate arrival based on route duration
        trip.setArrivalDate(tripRequest.getDepartureDate().plusMinutes(route.getDuration()));
        trip.setFare(fare);
        trip.setCreatedAt(LocalDateTime.now());
        trip.setUpdatedAt(LocalDateTime.now());

        if(tripMapper.countDuplicateTrip(trip) > 0)
            return new ApiResponse<>(false, "Trip already exists for the given route, bus, and departure date", null);

        tripMapper.createTrip(trip);
        return new ApiResponse<>("SUCCESS", "Trip created successfully", mapToResponse(trip));
    }

    public ApiResponse<PaginatedResponse<TripResponse>> getAllTrips(int page, int size) {
        if(page < 1) page = 1;
        if(size < 1) size = 10;

        int offset = (page - 1) * size;
        List<Trip> trips = tripMapper.getAllTripsPaginated(offset, size);

        List<TripResponse> tripResponses = trips.stream()
                .map(this::mapToResponse)
                .toList();

        int total = tripMapper.countTrip();
        PaginatedResponse<TripResponse> paginatedResponse = new PaginatedResponse<>(offset, size, total, tripResponses);
        return new ApiResponse<>("SUCCESS", "Trips retrieved successfully", paginatedResponse);
    }

    public ApiResponse<TripResponse> getTripById(Long id){
        Trip trip = tripMapper.getTripById(id);
        if(trip == null)
            throw new RuntimeException("Trip not found");

        return new ApiResponse<>("SUCCESS", "Trip retrieved successfully: " + id, mapToResponse(trip));
    }

    public ApiResponse<TripResponse> updateTrip(Long id, TripRequest request){
        Trip trip = tripMapper.getTripById(id);
        if(trip == null)
            throw new RuntimeException("Trip not found");

        Bus bus = busMapper.getBusById(request.getBusId());
        Route route = routeMapper.getRouteById(request.getRouteId());

        trip.setBusId(request.getBusId());
        trip.setRouteId(request.getRouteId());
        trip.setDepartureDate(request.getDepartureDate());
        trip.setArrivalDate(request.getDepartureDate().plusMinutes(route.getDuration()));
        trip.setFare(Math.ceil(bus.getPricePerKm() * route.getDistance() / 100) * 100);
        trip.setUpdatedAt(LocalDateTime.now());

        tripMapper.updateTrip(trip);
        return new ApiResponse<>("SUCCESS", "Trip updated successfully", mapToResponse(trip));
    }

    public ApiResponse<Void> deleteTrip(Long id){
        tripMapper.deleteTrip(id);
        return new ApiResponse<>("SUCCESS", "Trip deleted successfully : " + id, null);
    }

    public ApiResponse<List<TripResponse>> searchTripByDate(LocalDate departureDate) {
        // Fetch trips from DB
        List<Trip> trips = tripMapper.searchTripsByDepartureDate(departureDate);

        if (trips.isEmpty()) {
            return new ApiResponse<>("FAILURE", "No trips found for " + departureDate, List.of());
        }

        // Map trips to TripResponse
        List<TripResponse> tripResponses = trips.stream()
                .map(this::mapToResponse)
                .toList();

        return new ApiResponse<>("SUCCESS", "Trips found for " + departureDate, tripResponses);
    }


    public ApiResponse<Integer> countTripsByDepartureDate(LocalDate departureDate){
        int count = tripMapper.countTripsByDepartureDate(departureDate);
        return new ApiResponse<>("SUCCESS", "Counted trips successfully for departure date: " + departureDate, count);
    }


}
