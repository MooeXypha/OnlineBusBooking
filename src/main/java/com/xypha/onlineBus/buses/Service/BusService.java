package com.xypha.onlineBus.buses.Service;

import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.buses.Dto.BusRequest;
import com.xypha.onlineBus.buses.Dto.BusResponse;
import com.xypha.onlineBus.buses.Entity.Bus;
import com.xypha.onlineBus.routes.Dto.RouteResponse;

import java.util.List;

public interface BusService {



    int countBuses();

    //CRUD
    ApiResponse<BusResponse> addBus(BusRequest busRequest);



    ApiResponse<BusResponse> updateBus(Long id, BusRequest busRequest);

    ApiResponse<Void> deleteBus(Long id);

    List<BusResponse> searchBus (String busNumber,
                                 String driverName,
                                 String assistantName,
                                 String driverEmployeeId,
                                 String assistantEmployeeId
                                 );

}
