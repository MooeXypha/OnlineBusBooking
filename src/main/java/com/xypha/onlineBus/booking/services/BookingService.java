package com.xypha.onlineBus.booking.services;

import com.xypha.onlineBus.account.users.mapper.UserMapper;
import com.xypha.onlineBus.api.ApiResponse;
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

    public BookingService(SeatMapper seatMapper, BookingMapper bookingMapper, TripMapper tripMapper, RouteMapper routeMapper, GenerateBookingCode generateBookingCode, UserMapper userMapper, JavaMailSender mailSender, ApplicationEventPublisher eventPublisher) {
        this.seatMapper = seatMapper;
        this.bookingMapper = bookingMapper;
        this.tripMapper = tripMapper;
        this.routeMapper = routeMapper;
        this.generateBookingCode = generateBookingCode;
        this.userMapper = userMapper;
        this.mailSender = mailSender;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ApiResponse<BookingResponse> createBooking (BookingRequest request, Long userId){
        // 1️⃣ Validate seat list
        if (request.getSeatNumbers() == null || request.getSeatNumbers().isEmpty()) {
            return new ApiResponse<>("FAILURE", "No seats selected", null);
        }

        // 2️⃣ Get trip
        Trip trip = tripMapper.getTripFareById(request.getTripId());
        if (trip == null) {
            return new ApiResponse<>("FAILURE", "Trip not found", null);
        }

        // 3️⃣ Get route
        Route route = routeMapper.getRouteById(trip.getRouteId());
        if (route == null) {
            return new ApiResponse<>("FAILURE", "Route not found for the trip", null);
        }

        BigDecimal farePerSeat = BigDecimal.valueOf(trip.getFare());
        BigDecimal totalAmount = farePerSeat.multiply(BigDecimal.valueOf(request.getSeatNumbers().size()));

        // 4️⃣ Lock & book seats
        List<String> bookedSeats = new ArrayList<>();
        List<Long> seatIds = new ArrayList<>();

        for (String seatNo : request.getSeatNumbers()) {
            Seat seat = seatMapper.getSeatByTripAndNo(request.getTripId(), seatNo);
            if (seat == null) return new ApiResponse<>("FAILURE", "Seat not found: " + seatNo, null);
            if (seat.getStatus() == 1) return new ApiResponse<>("FAILURE", "Seat already booked: " + seatNo, null);

            seatMapper.updateSeatStatus(seat.getId(), 1);
            bookedSeats.add(seatNo);
            seatIds.add(seat.getId());
        }

        // 5️⃣ Create booking
        Booking booking = new Booking();
        booking.setBookingCode(GenerateBookingCode.generate());
        booking.setTripId(request.getTripId());
        booking.setUserId(userId);
        booking.setSeatNumbers(request.getSeatNumbers());
        booking.setTotalAmount(totalAmount.doubleValue());
        booking.setStatus("PENDING");
        bookingMapper.createBooking(booking);

        // 6️⃣ Map seats
        for (Long seatId : seatIds) {
            bookingMapper.createBookingSeat(booking.getId(), seatId, trip.getId());
        }

        // 7️⃣ Send email
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
        response.setTripId(booking.getTripId());
        response.setSeatNumbers(bookedSeats);
        response.setTotalAmount(totalAmount.doubleValue());
        response.setCreatedAt(booking.getCreatedAt());
        response.setStatus("PENDING");

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

        return new ApiResponse<>("SUCCESS","Booking retrieved successfully", response);
    }

    @Transactional
    public ApiResponse<Void> confirmPayment (String bookingCode){
        Booking booking = bookingMapper.getByBookingCode(bookingCode);
        if(booking == null)
            return new ApiResponse<>("FAILURE", "Booking not found", null);
        if (!booking.getStatus().equals("PENDING"))
            return new ApiResponse<>("FAILURE", "Booking not in PENDING status", null);

        bookingMapper.updateStatus(bookingCode, "CONFIRMED");
        return new ApiResponse<>("SUCCESS", "Payment confirmed, booking status updated to CONFIRMED", null);

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
        return new ApiResponse<>("SUCCESS", "Booking cancelled successfully", null);
    }








}

