package com.xypha.onlineBus.booking.controller;

import com.xypha.onlineBus.account.users.service.CustomUserDetails;
import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.booking.dto.BookingRequest;
import com.xypha.onlineBus.booking.dto.BookingResponse;
import com.xypha.onlineBus.booking.services.BookingService;
import com.xypha.onlineBus.buses.seat.mapper.SeatMapper;
import com.xypha.onlineBus.trip.mapper.TripMapper;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final SeatMapper seatMapper;
    private final TripMapper tripMapper;

    public BookingController(BookingService bookingService, SeatMapper seatMapper, TripMapper tripMapper) {
        this.bookingService = bookingService;
        this.seatMapper = seatMapper;
        this.tripMapper = tripMapper;
    }

    @PostMapping
    public ApiResponse<BookingResponse> createBooking (
            @Valid @RequestBody BookingRequest request,
            @AuthenticationPrincipal CustomUserDetails user){
        if(user == null)
            return new ApiResponse<>("FAILURE", "You must be logged in first", null);

        return bookingService.createBooking(request, user.getId());
    }

    @GetMapping("/{bookingCode}")
    public ApiResponse<BookingResponse> searchByBookingCode(
            @PathVariable String bookingCode
    ){
        return bookingService.getBookingByCode(bookingCode);
    }

    @PostMapping("/{bookingCode}/confirm")
    public ApiResponse<Void> confirmPayment(
            @PathVariable String bookingCode
    ){
        return bookingService.confirmPayment (bookingCode);
    }

    @DeleteMapping ("/{bookingCode}/cancel")
    public ApiResponse<Void> cancelBooking(
            @PathVariable String bookingCode
    ){
        return bookingService.cancelBooking (bookingCode);
    }

    @GetMapping("/paginated")
    public ApiResponse<PaginatedResponse<BookingResponse>> getAllBookingPaginated(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ){
        return bookingService.getAllBookingPaginated(status, offset, limit);
    }

    //UserBookingHistory
    @GetMapping ("/history")
    public ApiResponse<PaginatedResponse<BookingResponse>>getUserBookingHistory(
            @RequestParam (defaultValue = "0") int offset,
            @RequestParam (defaultValue = "10") int limit,
            @RequestParam (required = false) String status,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        return bookingService.getUserBookingHistory(
                userDetails.getId(),
                status,
                offset,
                limit
        );
    }



}
