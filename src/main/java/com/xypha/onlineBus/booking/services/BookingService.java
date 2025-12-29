package com.xypha.onlineBus.booking.services;

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
import com.xypha.onlineBus.buses.busType.entity.BusType;
import com.xypha.onlineBus.buses.mapper.BusMapper;
import com.xypha.onlineBus.buses.seat.entity.Seat;
import com.xypha.onlineBus.buses.seat.mapper.SeatMapper;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Book;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private final JavaMailSender mailSender;
    private final BusMapper busMapper;
    private final DriverMapper driverMapper;
    private final AssistantMapper assistantMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final BookingEmailService bookingEmailService;

    public BookingService(SeatMapper seatMapper, BookingMapper bookingMapper, TripMapper tripMapper, RouteMapper routeMapper, GenerateBookingCode generateBookingCode, UserMapper userMapper, JavaMailSender mailSender, BusMapper busMapper, DriverMapper driverMapper, AssistantMapper assistantMapper, ApplicationEventPublisher eventPublisher, BookingEmailService bookingEmailService) {
        this.seatMapper = seatMapper;
        this.bookingMapper = bookingMapper;
        this.tripMapper = tripMapper;
        this.routeMapper = routeMapper;
        this.generateBookingCode = generateBookingCode;
        this.userMapper = userMapper;
        this.mailSender = mailSender;
        this.busMapper = busMapper;
        this.driverMapper = driverMapper;
        this.assistantMapper = assistantMapper;
        this.eventPublisher = eventPublisher;
        this.bookingEmailService = bookingEmailService;
    }

    @Transactional
    public ApiResponse<BookingResponse> createBooking(BookingRequest request, Long userId) {

        // 1️⃣ Validate seat selection
        if (request.getSeatNumbers() == null || request.getSeatNumbers().isEmpty()) {
            return new ApiResponse<>("FAILURE", "No seats selected", null);
        }
        if (request.getSeatNumbers().size() > 5) {
            return new ApiResponse<>("FAILURE", "Maximum 5 seats allowed per booking", null);
        }

        // 2️⃣ Get trip
        Trip trip = tripMapper.getTripFareById(request.getTripId());
        if (trip == null) return new ApiResponse<>("FAILURE", "Trip not found", null);

        // 3️⃣ Check trip timing
        LocalDateTime now = LocalDateTime.now();
        if (trip.getDepartureDate().isBefore(now)) return new ApiResponse<>("FAILURE", "Trip already departed", null);
        if (trip.getDepartureDate().minusMinutes(30).isBefore(now)) return new ApiResponse<>("FAILURE", "Booking closed for this trip", null);

        // 4️⃣ Create booking
        Booking booking = createBookingEntity(trip, userId, request.getSeatNumbers().size(), now);

        // 5️⃣ Lock & update seats
        List<String> bookedSeats = lockAndBookSeats(trip.getId(), request.getSeatNumbers(), booking.getId());

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
        Booking booking = bookingMapper.getByBookingCode(bookingCode);
        if(booking == null) {
            return new ApiResponse<>("FAILURE", "Booking not found", null);
        }
        BookingResponse response = new BookingResponse();
        response.setBookingCode(booking.getBookingCode());
        response.setSeatNumbers(bookingMapper.getSeatNumbersByBookingId(booking.getId()));
        response.setTotalAmount(booking.getTotalAmount());
        response.setStatus(booking.getStatus());

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

        //Get user email
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
            return new ApiResponse<>("FAILURE", "No active bookings or seats to cancel for trip: " + tripId, null);
        }

        return new ApiResponse<>("SUCCESS",
                "All bookings cancelled and seats are now available for trip: " + tripId,
                null);
    }









    ///////////////////////External Map To Response
    private Booking createBookingEntity (Trip trip, Long userId,Integer seatCount, LocalDateTime now){
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
            if (seat == null) throw new RuntimeException("Seat not found: " + seatNo);
            if (seat.getStatus() != 0) throw new RuntimeException("Seat not available: " + seatNo);

            seatMapper.updateSeatStatus(seat.getId(), 1);
            bookingMapper.createBookingSeat(bookingId, seat.getId(), tripId);
            bookedSeats.add(seatNo);
        }
        return bookedSeats;
    }

    private TripResponse mapTripToResponse(Trip tripEntity) {
        TripResponse response = new TripResponse();
        response.setId(tripEntity.getId());
        response.setRouteId(tripEntity.getRouteId());
        response.setBusId(tripEntity.getBusId());
        response.setDepartureDate(tripEntity.getDepartureDate());
        response.setArrivalDate(tripEntity.getArrivalDate());
        response.setFare(tripEntity.getFare());
        response.setDuration(tripEntity.getDuration());
        response.setCreatedAt(tripEntity.getCreatedAt());
        response.setUpdatedAt(tripEntity.getUpdatedAt());

        // Map associated entities
        if (tripEntity.getRouteId() != null) {
            Route route = routeMapper.getRouteById(tripEntity.getRouteId());
            if (route != null) {
                RouteResponse routeResponse = new RouteResponse();
                routeResponse.setId(route.getId());
                routeResponse.setSource(route.getSource());
                routeResponse.setDestination(route.getDestination());
                routeResponse.setDistance(route.getDistance());
                routeResponse.setCreatedAt(route.getCreatedAt());
                routeResponse.setUpdatedAt(route.getUpdatedAt());
                response.setRoute(routeResponse);
            }
        }

        if (tripEntity.getBusId() != null) {
            Bus bus = busMapper.getBusById(tripEntity.getBusId());
            if (bus != null) {
                BusResponse busResponse = new BusResponse();
                busResponse.setId(bus.getId());
                busResponse.setBusNumber(bus.getBusNumber());
                busResponse.setTotalSeats(bus.getTotalSeats());
                busResponse.setImgUrl(bus.getImgUrl());
                busResponse.setDescription(bus.getDescription());
                busResponse.setPricePerKm(bus.getPricePerKm());
                busResponse.setCreatedAt(bus.getCreatedAt());
                busResponse.setUpdatedAt(bus.getUpdatedAt());

                if(bus.getBusType() != null){
                    BusTypeResponse busTypeResponse = new BusTypeResponse();
                    busTypeResponse.setId(bus.getBusType().getId());
                    busTypeResponse.setName(bus.getBusType().getName());
                    busTypeResponse.setSeatPerRow(bus.getBusType().getSeatPerRow());
                    busResponse.setBusType(busTypeResponse);
                }

                response.setBus(busResponse);
            }

        }

        // Set departure / arrival times as strings
        if (tripEntity.getDepartureDate() != null) response.setDepartureTime(tripEntity.getDepartureDate().toLocalTime().toString());
        if (tripEntity.getArrivalDate() != null) response.setArrivalTime(tripEntity.getArrivalDate().toLocalTime().toString());

        return response;
    }


    }




