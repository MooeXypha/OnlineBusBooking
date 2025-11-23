package com.xypha.OnlineBus.Staffs.Service;

import com.xypha.OnlineBus.Staffs.Assistant.Dto.AssistantRequest;
import com.xypha.OnlineBus.Staffs.Assistant.Dto.AssistantResponse;
import com.xypha.OnlineBus.Staffs.Assistant.Entity.Assistant;
import com.xypha.OnlineBus.Staffs.Assistant.Mapper.AssistantMapper;
import com.xypha.OnlineBus.Staffs.Driver.Dto.DriverRequest;
import com.xypha.OnlineBus.Staffs.Driver.Dto.DriverResponse;
import com.xypha.OnlineBus.Staffs.Driver.Entity.Driver;
import com.xypha.OnlineBus.Staffs.Driver.Mapper.DriverMapper;

import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class StaffService {
    private final DriverMapper driverMapper;

    private final AssistantMapper assistantMapper;

    public StaffService (DriverMapper driverMapper, AssistantMapper assistantMapper){
        this.driverMapper = driverMapper;
        this.assistantMapper = assistantMapper;
    }

    //DRIVER CRUD
    public DriverResponse addDriver(DriverRequest request){
        Driver driver = mapDriverToEntity(request);
        driverMapper.insertDriver(driver);
        return mapDriverToResponse(driver);
    }
    public DriverResponse getDriverById(Long id){
        Driver driver = driverMapper.getDriverById(id);
        if (driver == null)
            throw new RuntimeException("Driver not found with id: " + id);
        return mapDriverToResponse(driver);
    }
    public List<DriverResponse> getAllDriver(){
        return driverMapper.getALlDriver().stream()
                .map(this::mapDriverToResponse)
                .toList();
    }
    public DriverResponse getDriverByEmployeeId(String employeeId) {
        Driver driver = driverMapper.getDriverByEmployeeId(employeeId);
        if (driver == null)
            throw new RuntimeException("Driver not found with employeeId: " + employeeId);
        return mapDriverToResponse(driver);
    }
    public DriverResponse updateDriver(Long id, DriverRequest request){
        Driver driver = driverMapper.getDriverById(id);
        if (driver == null)
            throw new RuntimeException("Driver not found with id: " + id);
        driver.setName(request.getName());
        driver.setPhoneNumber(request.getPhoneNumber());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setEmployeeId(request.getEmployeeId());
        driverMapper.updateDriver(driver);
        return mapDriverToResponse(driver);
    }
    public void deleteDriver(Long id){
        driverMapper.deleteDriver(id);
    }




    //Assistant CRUD
    public AssistantResponse addAssistant(AssistantRequest assistantRequest){
        Assistant assistant = mapAssistantToEntity(assistantRequest);
        assistant.setName(assistantRequest.getName());
        assistant.setPhoneNumber(assistantRequest.getPhoneNumber());
        assistant.setEmployeeId(assistantRequest.getEmployeeId());
        assistantMapper.insertAssistant(assistant);
        return mapAssistantToResponse(assistant);
    }
    public AssistantResponse getAssistantById(Long id){
        Assistant assistant = assistantMapper.getAssistantById(id);
        if (assistant == null)
            throw new RuntimeException("Assistant not found with id: " + id);
        return mapAssistantToResponse(assistant);
    }
    public List<AssistantResponse> getAllAssistant(){
        return assistantMapper.getAllAssistant().stream()
                .map(this::mapAssistantToResponse)
                .toList();
    }
    public AssistantResponse getAssistantByEmployeeId(String employeeId){
        Assistant assistant = assistantMapper.getAssistantByEmployeeId(employeeId);
        return mapAssistantToResponse(assistant);
    }
    public AssistantResponse updateAssistant(Long id, AssistantRequest request){
        Assistant assistant = assistantMapper.getAssistantById(id);
        if (assistant == null)
            throw new RuntimeException("Assistant not found with id: " + id);
        assistant.setName(request.getName());
        assistant.setPhoneNumber(request.getPhoneNumber());
        assistant.setEmployeeId(request.getEmployeeId());
        assistantMapper.updateAssistant(assistant);
        return mapAssistantToResponse(assistant);
    }
    public void deleteAssistant(Long id){
        assistantMapper.deleteAssistant(id);

    }

    //Map DriverRequest To Driver Entity
    public Driver mapDriverToEntity (DriverRequest request){
        Driver driver = new Driver();
        driver.setName(request.getName());
        driver.setPhoneNumber(request.getPhoneNumber());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setEmployeeId(request.getEmployeeId());
        return driver;
    }

    //Map Driver Entity to DriverResponse DTO
    public DriverResponse mapDriverToResponse(Driver driver){
        DriverResponse response = new DriverResponse();
        response.setId(driver.getId());
        response.setName(driver.getName());
        response.setPhoneNumber(driver.getPhoneNumber());
        response.setEmployeeId(driver.getEmployeeId());
        response.setLicenseNumber(driver.getLicenseNumber());
        return response;
    }

    public Assistant mapAssistantToEntity(AssistantRequest assistantRequest){
        Assistant assistant = new Assistant();
        assistant.setName(assistantRequest.getName());
        assistant.setPhoneNumber(assistantRequest.getPhoneNumber());
        assistant.setEmployeeId(assistantRequest.getEmployeeId());
        return assistant;
    }

    public AssistantResponse mapAssistantToResponse(Assistant assistant){
        AssistantResponse response = new AssistantResponse();
        response.setId(assistant.getId());
        response.setName(assistant.getName());
        response.setPhoneNumber(assistant.getPhoneNumber());
        response.setEmployeeId(assistant.getEmployeeId());
        return response;
    }

}
