package com.xypha.onlineBus.staffs.Service;

import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.staffs.Assistant.Dto.AssistantRequest;
import com.xypha.onlineBus.staffs.Assistant.Dto.AssistantResponse;
import com.xypha.onlineBus.staffs.Assistant.Entity.Assistant;
import com.xypha.onlineBus.staffs.Assistant.Mapper.AssistantMapper;
import com.xypha.onlineBus.staffs.Driver.Dto.DriverRequest;
import com.xypha.onlineBus.staffs.Driver.Dto.DriverResponse;
import com.xypha.onlineBus.staffs.Driver.Entity.Driver;
import com.xypha.onlineBus.staffs.Driver.Mapper.DriverMapper;

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
        if (driverMapper.findByName(request.getName()) != null){
            throw new RuntimeException("Driver name already exists: " + request.getName());
        }
        if (driverMapper.findByPhoneNumber(request.getPhoneNumber()) != null){
            throw new RuntimeException("Phone number already exists: " + request.getPhoneNumber());
        }
        if (driverMapper.getDriverByEmployeeId(request.getEmployeeId()) != null){
            throw new RuntimeException("Employee ID already exists: " + request.getEmployeeId());
        }
        if (driverMapper.findByLicenseNumber(request.getLicenseNumber()) != null){
            throw new RuntimeException("License number already exists: " + request.getLicenseNumber());
        }

        if (request.getEmployeeId().trim().isEmpty()){
            throw new RuntimeException("Employee ID cannot be null or empty");
        }

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
    public PaginatedResponse<DriverResponse> getAllDriver(int offset, int limit){
        List<DriverResponse> drivers = driverMapper.getAllPaginatedDriver(offset, limit);
        long total = driverMapper.countDrivers();
        return new PaginatedResponse<>(offset, limit, total, drivers);
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

        if (request.getEmployeeId() != null &&
            !request.getEmployeeId().trim().isEmpty() &&
            !request.getEmployeeId().equals(driver.getEmployeeId())) {

            int count = driverMapper.countDriverUpdate(request.getEmployeeId(), id);
            if (count > 0){
                throw new RuntimeException("Employee ID already exists: " + request.getEmployeeId());
            }
        }

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

    public PaginatedResponse<DriverResponse> searchDriver(
            String name,int offset, int limit
    ){
        List<Driver> drivers = driverMapper.searchDriverByName(name, offset, limit);
        if (drivers.isEmpty()){
            return new PaginatedResponse<>(offset, limit, 0, List.of());
        }
        List<DriverResponse> driverResponse = drivers.stream().
                map(this::mapDriverToResponse).toList();
        long total = driverMapper.countDriverByName(name);
        return new PaginatedResponse<>(offset, limit, total, driverResponse);

    }



    public void validateEmployeeIdDriver(String employeeId){
        int count = driverMapper.countEmployeeId(employeeId);
        if (count > 0){
            throw new RuntimeException("Employee ID already exists: " + employeeId);
        }
    }

    public void validateEmployeeIdAssistant(String employeeId){
        int count = assistantMapper.countEmployeeId(employeeId);
        if (count > 0){
            throw new RuntimeException("Employee ID already exists: " + employeeId);
        }
    }


    //Assistant CRUD
    public AssistantResponse addAssistant(AssistantRequest assistantRequest){
        if (assistantMapper.findByName(assistantRequest.getName()) != null){
            throw new RuntimeException("Name already exists: "+ assistantRequest.getName());
        } else if (assistantMapper.findByPhoneNumber(assistantRequest.getPhoneNumber()) != null){
            throw new RuntimeException("Phone number already exists: " + assistantRequest.getPhoneNumber());
        }

        //Validate to assistant employeeId is unique
        validateEmployeeIdAssistant(assistantRequest.getEmployeeId());

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
    public PaginatedResponse<AssistantResponse> getAllAssistant(int offset, int limit){
        List<AssistantResponse> assistants = assistantMapper.getAllAssistantPaginated(offset, limit);
        long total = assistantMapper.countAssistants();
        return new PaginatedResponse<>(offset, limit, total, assistants);
    }
    public AssistantResponse getAssistantByEmployeeId(String employeeId){
        Assistant assistant = assistantMapper.getAssistantByEmployeeId(employeeId);
        return mapAssistantToResponse(assistant);
    }
    public AssistantResponse updateAssistant(Long id, AssistantRequest request){
        Assistant assistant = assistantMapper.getAssistantById(id);
        if (assistant == null)
            throw new RuntimeException("Assistant not found with id: " + id);
        if (request.getEmployeeId() != null &&
            !request.getEmployeeId().trim().isEmpty() &&
            !request.getEmployeeId().equals(assistant.getEmployeeId())) {

            int count = assistantMapper.countAssistantUpdate(request.getEmployeeId(), id);
            if (count > 0){
                throw new RuntimeException("Employee ID already exists: " + request.getEmployeeId());
            }
        }

        assistant.setPhoneNumber(request.getPhoneNumber());
        assistant.setEmployeeId(request.getEmployeeId());
        assistantMapper.updateAssistant(assistant);
        return mapAssistantToResponse(assistant);
    }
    public void deleteAssistant(Long id){
        assistantMapper.deleteAssistant(id);

    }

    public PaginatedResponse<AssistantResponse> searchAssistantByName(
            String name,int offset, int limit
    ){
        List<Assistant> assistants = assistantMapper.searchAssistantByName(name, offset, limit);
        if (assistants.isEmpty()){
            return new PaginatedResponse<>(offset, limit, 0, List.of());
        }

        List<AssistantResponse> assistantResponses = assistants.stream().
                map(this::mapAssistantToResponse).toList();
        long total = assistantMapper.countAssistantByName(name);
        return new PaginatedResponse<>(offset, limit, total, assistantResponses);

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
