package com.xypha.onlineBus.buses.service;

import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.buses.Dto.BusRequest;
import com.xypha.onlineBus.buses.Dto.BusResponse;

public interface BusService {

    // Count all buses
    int countBuses();

    // CRUD
    ApiResponse<BusResponse> addBus(BusRequest busRequest);

    ApiResponse<BusResponse> getBusResponseById(Long id);

    ApiResponse<BusResponse> updateBus(Long id, BusRequest busRequest);

    ApiResponse<Void> deleteBus(Long id);

    // Pagination
    ApiResponse<PaginatedResponse<BusResponse>> getBusesPaginatedResponse(int offset, int limit);
}
