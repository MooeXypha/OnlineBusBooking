package com.xypha.onlineBus.booking.services;

import com.xypha.onlineBus.account.users.entity.User;
import com.xypha.onlineBus.account.users.mapper.UserMapper;
import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.booking.dto.BookingRequest;
import com.xypha.onlineBus.booking.dto.BookingResponse;
import com.xypha.onlineBus.booking.entity.Booking;
import com.xypha.onlineBus.booking.mapper.BookingMapper;
import com.xypha.onlineBus.buses.Dto.BusResponse;
import com.xypha.onlineBus.buses.Entity.Bus;
import com.xypha.onlineBus.buses.busType.dto.BusTypeResponse;
import com.xypha.onlineBus.buses.mapper.BusMapper;
import com.xypha.onlineBus.buses.seat.entity.Seat;
import com.xypha.onlineBus.buses.seat.mapper.SeatMapper;
import com.xypha.onlineBus.buses.services.ServiceResponse;
import com.xypha.onlineBus.error.BadRequestException;
import com.xypha.onlineBus.error.ResourceNotFoundException;
import com.xypha.onlineBus.mail.EmailService;
import com.xypha.onlineBus.routes.Dto.RouteResponse;
import com.xypha.onlineBus.routes.Entity.Route;
import com.xypha.onlineBus.routes.Mapper.RouteMapper;
import com.xypha.onlineBus.staffs.Assistant.Dto.AssistantResponse;
import com.xypha.onlineBus.staffs.Assistant.Entity.Assistant;
import com.xypha.onlineBus.staffs.Assistant.Mapper.AssistantMapper;

import com.xypha.onlineBus.staffs.Driver.Dto.DriverResponse;
import com.xypha.onlineBus.staffs.Driver.Entity.Driver;
import com.xypha.onlineBus.staffs.Driver.Mapper.DriverMapper;
import com.xypha.onlineBus.trip.dto.TripResponse;
import com.xypha.onlineBus.trip.entity.Trip;
import com.xypha.onlineBus.trip.mapper.TripMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {

    private final SeatMapper seatMapper;
    private final BookingMapper bookingMapper;
    private final TripMapper tripMapper;
    private final RouteMapper routeMapper;
    private final GenerateBookingCode generateBookingCode;
    private final UserMapper userMapper;
    private final BusMapper busMapper;
    private final DriverMapper driverMapper;
    private final AssistantMapper assistantMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final BookingEmailService bookingEmailService;
private final EmailService emailService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public BookingService(SeatMapper seatMapper, BookingMapper bookingMapper, TripMapper tripMapper, RouteMapper routeMapper, GenerateBookingCode generateBookingCode, UserMapper userMapper, BusMapper busMapper, DriverMapper driverMapper, AssistantMapper assistantMapper, ApplicationEventPublisher eventPublisher, BookingEmailService bookingEmailService, EmailService emailService, SimpMessagingTemplate messagingTemplate) {
        this.seatMapper = seatMapper;
        this.bookingMapper = bookingMapper;
        this.tripMapper = tripMapper;
        this.routeMapper = routeMapper;
        this.generateBookingCode = generateBookingCode;
        this.userMapper = userMapper;
        this.busMapper = busMapper;
        this.driverMapper = driverMapper;
        this.assistantMapper = assistantMapper;
        this.eventPublisher = eventPublisher;
        this.bookingEmailService = bookingEmailService;
        this.emailService = emailService;
        this.messagingTemplate = messagingTemplate;
    }

    private static final ZoneId MYANMAR_ZONE = ZoneId.of("Asia/Yangon");

    @Transactional
    public ApiResponse<BookingResponse> createBooking(BookingRequest request, Long userId) {

        // 1️⃣ Validate seat selection
        if (request.getSeatNumbers() == null || request.getSeatNumbers().isEmpty()) {
            throw new IllegalArgumentException ( "No seats selected");
        }
        if (request.getSeatNumbers().size() > 5) {
            throw  new IllegalArgumentException("Maximum 5 seats allowed per booking");
        }

        // 2️⃣ Get trip
        Trip trip = tripMapper.getTripFareById(request.getTripId());
        if (trip == null) throw new ResourceNotFoundException("Trip not found");

        // 3️⃣ Check trip timing
        OffsetDateTime now = OffsetDateTime.now(ZoneId.of("Asia/Yangon"));

        if (now.isAfter(trip.getDepartureDate())) throw new IllegalArgumentException("Trip already departed");
        if (now.isAfter(trip.getDepartureDate().minusMinutes(30))) throw new BadRequestException("Booking closed for this trip");

        // 4️⃣ Create booking
        Booking booking = createBookingEntity(trip, userId, request.getSeatNumbers().size(), now);
        // 5️⃣ Lock & update seats
        List<String> bookedSeats = lockAndBookSeats(trip.getId(), request.getSeatNumbers(), booking.getId());

        User user = userMapper.getUserById(booking.getUserId());

        if (user != null && user.getGmail() != null){
            bookingEmailService.sendBookingPendingEmail(
                    user.getGmail(),
                    booking.getBookingCode(),
                    booking.getTotalAmount(),
                    request.getSeatNumbers(),
                    routeMapper.getRouteById(trip.getRouteId()).getSource(),
                    routeMapper.getRouteById(trip.getRouteId()).getDestination(),
                    trip.getDepartureDate()
            );
        }



        // 6️⃣ Build response
        Trip fullTrip = tripMapper.getTripById(trip.getId()); // get full trip with associations
        TripResponse tripResponse = mapTripToResponse(fullTrip);

        BookingResponse response = new BookingResponse();
        response.setBookingCode(booking.getBookingCode());
        response.setSeatNumbers(bookedSeats);
        response.setTotalAmount(booking.getTotalAmount());
        response.setStatus(booking.getStatus());
        response.setUserId(booking.getUserId());
        response.setUserName(booking.getUserName());
        response.setTrip(tripResponse);
        response.setCreatedAt(booking.getCreatedAt());
        response.setUpdatedAt(booking.getUpdatedAt());

        return new ApiResponse<>("SUCCESS", "Booking created successfully", response);
    }



    ///getby booking code
    public ApiResponse<BookingResponse> getBookingByCode (String bookingCode){
        BookingResponse response = bookingMapper.searchByBookingCode(bookingCode);
        if (response != null) {
            Long tripId = bookingMapper.getTripIdByBookingCode(bookingCode); // simple query
            if (tripId != null) {
                Trip tripEntity = tripMapper.getTripById(tripId);
                if (tripEntity != null) {
                    response.setTrip(mapTripToResponse(tripEntity));
                }
            }
        }


        return new ApiResponse<>("SUCCESS","Booking retrieved successfully: "+ bookingCode, response);
    }

    @Transactional
    public ApiResponse<Void> confirmPayment (String bookingCode){
        Booking booking = bookingMapper.getByBookingCode(bookingCode);
        if(booking == null)
            return new ApiResponse<>("FAILURE", "Booking not found", null);
        if (!booking.getStatus().equals("PENDING"))
            return new ApiResponse<>("FAILURE", "Booking not in PENDING status", null);


        //load trip + route info
        Trip trip = tripMapper.getTripById(booking.getTripId());
        if (trip == null){
            return new ApiResponse<>("FAILURE","Trip not found", null);
        }

        Route route = routeMapper.getRouteById(trip.getRouteId());
        if (route == null){
            return new ApiResponse<>("FAILURE","Route not found", null);
        }

        //Mark seats as Taken
        List<Long> seatIds = bookingMapper.getSeatIdsByBookingId(booking.getId());
        if (seatIds.isEmpty()){
            throw new RuntimeException("No seats found for booking");
        }

        //Update booking status to CONFIRMED
        bookingMapper.updateStatus(bookingCode, "CONFIRMED");

        //update seat to taken
        for (Long seatId : seatIds){
            seatMapper.updateSeatStatus(seatId, 2);
        }

//        Get user email
        String userEmail = userMapper.getEmailById(booking.getUserId());

        //Send confirmation email
        bookingEmailService.sendConfirmedTicketEmail(
                userEmail,
                booking.getBookingCode(),
                route.getSource(),
                route.getDestination(),
                trip.getDepartureDate(),
                bookingMapper.getSeatNumbersByBookingId(booking.getId()),
                booking.getTotalAmount()
        );

        return new ApiResponse<>("SUCCESS", "Payment confirmed, booking status updated to CONFIRMED: " + bookingCode, null);

    }
//
    @Transactional
    public ApiResponse<Void> cancelBooking (String bookingCode){
        Booking booking = bookingMapper.getByBookingCode(bookingCode);
        if(booking == null)
            return new ApiResponse<>("FAILURE", "Booking not found", null);
        if ("CANCELLED".equals(booking.getStatus()))
            return new ApiResponse<>("FAILURE", "Booking already CANCELLED", null);

        List<Long> seatIds = bookingMapper.getSeatIdsByBookingId(booking.getId());
        for (Long seatId : seatIds){
            seatMapper.updateSeatStatus(seatId, 0);
        }

        bookingMapper.updateStatus(bookingCode, "CANCELLED");
        return new ApiResponse<>("SUCCESS", "Booking cancelled successfully: "+bookingCode, null);
    }



    @Transactional
    public ApiResponse<BookingResponse> updateBookingStatus(String bookingCode, String newStatus){
        Booking booking = bookingMapper.getByBookingCode(bookingCode);
        if (booking == null){
            throw new ResourceNotFoundException("Booking not found");
        }
        if ("CANCELLED".equals(booking.getStatus())) {
            throw new IllegalArgumentException("Cancelled booking cannot be updated");
        }
         newStatus = newStatus.toUpperCase();

        //Validate allowed status
        if (!List.of("PENDING", "CONFIRMED","CANCELLED").contains(newStatus)){
            throw new IllegalArgumentException("Invalid status"+ newStatus);
        }
        if (booking.getTripId() != null){
            Trip tripEntity = tripMapper.getTripById(booking.getTripId());
            if (tripEntity != null && OffsetDateTime.now(MYANMAR_ZONE).isAfter(tripEntity.getDepartureDate())){
                throw new IllegalArgumentException("Cannot update booking for departed trip");
            }
        }

        bookingMapper.updateStatus(bookingCode, newStatus);

        if ("CANCELLED".equals(newStatus)){
            List<Long> seatIds = bookingMapper.getSeatIdsByBookingId(booking.getId());
            for (Long seatId : seatIds){
                seatMapper.updateSeatStatus(seatId, 0);
            }
        }else if("CONFIRMED". equals(newStatus)){
            List<Long> seatIds = bookingMapper.getSeatIdsByBookingId(booking.getId());
            for (Long seatId : seatIds){
                seatMapper.updateSeatStatus(seatId, 2);
            }
        }
        BookingResponse response = new BookingResponse();
        response.setBookingCode(booking.getBookingCode());
        response.setSeatNumbers(booking.getSeatNumbers());
        response.setTotalAmount(booking.getTotalAmount());
        response.setStatus(newStatus);
        response.setUserId(booking.getUserId());
        response.setUserName(booking.getUserName());

        if (booking.getUserId() != null){
            messagingTemplate.convertAndSend(
                    "/topic/user/" + booking.getUserId() + "/booking",
                    "Your booking" + bookingCode + "status is now" + newStatus
            );
            System.out.println("Sent WebSocket notification to user " + booking.getUserId() + " for booking " + bookingCode);
        }

return new ApiResponse<>("SUCCESS", "Booking status update to "+ newStatus, response);
    }

    @Transactional
    public ApiResponse<PaginatedResponse<BookingResponse>> getAllBookingPaginated (String status, int offset, int limit){
        List<Booking> bookings = bookingMapper.getAllBookingsPaginated(status, limit, offset);
        List<BookingResponse> responseList = bookings.stream().map(booking -> {
            BookingResponse response = new BookingResponse();
            response.setBookingCode(booking.getBookingCode());
            response.setSeatNumbers(bookingMapper.getSeatNumbersByBookingId(booking.getId()));
            response.setTotalAmount(booking.getTotalAmount());
            response.setStatus(booking.getStatus());
            response.setUserId(booking.getUserId());
            response.setUserName(booking.getUserName());
            response.setCreatedAt(booking.getCreatedAt());
            response.setUpdatedAt(booking.getUpdatedAt());

            if (booking.getTripId() != null){
                Trip tripEntity = tripMapper.getTripById(booking.getTripId());
                if (tripEntity != null) {
                    response.setTrip(mapTripToResponse(tripEntity));
                }
            }

            return response;
        }).toList();

        int total = bookingMapper.countBookingByStatus(status);
        PaginatedResponse<BookingResponse> paginatedResponse = new PaginatedResponse<>(offset, limit, total, responseList);

        return new ApiResponse<>("SUCCESS", "Bookings retrieved successfully", paginatedResponse);
    }


    @Transactional (readOnly = true)
    public ApiResponse<PaginatedResponse<BookingResponse>> getUserBookingHistory(
            Long userId,String status ,int offset, int limit
    ){

        List<BookingResponse> responseList = bookingMapper.getUserBookingHistory(userId,status,offset,limit);

        for (BookingResponse response : responseList){
            if (response.getTripId() != null){
                Trip trip = tripMapper.getTripById(response.getTripId());
                if (trip != null){
                    response.setTrip(mapTripToResponse(trip));
                }
            }
        }
        int total = bookingMapper.countBookingHistory(userId, status);
        PaginatedResponse<BookingResponse> paginatedResponse = new PaginatedResponse<>(offset,limit, total, responseList);
        return new ApiResponse<>("SUCCESS", "Booking history retrieved successfully: "+userId, paginatedResponse);
    }



    @Transactional
    public ApiResponse<Void> cancelTripAndReleaseSeats(Long tripId) {

        // 1️⃣ Cancel all bookings
        int cancelledBookings = bookingMapper.cancelAllBooingByTripId(tripId);

        // 2️⃣ Release all seats
        int releasedSeats = seatMapper.releaseAllSeatsByTrip(tripId);

        if (cancelledBookings == 0 && releasedSeats == 0) {
            throw new IllegalArgumentException("No active bookings or seats to cancel for trip: " + tripId);
        }

        return new ApiResponse<>("SUCCESS",
                "All bookings cancelled and seats are now available for trip: " + tripId,
                null);
    }















    ///////////////////////External Map To Response
    private Booking createBookingEntity (Trip trip, Long userId,Integer seatCount, OffsetDateTime now){
        BigDecimal totalAmount = BigDecimal.valueOf(trip.getFare()).multiply(BigDecimal.valueOf(seatCount));
        Booking booking = new Booking();
        booking.setBookingCode(generateBookingCode.generate());
        booking.setTripId(trip.getId());
        booking.setUserId(userId);
        booking.setTotalAmount(totalAmount.doubleValue());
        booking.setStatus("PENDING");
        booking.setCreatedAt(now);
        booking.setUpdatedAt(now);
        booking.setUserName(userMapper.getNameById(userId));
        bookingMapper.createBooking(booking);
        return booking;
    }
    private List<String> lockAndBookSeats(Long tripId, List<String> seatNumbers, Long bookingId) {
        List<String> bookedSeats = new ArrayList<>();
        for (String seatNo : seatNumbers) {
            Seat seat = seatMapper.lockSeatForUpdate(tripId, seatNo);
            if (seat == null) throw new IllegalArgumentException("Seat not found: " + seatNo);
            if (seat.getStatus() != 0) throw new IllegalArgumentException("Seat not available: " + seatNo);

            seatMapper.updateSeatStatus(seat.getId(), 1);
            bookingMapper.createBookingSeat(bookingId, seat.getId(), tripId);
            bookedSeats.add(seatNo);
        }
        return bookedSeats;
    }

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

    private TripResponse mapTripToResponse(Trip trip) {
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
    private final DateTimeFormatter TIME_12_FORMAT = DateTimeFormatter.ofPattern("hh:mm a");



}




