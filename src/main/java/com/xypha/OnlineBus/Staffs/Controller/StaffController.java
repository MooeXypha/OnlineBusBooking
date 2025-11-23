package com.xypha.OnlineBus.Staffs.Controller;


import com.xypha.OnlineBus.Staffs.Assistant.Dto.AssistantRequest;
import com.xypha.OnlineBus.Staffs.Assistant.Dto.AssistantResponse;
import com.xypha.OnlineBus.Staffs.Assistant.Entity.Assistant;
import com.xypha.OnlineBus.Staffs.Driver.Dto.DriverRequest;
import com.xypha.OnlineBus.Staffs.Driver.Dto.DriverResponse;
import com.xypha.OnlineBus.Staffs.Driver.Entity.Driver;
import com.xypha.OnlineBus.Staffs.Service.StaffService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/staff")
@CrossOrigin
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService){
        this.staffService = staffService;
    }

    //Driver
    @PostMapping ("/driver")
    public ResponseEntity<DriverResponse> addDriver(@Valid @RequestBody DriverRequest driverRequest){
        return ResponseEntity.ok(staffService.addDriver(driverRequest));
    }
    @GetMapping ("/driver")
    public ResponseEntity<List<DriverResponse>> getAllDriver(){
        return ResponseEntity.ok(staffService.getAllDriver());
    }
    @GetMapping ("/driver/{id}")
    public ResponseEntity<DriverResponse> getDriverById(@PathVariable Long id){
        return ResponseEntity.ok(staffService.getDriverById(id));
    }
    @GetMapping ("/driver/employee/{employeeId}")
    public ResponseEntity<DriverResponse> getDriverByEmployeeId(@PathVariable String employeeId){
        return ResponseEntity.ok(staffService.getDriverByEmployeeId(employeeId));
    }
    @PutMapping ("/driver/{id}")
    public ResponseEntity<DriverResponse> updateDriver(@PathVariable Long id, @RequestBody DriverRequest request){
        return ResponseEntity.ok(staffService.updateDriver(id,request));
    }
    @DeleteMapping ("/driver/{id}")
    public ResponseEntity<String> deleteDriver(@PathVariable Long id){
        staffService.deleteDriver(id);
        return ResponseEntity.ok("Driver deleted successfull");
    }









    //Assistant
    @PostMapping ("/assistant")
    public ResponseEntity<AssistantResponse> addAssistant(@Valid @RequestBody AssistantRequest assistantRequest){
        return ResponseEntity.ok(staffService.addAssistant(assistantRequest));
    }
    @GetMapping ("/assistant")
    public  ResponseEntity<List<AssistantResponse>> getAllAssistant(){
        return ResponseEntity.ok(staffService.getAllAssistant());
    }
    @GetMapping ("/assistant/{id}")
    public ResponseEntity<AssistantResponse> getAssistantById(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(staffService.getAssistantById(id));
    }
    @GetMapping ("/assistant/employee/{employeeId}")
    public ResponseEntity<AssistantResponse> getAssistantByEmployeeId(
            @PathVariable String employeeId
    ){
        return ResponseEntity.ok(staffService.getAssistantByEmployeeId(employeeId));
    }
    @PutMapping ("/assistant/{id}")
    public ResponseEntity<AssistantResponse> updateAssistant(
            @PathVariable Long id,
            @RequestBody AssistantRequest request
    ){
        return ResponseEntity.ok(staffService.updateAssistant(id, request));
    }
    @DeleteMapping ("/assistant/{id}")
    public ResponseEntity<String> deleteAssistant(
            @PathVariable Long id
    ){
        staffService.deleteAssistant(id);
        return ResponseEntity.ok( "Assistant deleted successfully");
    }

}