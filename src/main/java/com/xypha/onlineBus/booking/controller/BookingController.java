package com.xypha.onlineBus.booking.controller;

import com.xypha.onlineBus.account.users.service.CustomUserDetails;
import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.booking.dto.BookingRequest;
import com.xypha.onlineBus.booking.dto.BookingResponse;
import com.xypha.onlineBus.booking.entity.Booking;
import com.xypha.onlineBus.booking.services.BookingService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ApiResponse<BookingResponse> createBooking (
            @Valid @RequestBody BookingRequest request,
            Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return bookingService.createBooking(request, userDetails.getId());
    }

    @GetMapping("/{bookingCode}")
    public ApiResponse<BookingResponse> getByBookingCode (
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


}
