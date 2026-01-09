package com.xypha.onlineBus.monthlypayment.dto;

public class DailySummaryResponse {

    private int totalTrips;
    private int totalSeatsSold;
    private double totalRevenue;
    private double averageLoadFactor;

    public int getTotalTrips() {
        return totalTrips;
    }

    public void setTotalTrips(int totalTrips) {
        this.totalTrips = totalTrips;
    }

    public int getTotalSeatsSold() {
        return totalSeatsSold;
    }

    public void setTotalSeatsSold(int totalSeatsSold) {
        this.totalSeatsSold = totalSeatsSold;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public double getAverageLoadFactor() {
        return averageLoadFactor;
    }

    public void setAverageLoadFactor(double averageLoadFactor) {
        this.averageLoadFactor = averageLoadFactor;
    }
}
