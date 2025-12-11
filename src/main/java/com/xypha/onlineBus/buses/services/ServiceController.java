package com.xypha.onlineBus.buses.services;


import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bus/services")
public class ServiceController {

    @Autowired
    private SvcImpl service;


    @PostMapping
    public ApiResponse<ServiceResponse> createService(
            @Valid @RequestBody ServiceRequest request) {
        return service.createService(request);
    }

    @GetMapping("/paginated")
    public ApiResponse<PaginatedResponse<ServiceResponse>>getAllServicesPaginated(
            @RequestParam (defaultValue = "1") int page,
            @RequestParam (defaultValue = "10") int size
    ){
        return service.getAllServices(page, size);
    }

    @PutMapping ("/{id}")
    public ApiResponse<ServiceResponse> updateService(
            @PathVariable Long id,
            @Valid @RequestBody ServiceRequest request){
        return service.updateService(id, request);
    }

    @GetMapping("/{id}")
    public ApiResponse<ServiceResponse> getServiceById(
            @PathVariable Long id){
        return service.getServiceById(id);
    }
    @DeleteMapping ("/{id}")
    public ApiResponse<Void> deleteService(
            @PathVariable Long id){
        return service.deleteService(id);
    }


}
