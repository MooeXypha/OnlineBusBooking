package com.xypha.onlineBus.trip.services;

import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.booking.mapper.BookingMapper;
import com.xypha.onlineBus.buses.Dto.BusResponse;
import com.xypha.onlineBus.buses.Entity.Bus;
import com.xypha.onlineBus.buses.busType.dto.BusTypeResponse;
import com.xypha.onlineBus.buses.mapper.BusMapper;
import com.xypha.onlineBus.buses.seat.mapper.SeatMapper;
import com.xypha.onlineBus.buses.seat.services.SeatService;
import com.xypha.onlineBus.buses.services.ServiceResponse;
import com.xypha.onlineBus.error.BadRequestException;
import com.xypha.onlineBus.error.ResourceNotFoundException;
import com.xypha.onlineBus.routes.Dto.RouteResponse;
import com.xypha.onlineBus.routes.Dto.RouteWithCity;
import com.xypha.onlineBus.routes.Entity.Route;
import com.xypha.onlineBus.routes.Mapper.CityMapper;
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
import com.xypha.onlineBus.trip.entity.TripStatus;
import com.xypha.onlineBus.trip.mapper.TripMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class TripServiceImpl implements TripService {

    private final RouteMapper routeMapper;
    private final TripMapper tripMapper;
    private final BusMapper busMapper;
    private final StaffService staffService;
    private final DriverMapper driverMapper;
    private final AssistantMapper assistantMapper;
    private final SeatService seatService;
    private final SeatMapper seatMapper;
    private final BookingMapper bookingMapper;
    private final CityMapper cityMapper;

    // Myanmar timezone offset
    private static final ZoneId MYANMAR_ZONE = ZoneId.of("Asia/Yangon");
    private final DateTimeFormatter TIME_12_FORMAT = DateTimeFormatter.ofPattern("hh:mm a");

    public TripServiceImpl(RouteMapper routeMapper, TripMapper tripMapper, BusMapper busMapper,
                           StaffService staffService, DriverMapper driverMapper,
                           AssistantMapper assistantMapper, SeatService seatService,
                           SeatMapper seatMapper, BookingMapper bookingMapper, CityMapper cityMapper) {
        this.routeMapper = routeMapper;
        this.tripMapper = tripMapper;
        this.busMapper = busMapper;
        this.staffService = staffService;
        this.driverMapper = driverMapper;
        this.assistantMapper = assistantMapper;
        this.seatService = seatService;
        this.seatMapper = seatMapper;
        this.bookingMapper = bookingMapper;
        this.cityMapper = cityMapper;
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
            typeRes.setSeatPerRow(bus.getBusType().getSeatPerRow());

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

    private RouteResponse mapRoute(RouteWithCity route) {
        if (route.getSourceName() == null || route.getDestinationName() == null) {
            throw new RuntimeException("City not found for route");
        }
        RouteResponse r = new RouteResponse();
        r.setId(route.getId());
        r.setSource(route.getSourceName());
        r.setDestination(route.getDestinationName());
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

        LocalDateTime depUtc = trip.getDepartureDate();
        LocalDateTime arrUtc = trip.getArrivalDate();

        response.setDepartureDate(depUtc);
        response.setArrivalDate(arrUtc);

        response.setDepartureTime(
                depUtc != null ? depUtc.format(TIME_12_FORMAT) : null
        );

        response.setArrivalTime(
                arrUtc != null ? arrUtc.format(TIME_12_FORMAT) : null
        );

        response.setDuration(trip.getDuration());
        response.setFare(trip.getFare());
        response.setCreatedAt(trip.getCreatedAt());
        response.setUpdatedAt(trip.getUpdatedAt());
        response.setStatus(getTripStatus(trip));

        response.setBus(mapBus(trip.getBusId()));

        RouteWithCity routeWithCity = tripMapper.getRouteWithCityByTripId(trip.getId());
        response.setRoute(mapRoute(routeWithCity));
        response.setDriver(mapDriver(trip.getDriverId()));
        response.setAssistant(mapAssistant(trip.getAssistantId()));

        return response;
    }

    // =================== CRUD operations ===================
    @Override
    public ApiResponse<TripResponse> createTrip(TripRequest tripRequest) {

        String duration = calculateDuration(tripRequest.getDepartureDate(),
                tripRequest.getArrivalDate());
        tripRequest.setDuration(duration);

        Route route = routeMapper.getRouteById(tripRequest.getRouteId());
        Bus bus = busMapper.getBusById(tripRequest.getBusId());
        if (bus == null){
            throw new ResourceNotFoundException("Bus not found");
        }
        if (route == null){
            throw new ResourceNotFoundException("Route not found");
        }

        double distance = route.getDistance();
        double pricePerKm = bus.getPricePerKm();
        double fare = roundToNearThousand(distance * pricePerKm);
        tripRequest.setFare(fare);


        LocalDateTime now = LocalDateTime.now();

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

        // Now using OffsetDateTime
        trip.setDepartureDate(tripRequest.getDepartureDate());
        trip.setArrivalDate(tripRequest.getArrivalDate());

        trip.setDuration(duration);
        trip.setFare(fare);
        trip.setCreatedAt(now);
        trip.setUpdatedAt(now);

        // Duplicate checks
        if (tripMapper.countDuplicateTrip(
                tripRequest.getRouteId(),
                tripRequest.getBusId(),
                tripRequest.getDepartureDate(),
                null
        ) > 0) {
            return new ApiResponse<>("FAILURE", "Duplicate trip exists", null);
        }

        if (tripMapper.countBusAssignments(tripRequest.getBusId(), tripRequest.getDepartureDate(), null) > 0)
            throw new BadRequestException("This bus is already assigned on: " + tripDate);

        if (tripMapper.countDriverAssignments(tripRequest.getDriverId(), tripRequest.getDepartureDate(), null) > 0)
            throw new BadRequestException("Driver already assigned on: " + tripDate);

        if (tripMapper.countAssistantAssignments(tripRequest.getAssistantId(), tripRequest.getDepartureDate(), null) > 0)
            throw new BadRequestException("Assistant already assigned on: " + tripDate);

        if (sameBusTypeCount > 0)
            throw new BadRequestException("Same bus type already used on this route on: " + tripDate);

        tripMapper.createTrip(trip);
        seatService.generateSeatsForTrip(trip.getId(), trip.getBusId());


        return new ApiResponse<>("SUCCESS", "Trip created successfully", mapToResponse(trip));
    }

    @Override
    public ApiResponse<TripResponse> updateTrip(Long id, TripRequest tripRequest) {
        Trip trip = tripMapper.getTripById(id);
        if (trip == null) return new ApiResponse<>("NOT_FOUND", "Trip not found", null);

        if (tripRequest.getArrivalDate().isBefore(tripRequest.getDepartureDate())){
            throw new BadRequestException("Arrival time cannot be before departure time");
        }
        String duration = calculateDuration(tripRequest.getDepartureDate(),
                tripRequest.getArrivalDate());
        tripRequest.setDuration(duration);

        double distance = routeMapper.getRouteById(tripRequest.getRouteId()).getDistance();
        double pricePerKm = busMapper.getBusById(tripRequest.getBusId()).getPricePerKm();
        double fare = roundToNearThousand(distance * pricePerKm);
        tripRequest.setFare(fare);

        LocalDateTime now = LocalDateTime.now();
        Long oldBusId = trip.getBusId();
        LocalDate tripDate = tripRequest.getDepartureDate().toLocalDate();
        Long busTypeId = busMapper.getBusById(tripRequest.getBusId()).getBusType().getId();

        if (tripMapper.countDuplicateTrip(tripRequest.getRouteId(), tripRequest.getBusId(), tripRequest.getDepartureDate(), id) > 0)
            return new ApiResponse<>("FAILURE", "Duplicate trip exists", null);

        if (tripMapper.countBusAssignments(tripRequest.getBusId(), tripRequest.getDepartureDate(), id) > 0)
            throw new BadRequestException("This bus is already assigned: " + tripDate);

        if (tripMapper.countDriverAssignments(tripRequest.getDriverId(), tripRequest.getDepartureDate(), id) > 0)
            throw new BadRequestException("Driver already assigned: " + tripDate);

        if (tripMapper.countAssistantAssignments(tripRequest.getAssistantId(), tripRequest.getDepartureDate(), id) > 0)
            throw new BadRequestException("Assistant already assigned: " + tripDate);

        if (tripMapper.countSameBusTypeOnRoute(tripRequest.getRouteId(), busTypeId, tripDate, id) > 0)
            throw new BadRequestException("Same bus type already used on this route: " + tripDate);


        trip.setBusId(tripRequest.getBusId());
        trip.setRouteId(tripRequest.getRouteId());
        trip.setDriverId(tripRequest.getDriverId());
        trip.setAssistantId(tripRequest.getAssistantId());
        trip.setDepartureDate(tripRequest.getDepartureDate());
        trip.setArrivalDate(tripRequest.getArrivalDate());
        trip.setDuration(duration);
        trip.setFare(fare);
        trip.setUpdatedAt(now);

        tripMapper.updateTrip(trip);
        if (!oldBusId.equals(tripRequest.getBusId())){
            seatMapper.deleteSeatsByTripId(trip.getId());
            seatService.generateSeatsForTrip(trip.getId(), trip.getBusId());
        }
        Trip updateTrip = tripMapper.getTripWithRouteAndCity(id);
        return new ApiResponse<>("SUCCESS", "Trip updated successfully", mapToResponse(trip));
    }

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

    @Override
    public ApiResponse<TripResponse> getTripById(Long id) {
        Trip trip = tripMapper.getTripById(id);
        if (trip == null)
            throw new ResourceNotFoundException("Trip not found");

        return new ApiResponse<>("SUCCESS", "Trip retrieved", mapToResponse(trip));
    }

    @Override
    public ApiResponse<Void> deleteTrip(Long id) {
        return deleteTripIfAllowed(id);
    }

    @Override
    public ApiResponse<List<TripResponse>> searchTripByDate(LocalDate departureDate) {
        return null;
    }

    @Override
    public ApiResponse<Integer> countTripsByDepartureDate(LocalDate departureDate) {
        return null;
    }

    @Transactional
    public ApiResponse<Void> deleteTripIfAllowed(Long id){
        Trip trip = tripMapper.getTripById(id);
        if (trip == null){
            throw new ResourceNotFoundException("Trip not found");
        }

        int activeBookings = bookingMapper.countActiveBookingsByTripId(id);
        if (activeBookings > 0){
            throw new BadRequestException("Cannot delete trip: there are " + activeBookings + " active bookings associated");
        }

        bookingMapper.deleteAllCancelledBookingsByTripId(id);
        seatMapper.releaseAllSeatsByTrip(id);
        tripMapper.deleteTrip(id);

        return new ApiResponse<>("SUCCESS", "Trip deleted successfully", null);
    }

    @Transactional
    public void forceDeleteCompleteTrip (Long tripId){
        seatMapper.releaseAllSeatsByTrip(tripId);
        bookingMapper.deleteAllByTripId(tripId);
        tripMapper.deleteTripById(tripId);
    }

    @Override
    public ApiResponse<List<TripResponse>> searchTrips(String source, String destination, LocalDate departureDate) {

        if (source == null && destination == null && departureDate == null){
            throw new BadRequestException("At least one search parameter must be provided");
        }

        List<Trip> trips = tripMapper.searchTrips(source, destination, departureDate);
        if (trips.isEmpty()){
            throw new ResourceNotFoundException("No trips found on selected date");
        }

        List<TripResponse> responses = trips.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return new ApiResponse<>("SUCCESS", "Trips retrieved successfully", responses);
    }

    // =================== Helpers ===================
    private String calculateDuration(LocalDateTime departure, LocalDateTime arrival) {
        Duration duration = Duration.between(departure, arrival);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        if (arrival.isBefore(departure)){
            throw new BadRequestException("Arrival time cannot be before departure time");
        }
        return hours + "h" + minutes + "m";
    }

    private double roundToNearThousand(double amount) {
        return Math.ceil(amount / 1000) * 1000;
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    public void autoDeleteExpiredTrips(){
        LocalDate now = LocalDate.now(MYANMAR_ZONE);
        LocalDateTime startOfLastMonth =
                now.minusMonths(1).withDayOfMonth(1).atStartOfDay();

        LocalDateTime endOfLastMonth =
                now.withDayOfMonth(1).atStartOfDay();

        List<Long> tripIds = tripMapper.findCompletedTripsRange(startOfLastMonth,endOfLastMonth);
        for (Long tripId : tripIds){
            try{
                forceDeleteCompleteTrip(tripId);
            }catch (Exception e){
                System.out.println("Failed to delete trip "+ tripId);
                e.printStackTrace();
            }
        }
    }

    private TripStatus getTripStatus (Trip trip){
        LocalDateTime now = LocalDateTime.now(MYANMAR_ZONE);

        if (now.isBefore(trip.getDepartureDate())){
            return TripStatus.UPCOMING;
        }if (now.isAfter(trip.getArrivalDate())){
            return TripStatus.COMPLETED;
        }
        return TripStatus.COMPLETED;
    }

}
