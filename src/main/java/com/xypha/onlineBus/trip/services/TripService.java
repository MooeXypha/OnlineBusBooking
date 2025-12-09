package com.xypha.onlineBus.trip.services;

import com.xypha.onlineBus.trip.dto.TripResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


public interface TripService {
    TripResponse createTrip(TripResponse request);

    TripResponse getTripById(Long id);

    TripResponse updateTrip(Long id, TripResponse request);

    List<TripResponse> getAllTrips();

    void deleteTrip(Long id);

}
