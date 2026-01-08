package com.xypha.onlineBus.booking.controller;

import com.xypha.onlineBus.account.users.entity.User;
import com.xypha.onlineBus.account.users.service.CustomUserDetails;
import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.booking.dto.BookingRequest;
import com.xypha.onlineBus.booking.dto.BookingResponse;
import com.xypha.onlineBus.booking.dto.UpdateBookingStatusRequest;
import com.xypha.onlineBus.booking.entity.Booking;
import com.xypha.onlineBus.booking.services.BookingService;
import com.xypha.onlineBus.buses.seat.mapper.SeatMapper;
import com.xypha.onlineBus.error.BadRequestException;
import com.xypha.onlineBus.error.ResourceNotFoundException;
import com.xypha.onlineBus.trip.mapper.TripMapper;
import jakarta.validation.Valid;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.nio.file.attribute.UserPrincipal;
import java.util.Map;
import java.util.Objects;

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
        if(user == null) {
            throw new BadRequestException("You must be logged in first");
        }
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


    @PutMapping("/{bookingCode}/status")
    public ApiResponse<BookingResponse> updateBookingStatus(
            @PathVariable String bookingCode,
            @RequestBody Map<String, String>body
            ) {
        String status = body.get("status");
    return bookingService.updateBookingStatus(bookingCode,status);

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
        if (userDetails == null){
            throw new BadRequestException("You must be logged in first");
        }
        return bookingService.getUserBookingHistory(
                userDetails.getId(),
                status,
                offset,
                limit
        );
    }

    @GetMapping("/count/today")
    public ApiResponse<Map<String,Integer>> getTodayBookingCounts(){
        Map<String, Integer> counts = bookingService.getTodayBookingCounts();
        if (counts == null){
            throw new ResourceNotFoundException("There is no booking for today");
        }
        return new ApiResponse<>("SUCCESS","Booking counts retrieved for today", counts);
    }

    @GetMapping("/today/cash-in")
    public ApiResponse<Double> getTodayCashIn(){
        Double total = bookingService.getTodayTotalCashIn();
        return new ApiResponse<>("SUCCESS", "Today's total cash-in: ", total);
    }













}
