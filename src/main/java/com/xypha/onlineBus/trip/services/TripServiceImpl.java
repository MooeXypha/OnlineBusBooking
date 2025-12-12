package com.xypha.onlineBus.trip.services;

import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.buses.Dto.BusResponse;

import com.xypha.onlineBus.buses.Entity.Bus;

import com.xypha.onlineBus.buses.busType.dto.BusTypeResponse;
import com.xypha.onlineBus.buses.busType.entity.BusType;
import com.xypha.onlineBus.buses.mapper.BusMapper;
import com.xypha.onlineBus.buses.services.ServiceResponse;
import com.xypha.onlineBus.routes.Dto.RouteResponse;
import com.xypha.onlineBus.routes.Entity.Route;
import com.xypha.onlineBus.routes.Mapper.RouteMapper;
import com.xypha.onlineBus.staffs.Assistant.Dto.AssistantResponse;
import com.xypha.onlineBus.staffs.Assistant.Entity.Assistant;
import com.xypha.onlineBus.staffs.Assistant.Mapper.AssistantMapper;
import com.xypha.onlineBus.staffs.Driver.Dto.DriverResponse;
import com.xypha.onlineBus.staffs.Driver.Entity.Driver;
import com.xypha.onlineBus.staffs.Driver.Mapper.DriverMapper;
import com.xypha.onlineBus.staffs.Service.StaffService;
import com.xypha.onlineBus.trip.dto.TripRequest;
import com.xypha.onlineBus.trip.dto.TripResponse;
import com.xypha.onlineBus.trip.entity.Trip;
import com.xypha.onlineBus.trip.mapper.TripMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TripServiceImpl implements TripService {

    private final RouteMapper routeMapper;
    private final TripMapper tripMapper;
    private final BusMapper busMapper;
    private final StaffService staffService;
    private final DriverMapper driverMapper;
    private final AssistantMapper assistantMapper;

    public TripServiceImpl(RouteMapper routeMapper, TripMapper tripMapper, BusMapper busMapper,
                           StaffService staffService, DriverMapper driverMapper,
                           AssistantMapper assistantMapper) {
        this.routeMapper = routeMapper;
        this.tripMapper = tripMapper;
        this.busMapper = busMapper;
        this.staffService = staffService;
        this.driverMapper = driverMapper;
        this.assistantMapper = assistantMapper;
    }

    // =================== Mapping helpers ===================
    private BusResponse mapBus(Long busId) {
        Bus bus = busMapper.getBusById(busId);
        if (bus == null) return null;

        BusResponse res = new BusResponse();
        res.setId(bus.getId());
        res.setBusNumber(bus.getBusNumber());
        res.setTotalSeats(bus.getTotalSeats());
        res.setImgUrl(bus.getImgUrl());
        res.setDescription(bus.getDescription());
        res.setPricePerKm(bus.getPricePerKm());
        res.setCreatedAt(bus.getCreatedAt());
        res.setUpdatedAt(bus.getUpdatedAt());

        if (bus.getBusType() != null) {
            BusTypeResponse typeRes = new BusTypeResponse();
            typeRes.setId(bus.getBusType().getId());
            typeRes.setName(bus.getBusType().getName());


            // Manually fetch services for this bus type
            List<ServiceResponse> services = busMapper.getServicesByBusTypeId(bus.getBusType().getId())
                    .stream()
                    .map(s -> {
                        ServiceResponse sr = new ServiceResponse();
                        sr.setId(s.getId());
                        sr.setName(s.getName());
                        return sr;
                    }).toList();

            typeRes.setServices(services);
            res.setBusType(typeRes);
        }

        return res;
    }

    private RouteResponse mapRoute(Long routeId) {
        Route route = routeMapper.getRouteById(routeId);
        if (route == null) return null;

        RouteResponse r = new RouteResponse();
        r.setId(route.getId());
        r.setSource(route.getSource());
        r.setDestination(route.getDestination());
        r.setDistance(route.getDistance());
        r.setCreatedAt(route.getCreatedAt());
        r.setUpdatedAt(route.getUpdatedAt());
        return r;
    }

    private DriverResponse mapDriver(Long driverId) {
        Driver driver = driverMapper.getDriverById(driverId);
        if (driver == null) return null;

        DriverResponse d = new DriverResponse();
        d.setId(driver.getId());
        d.setName(driver.getName());
        d.setEmployeeId(driver.getEmployeeId());
        d.setLicenseNumber(driver.getLicenseNumber());
        d.setPhoneNumber(driver.getPhoneNumber());
        return d;
    }

    private AssistantResponse mapAssistant(Long assistantId) {
        Assistant assistant = assistantMapper.getAssistantById(assistantId);
        if (assistant == null) return null;

        AssistantResponse a = new AssistantResponse();
        a.setId(assistant.getId());
        a.setName(assistant.getName());
        a.setEmployeeId(assistant.getEmployeeId());
        a.setPhoneNumber(assistant.getPhoneNumber());
        return a;
    }

    private TripResponse mapToResponse(Trip trip) {
        TripResponse response = new TripResponse();
        response.setId(trip.getId());
        response.setBusId(trip.getBusId());
        response.setRouteId(trip.getRouteId());
        response.setDriverId(trip.getDriverId());
        response.setAssistantId(trip.getAssistantId());

        // Full date-time
        response.setDepartureDate(trip.getDepartureDate());
        response.setArrivalDate(trip.getArrivalDate());

        // 12-hour formatted time
        response.setDepartureTime(trip.getDepartureDate().format(TIME_12_FORMAT));
        response.setArrivalTime(trip.getArrivalDate().format(TIME_12_FORMAT));

        response.setDuration(trip.getDuration());
        response.setFare(trip.getFare());
        response.setCreatedAt(trip.getCreatedAt());
        response.setUpdatedAt(trip.getUpdatedAt());

        response.setBus(mapBus(trip.getBusId()));
        response.setRoute(mapRoute(trip.getRouteId()));
        response.setDriver(mapDriver(trip.getDriverId()));
        response.setAssistant(mapAssistant(trip.getAssistantId()));

        return response;
    }

    // =================== CRUD operations ===================
    @Override
    public ApiResponse<TripResponse> createTrip(TripRequest tripRequest) {

        String duration = calculateDuration(tripRequest.getDepartureDate(), tripRequest.getArrivalDate());
        tripRequest.setDuration(duration);

        double distance = routeMapper.getRouteById(tripRequest.getRouteId()).getDistance();
        double pricePerKm = busMapper.getBusById(tripRequest.getBusId()).getPricePerKm();
        double fare = roundToNearThousand(distance * pricePerKm);
        tripRequest.setFare(fare);

        LocalDate tripDate = tripRequest.getDepartureDate().toLocalDate();
        Long busTypeId = busMapper.getBusById(tripRequest.getBusId()).getBusType().getId();
        Long excludeId = null;

        int sameBusTypeCount = tripMapper.countSameBusTypeOnRoute(
                tripRequest.getRouteId(), busTypeId, tripDate, excludeId);

        Trip trip = new Trip();
        trip.setBusId(tripRequest.getBusId());
        trip.setRouteId(tripRequest.getRouteId());
        trip.setDriverId(tripRequest.getDriverId());
        trip.setAssistantId(tripRequest.getAssistantId());
        trip.setDepartureDate(tripRequest.getDepartureDate());
        trip.setArrivalDate(tripRequest.getArrivalDate());
        trip.setDuration(duration);
        trip.setFare(fare);
        trip.setCreatedAt(LocalDateTime.now());
        trip.setUpdatedAt(LocalDateTime.now());

        if (tripMapper.countDuplicateTrip(
                tripRequest.getRouteId(),
                tripRequest.getBusId(),
                tripRequest.getDepartureDate(),
                null // new trip
        ) > 0) {
            return new ApiResponse<>("FAILURE", "Duplicate trip exists", null);
        }
        if (tripMapper.countBusAssignments(tripRequest.getBusId(), tripRequest.getDepartureDate(), excludeId) > 0)
            throw new RuntimeException("This bus is already assigned to another trip on: " + tripDate);

        if (tripMapper.countDriverAssignments(tripRequest.getDriverId(), tripRequest.getDepartureDate(), excludeId) > 0)
            throw new RuntimeException("Driver already assigned another trip on: " + tripDate);

        if (tripMapper.countAssistantAssignments(tripRequest.getAssistantId(), tripRequest.getDepartureDate(), excludeId) > 0)
            throw new RuntimeException("Assistant already assigned another trip on: " + tripDate);

        if (sameBusTypeCount > 0)
            throw new RuntimeException("Same bus type already used on this route on: " + tripDate);

        tripMapper.createTrip(trip);

        return new ApiResponse<>("SUCCESS", "Trip created successfully", mapToResponse(trip));
    }

    // ================= GET ALL PAGINATED =================

    @Override
    public ApiResponse<PaginatedResponse<TripResponse>> getAllTrips(int offset, int limit) {
        if (offset < 0) offset = 0;
        if (limit < 1) limit = 10;

        List<Trip> trips = tripMapper.getAllTripsPaginated(offset, limit);
        List<TripResponse> responses = trips.stream().map(this::mapToResponse).toList();
        int total = tripMapper.countTrip();

        PaginatedResponse<TripResponse> paginated = new PaginatedResponse<>(offset, limit, total, responses);
        return new ApiResponse<>("SUCCESS", "Trips retrieved successfully", paginated);
    }

    // ================= GET BY ID =================

    @Override
    public ApiResponse<TripResponse> getTripById(Long id) {
        Trip trip = tripMapper.getTripById(id);
        if (trip == null)
            return new ApiResponse<>("NOT_FOUND", "Trip not found", null);

        return new ApiResponse<>("SUCCESS", "Trip retrieved", mapToResponse(trip));
    }

    // ================= UPDATE ====================

    @Override
    public ApiResponse<TripResponse> updateTrip(Long id, TripRequest tripRequest) {
        Trip trip = tripMapper.getTripById(id);
        if (trip == null) return new ApiResponse<>("NOT_FOUND", "Trip not found", null);

        String duration = calculateDuration(tripRequest.getDepartureDate(), tripRequest.getArrivalDate());
        tripRequest.setDuration(duration);

        double distance = routeMapper.getRouteById(tripRequest.getRouteId()).getDistance();
        double pricePerKm = busMapper.getBusById(tripRequest.getBusId()).getPricePerKm();
        double fare = roundToNearThousand(distance * pricePerKm);
        tripRequest.setFare(fare);

        trip.setBusId(tripRequest.getBusId());
        trip.setRouteId(tripRequest.getRouteId());
        trip.setDriverId(tripRequest.getDriverId());
        trip.setAssistantId(tripRequest.getAssistantId());
        trip.setDepartureDate(tripRequest.getDepartureDate());
        trip.setArrivalDate(tripRequest.getArrivalDate());
        trip.setDuration(duration);
        trip.setFare(fare);
        trip.setUpdatedAt(LocalDateTime.now());

        LocalDate tripDate = tripRequest.getDepartureDate().toLocalDate();
        Long busTypeId = busMapper.getBusById(tripRequest.getBusId()).getBusType().getId();

        if (tripMapper.countDuplicateTrip(
                tripRequest.getRouteId(),
                tripRequest.getBusId(),
                tripRequest.getDepartureDate(),
                id // current trip id
        ) > 0) {
            return new ApiResponse<>("FAILURE", "Duplicate trip exists", null);
        }
        if (tripMapper.countBusAssignments(tripRequest.getBusId(), tripRequest.getDepartureDate(), id) > 0)
            throw new RuntimeException("This bus is already assigned to another trip: " + tripDate);

        if (tripMapper.countDriverAssignments(tripRequest.getDriverId(), tripRequest.getDepartureDate(), id) > 0)
            throw new RuntimeException("Driver already assigned on: " + tripDate);

        if (tripMapper.countAssistantAssignments(tripRequest.getAssistantId(), tripRequest.getDepartureDate(), id) > 0)
            throw new RuntimeException("Assistant already assigned on: " + tripDate);

        if (tripMapper.countSameBusTypeOnRoute(tripRequest.getRouteId(), busTypeId, tripDate, id) > 0)
            throw new RuntimeException("Same bus type already used on this route on: " + tripDate);

        tripMapper.updateTrip(trip);

        return new ApiResponse<>("SUCCESS", "Trip updated successfully", mapToResponse(trip));
    }

    // ================= DELETE ====================

    @Override
    public ApiResponse<Void> deleteTrip(Long id) {
        tripMapper.deleteTrip(id);
        return new ApiResponse<>("SUCCESS", "Trip deleted successfully", null);
    }

    @Override
    public ApiResponse<List<TripResponse>> searchTripByDate(LocalDate departureDate) {
        return null;
    }

    @Override
    public ApiResponse<Integer> countTripsByDepartureDate(LocalDate departureDate) {
        return null;
    }

    // =============== Helpers =====================

    private String calculateDuration(LocalDateTime departure, LocalDateTime arrival) {
        Duration duration = Duration.between(departure, arrival);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        return hours + "h" + minutes + "m";
    }

    private final DateTimeFormatter TIME_12_FORMAT = DateTimeFormatter.ofPattern("hh:mm a");

    private double roundToNearThousand(double amount) {
        return Math.ceil(amount / 1000) * 1000;
    }
}