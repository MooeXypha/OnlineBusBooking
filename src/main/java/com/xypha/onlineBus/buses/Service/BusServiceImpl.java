package com.xypha.onlineBus.buses.Service;

import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.buses.Dto.BusRequest;
import com.xypha.onlineBus.buses.Dto.BusResponse;
import com.xypha.onlineBus.buses.Entity.Bus;
import com.xypha.onlineBus.buses.Mapper.BusMapper;
import com.xypha.onlineBus.staffs.Assistant.Dto.AssistantResponse;
import com.xypha.onlineBus.staffs.Assistant.Mapper.AssistantMapper;
import com.xypha.onlineBus.staffs.Driver.Dto.DriverResponse;
import com.xypha.onlineBus.staffs.Driver.Mapper.DriverMapper;
import com.xypha.onlineBus.staffs.Service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusServiceImpl implements BusService {

  private final BusMapper busMapper;
  private final DriverMapper driverMapper;
  private final AssistantMapper assistantMapper;
  private final StaffService staffService;



  public BusServiceImpl(BusMapper busMapper, DriverMapper driverMapper, AssistantMapper assistantMapper, StaffService staffService) {
        this.busMapper = busMapper;
        this.driverMapper = driverMapper;
        this.assistantMapper = assistantMapper;
        this.staffService = staffService;
    }



    //Pagination Part
    public ApiResponse<PaginatedResponse<BusResponse>> getBusesPaginatedResponse(int page, int size) {
        int offset = page * size;
        List<BusResponse> buses = busMapper.findPaginated(offset, size)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        int total = busMapper.countBuses();
        PaginatedResponse<BusResponse> paginatedResponse = new PaginatedResponse<>(offset, size, total, buses);
        return new ApiResponse<>("SUCCESS", "Buses retrieved successfully", paginatedResponse);
    }



    public int countBuses(){
        return busMapper.countBuses();
    }


    public ApiResponse<BusResponse> addBus(BusRequest busRequest){

        if(busMapper.existsByBusNumber(busRequest.getBusNumber()) > 0){
            throw new RuntimeException("Bus number already exists");
        }
        LocalDate today = LocalDate.now();
        if (busRequest.getDriverId() != null) {
            int driverCount = busMapper.countDriverAssignmentsForDate(busRequest.getDriverId(), today);
            if (driverCount > 0){
                throw new RuntimeException("Driver is already assigned to another bus today");
            }
        }

        if (busRequest.getAssistantId() != null) {
            int assistantCount = busMapper.countAssistantAssignmentsForDate(busRequest.getAssistantId(), today);
            if (assistantCount > 0){
                throw new RuntimeException("Assistant is already assigned to another bus today");
            }
        }

        Bus bus = new Bus();
        bus.setBusNumber(busRequest.getBusNumber());
        bus.setBusType(busRequest.getBusType());
        bus.setTotalSeats(busRequest.getTotalSeats());
        bus.setHasAC(busRequest.getHasAC());
        bus.setHasWifi(busRequest.getHasWifi());
        bus.setImgUrl(busRequest.getImgUrl());
        bus.setDescription(busRequest.getDescription());
        bus.setDriverId(busRequest.getDriverId());
        bus.setAssistantId(busRequest.getAssistantId());

        busMapper.insertBus(bus);
        return new ApiResponse<>("SUCCESS","Bus added successfully", mapToResponse(bus));
    }

   public ApiResponse<BusResponse> getBusResponseById(Long id){
      Bus bus = busMapper.getBusById(id);
      if (bus == null) throw new RuntimeException("Bus not found with id: " + id);

      BusResponse busResponse = mapToResponse(bus);
      return new ApiResponse<>("SUCCESS", "Bus retrieved successfully", busResponse);
   }

    public List<BusResponse> getAllBuses(){
        return busMapper.findAllBusResponse();
    }

    public ApiResponse<BusResponse> updateBus(Long id, BusRequest busRequest){
        Bus bus = busMapper.getBusById(id);
        if (bus == null) throw new RuntimeException("Bus not found");

        bus.setBusNumber(busRequest.getBusNumber());
        bus.setBusType(busRequest.getBusType());
        bus.setTotalSeats(busRequest.getTotalSeats());
        bus.setHasAC(busRequest.getHasAC());
        bus.setHasWifi(busRequest.getHasWifi());
        bus.setImgUrl(busRequest.getImgUrl());
        bus.setDescription(busRequest.getDescription());
        bus.setDriverId(busRequest.getDriverId());
        bus.setAssistantId(busRequest.getAssistantId());

        busMapper.updateBus(bus);
        return new ApiResponse<>("SUCCESS", "Bus updated successfully", mapToResponse(bus));
    }

    public ApiResponse<Void> deleteBus(Long id) {
        busMapper.deleteBus(id);
        // Specify Void as the type parameter
        return new ApiResponse<>("SUCCESS", "Bus deleted successfully", null);
    }



    //Convert Bus -> BusResponse
    private BusResponse mapToResponse(Bus bus){
        BusResponse res = new BusResponse();
        res.setId(bus.getId());
        res.setBusNumber(bus.getBusNumber());
        res.setBusType(bus.getBusType());
        res.setTotalSeats(bus.getTotalSeats());
        res.setHasAC(bus.getHasAC());
        res.setHasWifi(bus.getHasWifi());
        res.setImgUrl(bus.getImgUrl());
        res.setDescription(bus.getDescription());
        res.setCreatedAt(bus.getCreatedAt());
        res.setUpdatedAt(bus.getUpdatedAt());

        if (bus.getDriverId() != null ){
            DriverResponse driver = staffService.getDriverById(bus.getDriverId());
            res.setDriver(driver);
        }
        if (bus.getAssistantId() != null){
            AssistantResponse assistant = staffService.getAssistantById(bus.getAssistantId());
            res.setAssistant(assistant);
        }
        return res;
    }




}