package com.xypha.onlineBus.monthlypayment.controller;

import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.booking.dto.DailyRevenue;
import com.xypha.onlineBus.booking.dto.TopRoute;
import com.xypha.onlineBus.booking.services.BookingService;
import com.xypha.onlineBus.error.BadRequestException;
import com.xypha.onlineBus.error.ResourceNotFoundException;
import com.xypha.onlineBus.monthlypayment.entity.DailyTripReport;
import com.xypha.onlineBus.monthlypayment.service.BookingStatementService;
import com.xypha.onlineBus.monthlypayment.dto.DailtBookingStatementResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class AdminController {
    private final BookingService bookingService;
    private final BookingStatementService bookingStatementService;

    public AdminController(BookingService bookingService, BookingStatementService bookingStatementService) {
        this.bookingService = bookingService;
        this.bookingStatementService = bookingStatementService;
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

    @GetMapping("/revenue-trend")
    public ApiResponse<List<DailyRevenue>> getRevenueTrend(){
        return bookingService.getRevenueTrendLast7Days();
    }

    @GetMapping("/top-routes")
    public ApiResponse<List<TopRoute>> topRoutes(){
        List<TopRoute> topRoutes = bookingService.getTopRoute();
        return new ApiResponse<>("SUCCESS","Top 3 performing routes for last 7 days retrieved", topRoutes);
    }


    @GetMapping("/reports/daily-booking")
    public ApiResponse<DailtBookingStatementResponse> getDailyBooking(
            @RequestParam(required = false)LocalDate date
            ){
        return bookingStatementService.getDailyBookingStatement(date);
    }


    @GetMapping("/reports")
    public ApiResponse<List<DailyTripReport>> getBookingStatement(
            @RequestParam(value = "from", required = false)LocalDate from,
            @RequestParam(value = "to", required = false) LocalDate to
    ) {
        if (from == null || to == null)
            throw new BadRequestException("Both start date and end date must be provided");
        if (from.isAfter(to))
            throw new BadRequestException("Start date cannot be after end date");

        try {
            List<DailyTripReport> report = bookingStatementService.getBookingReport(from, to);
            return new ApiResponse<>("SUCCESS", "Booking report retrieved successfully", report);
        } catch (Exception e) {
            e.printStackTrace(); // see the actual exception in console
            return new ApiResponse<>("FAILURE", "Internal Server error occurred", null);
        }
    }



}
