package com.xypha.onlineBus.monthlypayment.service;

import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.buses.mapper.BusMapper;
import com.xypha.onlineBus.monthlypayment.entity.DailyTripReport;
import com.xypha.onlineBus.monthlypayment.mapper.BookingStatementMapper;
import com.xypha.onlineBus.monthlypayment.dto.DailtBookingStatementResponse;
import com.xypha.onlineBus.monthlypayment.dto.DailySummaryResponse;
import com.xypha.onlineBus.monthlypayment.dto.DailyTripResponse;
import com.xypha.onlineBus.monthlypayment.dto.DailyTripStatementRow;
import com.xypha.onlineBus.routes.mapper.RouteMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class BookingStatementService {

    private final BookingStatementMapper bookingStatementMapper;
    private final BusMapper busMapper;
    private final RouteMapper routeMapper;


    public BookingStatementService(BookingStatementMapper bookingStatementMapper, BusMapper busMapper, RouteMapper routeMapper) {
        this.bookingStatementMapper = bookingStatementMapper;
        this.busMapper = busMapper;
        this.routeMapper = routeMapper;
    }


    public ApiResponse<DailtBookingStatementResponse> getDailyBookingStatement (LocalDate date){
        if (date == null){
            date = LocalDate.now();
        }

        List<DailyTripStatementRow> rows = bookingStatementMapper.getTripStatementsByDate(date);
        DailyTripResponse summaryDto = bookingStatementMapper.getDailySummary(date);

        List<DailyTripResponse> tripResponses = rows.stream()
                .map(this::mapToDailyTripResponse)
                .toList();

        DailySummaryResponse summary = new DailySummaryResponse();
        summary.setTotalTrips(summary.getTotalTrips());
        summary.setTotalSeatsSold(summary.getTotalSeatsSold());
        summary.setTotalRevenue(summary.getTotalRevenue());
        summary.setAverageLoadFactor(summaryDto.getLoadFactor());

        DailtBookingStatementResponse response = new DailtBookingStatementResponse();
        response.setDate(date);
        response.setSummary(summary);
        response.setTrips(tripResponses);

        return new ApiResponse<>("SUCCESS","Daily booking statement retrieved", response);

        }

        public List<DailyTripReport> getBookingReport (LocalDate from, LocalDate to){
            LocalDateTime fromDateTime = from.atStartOfDay();
            LocalDateTime toDateTime = to.atTime(LocalTime.MAX);

            return bookingStatementMapper.getTripsBetweenDates (fromDateTime,toDateTime);
        }


        private DailyTripResponse mapToDailyTripResponse (DailyTripStatementRow row){
        DailyTripResponse res = new DailyTripResponse();

        res.setTripId(row.getTripId());
        res.setSeatsSold(row.getSeatSold());
        res.setTotalSeats(row.getTotalSeats());
        res.setLoadFactor(row.getLoadFactor());
        res.setRevenue(row.getTripRevenue());

        //related name
            res.setBusNumber(busMapper.getBusNumberById(row.getBusId()));
            res.setRoute(routeMapper.getRouteNameById(row.getRouteId()));
            return res;
        }




    }





