package com.xypha.onlineBus.monthlypayment.dto;

import java.time.LocalDate;
import java.util.List;

public class DailtBookingStatementResponse {

    private LocalDate date;
    private DailySummaryResponse summary;
    private List<DailyTripResponse> trips;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public DailySummaryResponse getSummary() {
        return summary;
    }

    public void setSummary(DailySummaryResponse summary) {
        this.summary = summary;
    }

    public List<DailyTripResponse> getTrips() {
        return trips;
    }

    public void setTrips(List<DailyTripResponse> trips) {
        this.trips = trips;
    }
}
