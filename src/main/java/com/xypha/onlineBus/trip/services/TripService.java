package com.xypha.onlineBus.trip.services;

import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.trip.dto.TripRequest;
import com.xypha.onlineBus.trip.dto.TripResponse;
import com.xypha.onlineBus.trip.entity.Trip;

import java.time.LocalDate;
import java.util.List;

public interface TripService {

    ApiResponse<TripResponse> createTrip(TripRequest tripRequest);

    ApiResponse<PaginatedResponse<TripResponse>> getAllTrips(int page, int size);

    ApiResponse<TripResponse> getTripById(Long id);

    ApiResponse<TripResponse> updateTrip(Long id, TripRequest tripRequest);

    ApiResponse<Void> deleteTrip(Long id);

    ApiResponse<List<TripResponse>> searchTripByDate(LocalDate departureDate);

    ApiResponse<Integer> countTripsByDepartureDate(LocalDate departureDate);
}
