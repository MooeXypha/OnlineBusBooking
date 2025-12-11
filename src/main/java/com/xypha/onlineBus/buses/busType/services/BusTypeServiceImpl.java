package com.xypha.onlineBus.buses.busType.services;

import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.buses.busType.dto.BusTypeRequest;
import com.xypha.onlineBus.buses.busType.dto.BusTypeResponse;
import com.xypha.onlineBus.buses.busType.entity.BusType;
import com.xypha.onlineBus.buses.busType.mapper.BusTypeMapper;
import com.xypha.onlineBus.buses.services.Service;
import com.xypha.onlineBus.buses.services.ServiceMapper;
import com.xypha.onlineBus.buses.services.ServiceResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class BusTypeServiceImpl implements BusTypeService {

    private final BusTypeMapper busTypeMapper;
    private final ServiceMapper serviceMapper;

    public BusTypeServiceImpl(BusTypeMapper busTypeMapper, ServiceMapper serviceMapper) {
        this.busTypeMapper = busTypeMapper;
        this.serviceMapper = serviceMapper;
    }

    public BusTypeResponse mapToResponse(BusType busType) {
        BusTypeResponse response = new BusTypeResponse();
        response.setId(busType.getId());
        response.setName(busType.getName());


        List<Service> services = busTypeMapper.findServicesByBusTypeId(busType.getId());
        List<ServiceResponse> serviceResponses = services.stream()
                .map(s -> {
                    ServiceResponse sr = new ServiceResponse();
                    sr.setId(s.getId());
                    sr.setName(s.getName());
                    return sr;
                }).collect(Collectors.toList());

        response.setServices(serviceResponses);

        return response;
    }


    @Override
    public ApiResponse<BusTypeResponse> createBusType(BusTypeRequest request) {

        // Ensure services exist in DB
        if (serviceMapper.countServices() == 0) {
            return new ApiResponse<>("FAILURE", "Cannot create bus type without existing services. Please create services first.", null);
        }

        // Validate request list
        if (request.getServiceIds() == null || request.getServiceIds().size() < 2) {
            return new ApiResponse<>("FAILURE", "At least two services must be associated with a bus type.", null);
        }

        // Check if all IDs exist
        int validCount = serviceMapper.countExistingServices(request.getServiceIds());
        if (validCount != request.getServiceIds().size()) {
            return new ApiResponse<>("FAILURE", "Some services IDs do not exist.", null);
        }

        // Create bus type
        BusType busType = new BusType();
        busType.setName(request.getName());
        busType.setCreatedAt(LocalDateTime.now());
        busType.setUpdatedAt(LocalDateTime.now());
        busTypeMapper.insertBusType(busType);

        // Map services
        busTypeMapper.addServicesToBusType(busType.getId(), request.getServiceIds());

        return new ApiResponse<>("SUCCESS", "Bus type created successfully", mapToResponse(busType));
    }


    @Override
    public ApiResponse<BusTypeResponse> getBusTypeById(Long id) {
        BusType busType = busTypeMapper.getBusTypeById(id);
        if (busType == null) {
            return new ApiResponse<>("NOT_FOUND", "Bus type not found", null);
        }
        return new ApiResponse<>("SUCCESS", "Bus type retrieved successfully", mapToResponse(busType));
    }

    @Override
    public ApiResponse<BusTypeResponse> updateBusType(Long id, BusTypeRequest request) {
        BusType busType = busTypeMapper.getBusTypeById(id);
        if (busType == null) {
            return new ApiResponse<>("NOT_FOUND", "Bus type not found", null);
        }
        if (serviceMapper.countServices() == 0){
            return new ApiResponse<>("FAILURE", "Cannot update bus type without existing services. Please create services first.", null);
        }
        if (request.getServiceIds() == null || request.getServiceIds().isEmpty()){
            return new ApiResponse<>("FAILURE", "At least two services must be associated with a bus type.", null);
        }
        if (request.getServiceIds().size() < 2){
            return new ApiResponse<>("FAILURE", "A bus type must include at least Two services.", null);
        }

        int validCount = serviceMapper.countExistingServices(request.getServiceIds());
        if (validCount != request.getServiceIds().size()){
            return new ApiResponse<>("FAILURE", "Some services IDs do not exist.", null);
        }

        busType.setName(request.getName());
        busType.setUpdatedAt(LocalDateTime.now());
        busTypeMapper.updateBusType(busType);

        //Update services
        busTypeMapper.removeServicesFromBusType(id);
        if (request.getServiceIds() != null && !request.getServiceIds().isEmpty()){
            busTypeMapper.addServicesToBusType(id, request.getServiceIds());
        }
        return new ApiResponse<>("SUCCESS", "Bus type updated successfully", mapToResponse(busType));
    }




//    @Override
//    public ApiResponse<PaginatedResponse<BusTypeResponse>> getAllBusTypesPaginated(int page, int size) {
//        if (page < 1) page = 1;
//        if (size < 1) size = 10;
//        int offset = (page - 1) * size;
//
//        List<BusType> busTypes = busTypeMapper.getAllBusTypesPaginated(offset, size);
//        List<BusTypeResponse> responses = busTypes.stream()
//                .map(this::mapToResponse)
//                .collect(Collectors.toList());
//
//        int total = busTypeMapper.countBusTypes();
//
//        PaginatedResponse<BusTypeResponse> paginatedResponse =
//                new PaginatedResponse<>(offset, size, total, responses);
//
//        return new ApiResponse<>("SUCCESS", "Bus types retrieved successfully", paginatedResponse);
//    }
@Override
public ApiResponse<PaginatedResponse<BusTypeResponse>> getAllBusTypesPaginated(int page, int size) {
    if (page < 1) page = 1;
    if (size < 1) size = 10;
    int offset = (page - 1) * size;

    // Fetch bus types from DB
    List<BusType> busTypes = busTypeMapper.getAllBusTypesPaginated(offset, size);
    for (BusType bt : busTypes) {
        System.out.println("DEBUG: BusType ID: " + bt.getId() + ", Name: " + bt.getName());
    }

    // Map to response
    List<BusTypeResponse> responses = busTypes.stream()
            .map(bt -> {
                BusTypeResponse response = mapToResponse(bt);
                System.out.println("DEBUG: BusTypeResponse ID: " + response.getId() +
                        ", Services count: " + (response.getServices() != null ? response.getServices().size() : 0));
                return response;
            })
            .collect(Collectors.toList());

    int total = busTypeMapper.countBusTypes();
    PaginatedResponse<BusTypeResponse> paginatedResponse =
            new PaginatedResponse<>(offset, size, total, responses);

    return new ApiResponse<>("SUCCESS", "Bus types retrieved successfully", paginatedResponse);
}


    @Override
    public ApiResponse<Void> deleteBusType(Long id) {
        busTypeMapper.removeServicesFromBusType(id);
        busTypeMapper.deleteBusType(id);
        return new ApiResponse<>("SUCCESS", "Bus type deleted successfully", null);
    }
}
