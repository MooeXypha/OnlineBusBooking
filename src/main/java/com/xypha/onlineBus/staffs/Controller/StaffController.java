package com.xypha.onlineBus.staffs.Controller;


import com.xypha.onlineBus.account.users.dto.UserResponse;
import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.BranchDto;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.api.service.BranchService;
import com.xypha.onlineBus.staffs.Assistant.Dto.AssistantRequest;
import com.xypha.onlineBus.staffs.Assistant.Dto.AssistantResponse;
import com.xypha.onlineBus.staffs.Driver.Dto.DriverRequest;
import com.xypha.onlineBus.staffs.Driver.Dto.DriverResponse;
import com.xypha.onlineBus.staffs.Service.StaffService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/staff")

public class StaffController {

    private final StaffService staffService;
    private final BranchService branchService;

    public StaffController(StaffService staffService, BranchService branchService){
        this.staffService = staffService;
        this.branchService = branchService;
    }

    //Driver
    @PostMapping ("/driver")
    public ResponseEntity<ApiResponse<DriverResponse>> addDriver(@Valid @RequestBody DriverRequest driverRequest){
        DriverResponse driver = staffService.addDriver(driverRequest);
        ApiResponse<DriverResponse> response = new ApiResponse<>(
                "SUCCESS",
                "Driver added successfully",
                        driver
        );
        return ResponseEntity.ok(response);
    }
    @GetMapping ("/driver")
    public ResponseEntity<ApiResponse<PaginatedResponse<DriverResponse>>> getAllDriver(
            @RequestParam (defaultValue = "0") int offset,
            @RequestParam (defaultValue = "10") int limit
    ){
        PaginatedResponse<DriverResponse> paginatedResponse
                = staffService.getAllDriver(offset, limit);
        ApiResponse<PaginatedResponse<DriverResponse>> response = new ApiResponse<>(
                "SUCCESS",
                "Drivers retrieved successfully",
                paginatedResponse
        );
        return ResponseEntity.ok(response);
    }
    @GetMapping ("/driver/{id}")
    public ResponseEntity<ApiResponse<DriverResponse>> getDriverById(@PathVariable Long id){

        DriverResponse driver = staffService.getDriverById(id);
        ApiResponse<DriverResponse> response = new ApiResponse<>(
                "SUCCESS",
                "Driver retrieved successfully",
                driver
        );
        return ResponseEntity.ok(response);
    }
    @GetMapping ("/driver/employee/{employeeId}")
    public ResponseEntity<ApiResponse<DriverResponse>> getDriverByEmployeeId(@PathVariable String employeeId){
        DriverResponse driver = staffService.getDriverByEmployeeId(employeeId);
        ApiResponse<DriverResponse> response = new ApiResponse<>(
                "SUCCESS",
                "Driver retrieved successfully",
                driver
        );
        return ResponseEntity.ok(response);

    }
    @PutMapping ("/driver/{id}")
    public ResponseEntity<ApiResponse<DriverResponse>> updateDriver(@PathVariable Long id, @Valid @RequestBody DriverRequest request){
        DriverResponse drivers = staffService.updateDriver(id, request);
        ApiResponse<DriverResponse> response = new ApiResponse<>(
                "SUCCESS",
                "Driver updated successfully",
                drivers
        );
        return ResponseEntity.ok(response);
    }
    @DeleteMapping ("/driver/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDriver(@PathVariable Long id){
        staffService.deleteDriver(id);
        ApiResponse<Void> response = new ApiResponse<>(
                "SUCCESS",
                "Driver deleted successfully",
                null
        );
        return ResponseEntity.ok(response);
    }
    @GetMapping("/driver/search")
    public ResponseEntity<ApiResponse<PaginatedResponse<DriverResponse>>> searchDrivers(
            @RequestParam String name,
            @RequestParam (defaultValue = "0") int offset,
            @RequestParam (defaultValue = "10") int limit
    ) {
        PaginatedResponse<DriverResponse> paginatedResponse = staffService.searchDriver(name, offset, limit);
        if (paginatedResponse.getContents().isEmpty()) {
            ApiResponse<PaginatedResponse<DriverResponse>> response = new ApiResponse<>(
                    "NOT_FOUND",
                    "No drivers found with name: " + name,
                    null
            );
            return ResponseEntity.status(404).body(response);
        }
        ApiResponse<PaginatedResponse<DriverResponse>> response = new ApiResponse<>(
                "SUCCESS",
                "Search results for name: " + name,
                paginatedResponse
        );
        return ResponseEntity.ok(response);
    }









    //Assistant
    @PostMapping ("/assistant")
    public ResponseEntity<ApiResponse<AssistantResponse>> addAssistant(@Valid @RequestBody AssistantRequest assistantRequest){
        AssistantResponse assistant = staffService.addAssistant(assistantRequest);
        ApiResponse<AssistantResponse> response = new ApiResponse<>(
                "SUCCESS",
                "Assistant added successfully",
                assistant
        );
        return ResponseEntity.ok(response);
    }
    @GetMapping ("/assistant")
    public  ResponseEntity<ApiResponse<PaginatedResponse<AssistantResponse>>> getAllAssistant(
            @RequestParam (defaultValue = "0") int offset,
            @RequestParam (defaultValue = "10") int limit
    ){
        PaginatedResponse<AssistantResponse> paginatedResponse = staffService.getAllAssistant(offset,limit);
        ApiResponse<PaginatedResponse<AssistantResponse>> response = new ApiResponse<>(
                "SUCCESS",
                "Assistants retrieved successfully",
                paginatedResponse);
            return ResponseEntity.ok(response);
    }

    @GetMapping ("/assistant/{id}")
    public ResponseEntity<ApiResponse<AssistantResponse>> getAssistantById(
            @PathVariable Long id
    ){
        AssistantResponse assistant = staffService.getAssistantById(id);
        ApiResponse<AssistantResponse> response = new ApiResponse<>(
                "SUCCESS",
                "Assistant retrieved successfully",
                assistant
        );
        return ResponseEntity.ok(response);
    }
    @GetMapping ("/assistant/employee/{employeeId}")
    public ResponseEntity<ApiResponse<AssistantResponse>> getAssistantByEmployeeId(
            @PathVariable String employeeId

    ){
        AssistantResponse assistant = staffService.getAssistantByEmployeeId(employeeId);
        if (assistant == null){
            return ResponseEntity.status(404).body(
                    new ApiResponse<>(
                            "NOT_FOUND",
                            "Assistant not found with employee ID: " + employeeId,
                            null
                    )
            );
        }
        ApiResponse<AssistantResponse> response = new ApiResponse<>(
                "SUCCESS",
                "Assistant retrieved successfully",
                assistant
        );
        return ResponseEntity.ok(response);
    }
    @PutMapping ("/assistant/{id}")
    public ResponseEntity<ApiResponse<AssistantResponse>> updateAssistant(
            @PathVariable Long id,
            @RequestBody AssistantRequest request
    ){
        AssistantResponse assistant = staffService.updateAssistant(id, request);
        ApiResponse<AssistantResponse> response = new ApiResponse<>(
                "SUCCESS",
                "Assistant updated successfully",
                assistant
        );
        return ResponseEntity.ok(response);
    }
    @DeleteMapping ("/assistant/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAssistant(
            @PathVariable Long id
    ){
        staffService.deleteAssistant(id);
        ApiResponse<Void> response = new ApiResponse<>(
                "SUCCESS",
                "Assistant deleted successfully :" + id,
                null
        );
        return ResponseEntity.ok( response);
    }

    @GetMapping ("/assistant/search")
    public ResponseEntity<ApiResponse<PaginatedResponse<AssistantResponse>>> searchAssistantByName(
            @RequestParam String name,
            @RequestParam (defaultValue = "0") int offset,
            @RequestParam (defaultValue =  "10") int limit
    ){
        PaginatedResponse<AssistantResponse> paginatedResponse = staffService.searchAssistantByName(name, offset, limit);
        if (paginatedResponse.getContents().isEmpty()){
            ApiResponse<PaginatedResponse<AssistantResponse>> response = new ApiResponse<>(
                    "NOT_FOUND",
                    "No assistant found with that name: " + name,
                    null
            );

            return ResponseEntity.status(404).body(response);
        }
        ApiResponse<PaginatedResponse<AssistantResponse>> response = new ApiResponse<>(
                "SUCCESS",
                "Search results for name: " + name,
                paginatedResponse
        );
        return ResponseEntity.ok(response);
    }


}