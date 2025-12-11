package com.xypha.onlineBus.buses.services;

import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@org.springframework.stereotype.Service
public class SvcImpl implements ServiceSvc{

    private final ServiceMapper serviceMapper;

    public SvcImpl(ServiceMapper serviceMapper) {
        this.serviceMapper = serviceMapper;
    }


    public ServiceResponse mapToResponse (Service service){
        ServiceResponse response = new ServiceResponse();
        response.setId(service.getId());
        response.setName(service.getName());
        return response;
    }


    public ApiResponse<ServiceResponse> createService (ServiceRequest request) {
        Service service = new Service();
        service.setName(request.getName());
        service.setCreatedAt(LocalDateTime.now());
        service.setUpdatedAt(LocalDateTime.now());
        serviceMapper.insertService(service);
        return new ApiResponse<>("SUCCESS", "Service created successfully", mapToResponse(service));

    }

    @Override
    public ApiResponse<ServiceResponse> getServiceById(Long id) {
        Service service = serviceMapper.getServiceById(id);
        if (service == null)
            return new ApiResponse<>("NOT_FOUND", "Service not found", null);
        return new ApiResponse<>("SUCCESS", "Service retrieved successfully", mapToResponse(service));
    }

    @Override
    public ApiResponse<ServiceResponse> updateService(Long id, ServiceRequest request) {
        Service service = serviceMapper.getServiceById(id);
        if (service == null)
            return new ApiResponse<>("NOT_FOUND", "Service not found", null);

        service.setName(request.getName());
        service.setUpdatedAt(LocalDateTime.now());
        serviceMapper.updateService(service);
        return new ApiResponse<>("SUCCESS", "Service updated successfully: " +id, mapToResponse(service));
    }


    @Override
    public ApiResponse<Void> deleteService(Long id) {
        serviceMapper.deleteService(id);
        return new ApiResponse<>("SUCCESS", "Service deleted successfully: " + id, null);
    }

    @Override
    public ApiResponse<PaginatedResponse<ServiceResponse>> getAllServices(int page, int size) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        int offset = (page - 1) * size;

        List<Service> services = serviceMapper.getServicesPaginated(size, offset);

        List<ServiceResponse> responses = services.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        int total = serviceMapper.countServices();

        PaginatedResponse<ServiceResponse> paginatedResponse =
                new PaginatedResponse<>(offset, size, total, responses);

        return new ApiResponse<>(
                "SUCCESS",
                "Services retrieved successfully",
                paginatedResponse   // ✅ FIXED HERE
        );
    }

}
