package com.xypha.onlineBus.booking.services;

import com.xypha.onlineBus.account.users.mapper.UserMapper;
import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.booking.dto.BookingRequest;
import com.xypha.onlineBus.booking.dto.BookingResponse;
import com.xypha.onlineBus.booking.entity.Booking;
import com.xypha.onlineBus.booking.mapper.BookingMapper;
import com.xypha.onlineBus.buses.seat.entity.Seat;
import com.xypha.onlineBus.buses.seat.mapper.SeatMapper;
import com.xypha.onlineBus.routes.Entity.Route;
import com.xypha.onlineBus.routes.Mapper.RouteMapper;
import com.xypha.onlineBus.trip.entity.Trip;
import com.xypha.onlineBus.trip.mapper.TripMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ApplicationEventPublisher eventPublisher;
    private final BookingEmailService bookingEmailService;

    public BookingService(SeatMapper seatMapper, BookingMapper bookingMapper, TripMapper tripMapper, RouteMapper routeMapper, GenerateBookingCode generateBookingCode, UserMapper userMapper, JavaMailSender mailSender, ApplicationEventPublisher eventPublisher, BookingEmailService bookingEmailService) {
        this.seatMapper = seatMapper;
        this.bookingMapper = bookingMapper;
        this.tripMapper = tripMapper;
        this.routeMapper = routeMapper;
        this.generateBookingCode = generateBookingCode;
        this.userMapper = userMapper;
        this.mailSender = mailSender;
        this.eventPublisher = eventPublisher;
        this.bookingEmailService = bookingEmailService;
    }

    @Transactional
    public ApiResponse<BookingResponse> createBooking(BookingRequest request, Long userId) {

        // 1️⃣ Basic validations
        if (request.getSeatNumbers() == null || request.getSeatNumbers().isEmpty()) {
            return new ApiResponse<>("FAILURE", "No seats selected", null);
        }

        if (request.getSeatNumbers().size() > 5) {
            return new ApiResponse<>("FAILURE", "Maximum 5 seats allowed per booking", null);
        }

        // 2️⃣ Get trip
        Trip trip = tripMapper.getTripFareById(request.getTripId());
        if (trip == null) {
            return new ApiResponse<>("FAILURE", "Trip not found", null);
        }

        if (trip.getDepartureDate().isBefore(LocalDateTime.now())) {
            return new ApiResponse<>("FAILURE", "Trip already departed", null);
        }

        if (trip.getDepartureDate().minusMinutes(30).isBefore(LocalDateTime.now())) {
            return new ApiResponse<>("FAILURE", "Booking closed for this trip", null);
        }

        // 3️⃣ Get route
        Route route = routeMapper.getRouteById(trip.getRouteId());
        if (route == null) {
            return new ApiResponse<>("FAILURE", "Route not found for the trip", null);
        }

        // 4️⃣ Calculate total
        BigDecimal totalAmount =
                BigDecimal.valueOf(trip.getFare())
                        .multiply(BigDecimal.valueOf(request.getSeatNumbers().size()));

        // 5️⃣ Create booking FIRST
        Booking booking = new Booking();
        booking.setBookingCode(GenerateBookingCode.generate());
        booking.setTripId(trip.getId());
        booking.setUserId(userId);
        booking.setTotalAmount(totalAmount.doubleValue());
        booking.setStatus("PENDING");
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        booking.setUserName(userMapper.getNameById(userId)); // populate user name
        booking.setDepartureDate(trip.getDepartureDate());
        booking.setArrivalDate(trip.getArrivalDate());
        booking.setRouteSource(route.getSource());
        booking.setRouteDestination(route.getDestination());

        bookingMapper.createBooking(booking);

        // 6️⃣ Lock & update seats
        List<String> bookedSeats = new ArrayList<>();

        for (String seatNo : request.getSeatNumbers()) {

            Seat seat = seatMapper.lockSeatForUpdate(trip.getId(), seatNo);

            if (seat == null) {
                throw new RuntimeException("Seat not found: " + seatNo);
            }

            if (seat.getStatus() != 0) {
                throw new RuntimeException("Seat not available: " + seatNo);
            }

            seatMapper.updateSeatStatus(seat.getId(), 1);
            bookingMapper.createBookingSeat(booking.getId(), seat.getId(), trip.getId());

            bookedSeats.add(seatNo);
        }

        // 7️⃣ Send email (after commit via event)
        String userEmail = userMapper.getEmailById(userId);
        if (userEmail != null && !userEmail.isEmpty()) {
            eventPublisher.publishEvent(
                    new BookingCreatedEvent(
                            userEmail,
                            booking.getBookingCode(),
                            totalAmount.doubleValue(),
                            bookedSeats,
                            route.getSource(),
                            route.getDestination(),
                            trip.getDepartureDate()
                    )
            );
        }

        // 8️⃣ Response
        BookingResponse response = new BookingResponse();
        response.setBookingCode(booking.getBookingCode());
        response.setTripId(trip.getId());
        response.setSeatNumbers(bookedSeats);
        response.setTotalAmount(totalAmount.doubleValue());
        response.setStatus("PENDING");
        response.setCreatedAt(booking.getCreatedAt());
        response.setUpdatedAt(booking.getUpdatedAt());
        response.setUserId(booking.getUserId());
        response.setUserName(booking.getUserName());
        response.setRouteSource(booking.getRouteSource());
        response.setRouteDestination(booking.getRouteDestination());
        response.setDepartureDate(booking.getDepartureDate());
        response.setArrivalDate(booking.getArrivalDate());

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
        response.setTripId(booking.getTripId());
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
            response.setTripId(booking.getTripId());
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




}

