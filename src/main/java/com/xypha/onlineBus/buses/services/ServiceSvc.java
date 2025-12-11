package com.xypha.onlineBus.buses.services;

import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ServiceSvc {

    ApiResponse<ServiceResponse> createService(ServiceRequest request);

    ApiResponse<ServiceResponse> getServiceById(Long id);

    ApiResponse<ServiceResponse> updateService(Long id, ServiceRequest request);

    ApiResponse<PaginatedResponse<ServiceResponse>> getAllServices(int page, int size);

    ApiResponse<Void> deleteService(Long id);
}
