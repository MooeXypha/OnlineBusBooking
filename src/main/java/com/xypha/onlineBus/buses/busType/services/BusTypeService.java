package com.xypha.onlineBus.buses.busType.services;

import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.buses.busType.dto.BusTypeRequest;
import com.xypha.onlineBus.buses.busType.dto.BusTypeResponse;

import java.util.List;

public interface BusTypeService {

    ApiResponse<BusTypeResponse> createBusType(BusTypeRequest request);

    ApiResponse<BusTypeResponse> getBusTypeById(Long id);

    ApiResponse<BusTypeResponse> updateBusType(Long id, BusTypeRequest request);


    ApiResponse<PaginatedResponse<BusTypeResponse>> getAllBusTypesPaginated(int page, int size);

    ApiResponse<Void> deleteBusType(Long id);
}
