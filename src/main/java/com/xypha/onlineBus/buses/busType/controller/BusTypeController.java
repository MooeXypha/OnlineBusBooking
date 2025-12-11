package com.xypha.onlineBus.buses.busType.controller;

import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.buses.busType.dto.BusTypeRequest;
import com.xypha.onlineBus.buses.busType.dto.BusTypeResponse;
import com.xypha.onlineBus.buses.busType.services.BusTypeServiceImpl;
import jakarta.validation.Valid;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("/api/bus/bus-types")
public class BusTypeController {
    private final BusTypeServiceImpl busTypeService;


    public BusTypeController(BusTypeServiceImpl busTypeService) {
        this.busTypeService = busTypeService;
    }

    @PostMapping
    public ApiResponse<BusTypeResponse> createBusType(@Valid @RequestBody BusTypeRequest request) {
        return busTypeService.createBusType(request);
    }

    @GetMapping("/paginated")
    public ApiResponse<PaginatedResponse<BusTypeResponse>> getAllBusTypesPaginated(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return busTypeService.getAllBusTypesPaginated(page, size);
    }

    @GetMapping ("/{id}")
    public ApiResponse<BusTypeResponse> getBusTypeById(@PathVariable Long id) {
        return busTypeService.getBusTypeById(id);
    }

    @PutMapping ("/{id}")
    public ApiResponse<BusTypeResponse> updateBusType(@PathVariable Long id, @RequestBody BusTypeRequest request) {
        return busTypeService.updateBusType(id, request);
    }

    @DeleteMapping ("/{id}")
    public ApiResponse<Void> deleteBusType(@PathVariable Long id) {
        return busTypeService.deleteBusType(id);
    }


}
