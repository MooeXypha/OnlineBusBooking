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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        response.setDepartureDate(trip.getDepartureDate());
        response.setArrivalDate(trip.getArrivalDate());
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
        BusResponse bus = mapBus(tripRequest.getBusId());
        RouteResponse route = mapRoute(tripRequest.getRouteId());

        if (bus == null) return new ApiResponse<>("FAILURE", "Bus not found", null);
        if (route == null) return new ApiResponse<>("FAILURE", "Route not found", null);

//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime departure = tripRequest.getDepartureDate();
//        LocalDateTime arrival = departure.plusMinutes(route.getDuration());
//
//        if (departure.isBefore(now))
//            return new ApiResponse<>("FAILURE", "Departure must be in the future", null);
//
//        if (tripMapper.countBusConflict(tripRequest.getBusId(), departure, arrival) > 0)
//            return new ApiResponse<>("FAILURE", "Bus already assigned in this time range", null);
//
//        if (tripMapper.countDriverConflict(tripRequest.getDriverId(), departure, arrival) > 0)
//            return new ApiResponse<>("FAILURE", "Driver already assigned in this time range", null);
//
//        if (tripMapper.countAssistantConflict(tripRequest.getAssistantId(), departure, arrival) > 0)
//            return new ApiResponse<>("FAILURE", "Assistant already assigned in this time range", null);
//
//        double fare = Math.ceil(bus.getPricePerKm() * route.getDistance() / 100) * 100;

        Trip trip = new Trip();
        trip.setBusId(tripRequest.getBusId());
        trip.setRouteId(tripRequest.getRouteId());
        trip.setDriverId(tripRequest.getDriverId());
        trip.setAssistantId(tripRequest.getAssistantId());
//        trip.setDepartureDate(departure);
//        trip.setArrivalDate(arrival);
//        trip.setFare(fare);
//        trip.setCreatedAt(now);
//        trip.setUpdatedAt(now);

        if (tripMapper.countDuplicateTrip(trip) > 0)
            return new ApiResponse<>("FAILURE", "Duplicate trip exists", null);

        tripMapper.createTrip(trip);

        return new ApiResponse<>("SUCCESS", "Trip created successfully", mapToResponse(trip));
    }

    @Override
    public ApiResponse<PaginatedResponse<TripResponse>> getAllTrips(int page, int size) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        int offset = (page - 1) * size;
        List<Trip> trips = tripMapper.getAllTripsPaginated(offset, size);
        List<TripResponse> responses = trips.stream().map(this::mapToResponse).toList();
        int total = tripMapper.countTrip();

        PaginatedResponse<TripResponse> paginated = new PaginatedResponse<>(offset, size, total, responses);
        return new ApiResponse<>("SUCCESS", "Trips retrieved successfully", paginated);
    }

    @Override
    public ApiResponse<TripResponse> getTripById(Long id) {
        Trip trip = tripMapper.getTripById(id);
        if (trip == null) return new ApiResponse<>("NOT_FOUND", "Trip not found", null);
        return new ApiResponse<>("SUCCESS", "Trip retrieved", mapToResponse(trip));
    }

    @Override
    public ApiResponse<TripResponse> updateTrip(Long id, TripRequest tripRequest) {
        Trip trip = tripMapper.getTripById(id);
        if (trip == null) return new ApiResponse<>("NOT_FOUND", "Trip not found", null);

        BusResponse bus = mapBus(tripRequest.getBusId());
        RouteResponse route = mapRoute(tripRequest.getRouteId());

        trip.setBusId(tripRequest.getBusId());
        trip.setRouteId(tripRequest.getRouteId());
        trip.setDriverId(tripRequest.getDriverId());
        trip.setAssistantId(tripRequest.getAssistantId());
        trip.setDepartureDate(tripRequest.getDepartureDate());
//        trip.setArrivalDate(tripRequest.getDepartureDate().plusMinutes(route.getDuration()));
        trip.setFare(Math.ceil(bus.getPricePerKm() * route.getDistance() / 100) * 100);
        trip.setUpdatedAt(LocalDateTime.now());

        tripMapper.updateTrip(trip);
        return new ApiResponse<>("SUCCESS", "Trip updated successfully", mapToResponse(trip));
    }

    @Override
    public ApiResponse<Void> deleteTrip(Long id) {
        tripMapper.deleteTrip(id);
        return new ApiResponse<>("SUCCESS", "Trip deleted successfully", null);
    }

    @Override
    public ApiResponse<List<TripResponse>> searchTripByDate(LocalDate departureDate) {
        List<Trip> trips = tripMapper.searchTripsByDepartureDate(departureDate);
        List<TripResponse> responses = trips.stream().map(this::mapToResponse).toList();
        return new ApiResponse<>("SUCCESS", "Trips found", responses);
    }

    @Override
    public ApiResponse<Integer> countTripsByDepartureDate(LocalDate departureDate) {
        int count = tripMapper.countTripsByDepartureDate(departureDate);
        return new ApiResponse<>("SUCCESS", "Trips counted", count);
    }
}
