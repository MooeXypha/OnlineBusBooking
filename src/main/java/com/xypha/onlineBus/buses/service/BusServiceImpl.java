package com.xypha.onlineBus.buses.service;

import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.buses.Dto.BusRequest;
import com.xypha.onlineBus.buses.Dto.BusResponse;
import com.xypha.onlineBus.buses.Entity.Bus;

import com.xypha.onlineBus.buses.busType.dto.BusTypeResponse;
import com.xypha.onlineBus.buses.busType.entity.BusType;
import com.xypha.onlineBus.buses.mapper.BusMapper;
import com.xypha.onlineBus.buses.services.ServiceResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusServiceImpl implements BusService {

    private final BusMapper busMapper;

    public BusServiceImpl(BusMapper busMapper) {
        this.busMapper = busMapper;
    }

    @Override
    public ApiResponse<PaginatedResponse<BusResponse>> getBusesPaginatedResponse(int offset, int limit) {
        int offsets = offset * limit;
        List<Bus> buses = busMapper.findPaginated(offsets, limit);
        List<BusResponse> responseList = buses.stream().map(this::mapToResponse).collect(Collectors.toList());
        int total = busMapper.countBuses();
        PaginatedResponse<BusResponse> paginatedResponse = new PaginatedResponse<>(offsets, limit, total, responseList);
        return new ApiResponse<>("SUCCESS", "Buses retrieved successfully", paginatedResponse);
    }

    @Override
    public int countBuses() {
        return busMapper.countBuses();
    }

    @Override
    public ApiResponse<BusResponse> addBus(BusRequest busRequest) {
        if (busMapper.existsByBusNumber(busRequest.getBusNumber()) > 0)
            throw new RuntimeException("Bus number already exists");
        if (!busRequest.getBusNumber().matches("^(?!\\d{2})[A-Z\\d]{1,2}/\\d{4}$")) {
            throw new RuntimeException(
                    "Invalid Myanmar bus number format. Example: 5I/2108 or AB/1234"
            );
        }

        Bus bus = new Bus();
        bus.setBusNumber(busRequest.getBusNumber());
        BusType busType = new BusType();
        busType.setId(busRequest.getBusTypeId());
        bus.setBusType(busType);
        bus.setTotalSeats(busRequest.getTotalSeats());
        bus.setImgUrl(busRequest.getImgUrl());
        bus.setDescription(busRequest.getDescription());
        bus.setPricePerKm(busRequest.getPricePerKm());
        bus.setCreatedAt(LocalDateTime.now());
        bus.setUpdatedAt(LocalDateTime.now());

        busMapper.insertBus(bus);
        Bus inserted = busMapper.getBusById(bus.getId());
        return new ApiResponse<>("SUCCESS", "Bus added successfully", mapToResponse(inserted));
    }

    @Override
    public ApiResponse<BusResponse> getBusResponseById(Long id) {
        Bus bus = busMapper.getBusById(id);
        if (bus == null) throw new RuntimeException("Bus not found with id: " + id);
        return new ApiResponse<>("SUCCESS", "Bus retrieved successfully", mapToResponse(bus));
    }

    @Override
    public ApiResponse<BusResponse> updateBus(Long id, BusRequest busRequest) {
        Bus bus = busMapper.getBusById(id);
        if (bus == null) throw new RuntimeException("Bus not found");

        bus.setBusNumber(busRequest.getBusNumber());
        BusType busType = new BusType();
        busType.setId(busRequest.getBusTypeId());
        bus.setBusType(busType);
        bus.setTotalSeats(busRequest.getTotalSeats());
        bus.setImgUrl(busRequest.getImgUrl());
        bus.setDescription(busRequest.getDescription());
        bus.setPricePerKm(busRequest.getPricePerKm());
        bus.setUpdatedAt(LocalDateTime.now());

        busMapper.updateBus(bus);
        Bus updated = busMapper.getBusById(id);
        return new ApiResponse<>("SUCCESS", "Bus updated successfully", mapToResponse(updated));
    }

    @Override
    public ApiResponse<Void> deleteBus(Long id) {
        busMapper.deleteBus(id);
        return new ApiResponse<>("SUCCESS", "Bus deleted successfully", null);
    }

    // Map Bus entity → BusResponse
    private BusResponse mapToResponse(Bus bus) {
        BusResponse res = new BusResponse();
        res.setId(bus.getId());
        res.setBusNumber(bus.getBusNumber());
        res.setTotalSeats(bus.getTotalSeats());
        res.setImgUrl(bus.getImgUrl());
        res.setDescription(bus.getDescription());
        res.setPricePerKm(bus.getPricePerKm());
        res.setCreatedAt(bus.getCreatedAt());
        res.setUpdatedAt(bus.getUpdatedAt());

        if (bus.getBusType() != null) {
            BusType busType = bus.getBusType();
            BusTypeResponse busTypeResponse = new BusTypeResponse();
            busTypeResponse.setId(busType.getId());
            busTypeResponse.setName(busType.getName());

            // Fetch services from DB
            List<ServiceResponse> services = busMapper.getServicesByBusTypeId(busType.getId())
                    .stream()
                    .map(s -> {
                        ServiceResponse sr = new ServiceResponse();
                        sr.setId(s.getId());
                        sr.setName(s.getName());

                        return sr;
                    }).collect(Collectors.toList());

            busTypeResponse.setServices(services);

            res.setBusType(busTypeResponse);
        }

        return res;
    }

}
