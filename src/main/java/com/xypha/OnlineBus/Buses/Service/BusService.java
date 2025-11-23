package com.xypha.OnlineBus.Buses.Service;

import com.xypha.OnlineBus.Buses.Dto.BusRequest;
import com.xypha.OnlineBus.Buses.Dto.BusResponse;
import com.xypha.OnlineBus.Buses.Entity.Bus;
import com.xypha.OnlineBus.Buses.Mapper.BusMapper;
import com.xypha.OnlineBus.Staffs.Service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusService {

    @Autowired
    private BusMapper busMapper;

    @Autowired
    StaffService staffService;


    //Pagination Part
    public List<BusResponse> getBusPaginated(int page, int size){
        int offset = page * size;
        List<Bus> buses = busMapper.findPaginated(offset,size);
        return buses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    public int countBuses(){
        return busMapper.countBuses();
    }


    public BusResponse addBus(BusRequest busRequest){
        if(busMapper.existsByBusNumber(busRequest.getBusNumber()) > 0){
            throw new RuntimeException("Bus number already exists");
        }
        LocalDate today = LocalDate.now();
        if (busRequest.getDriverId() != null){
            int driverCount = busMapper.countDriverAssignmentsForDate(busRequest.getDriverId(), today);
            if (driverCount > 0){
                throw new RuntimeException("Driver is already assigned to another bus today");
            }

            if (busRequest.getAssistantId() != null){
                int assistantCount = busMapper.countAssistantAssignmentsForDate(busRequest.getAssistantId(), today);
                if (assistantCount > 0){
                    throw new RuntimeException("Assistant is already assigned to another bus today");
                }
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
        return mapToResponse(bus);
    }

    public BusResponse getBusById(Long id){
        Bus bus = busMapper.getBusById(id);
        if(bus == null) throw new RuntimeException("Bus not found");
        return mapToResponse(bus);
    }

    public List<BusResponse> getAllBuses(){
        return busMapper.getAllBuses().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public BusResponse updateBus(Long id, BusRequest busRequest){
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
        bus.setAssistantId(bus.getAssistantId());

        busMapper.updateBus(bus);
        return mapToResponse(bus);
    }

    public void deleteBus(Long id){
        busMapper.deleteBus(id);
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
        res.setDriverId(bus.getDriverId());
        res.setAssistantId(bus.getAssistantId());
        res.setCreatedAt(bus.getCreatedAt());
        res.setUpdatedAt(bus.getUpdatedAt());

        if (bus.getDriverId() != null ){
            res.setDriverName(staffService.getDriverById (bus.getDriverId()).getName());
            res.setDriverId(staffService.getDriverById(bus.getDriverId()).getId());
            res.setDriverEmployeeId(staffService.getDriverById(bus.getDriverId()).getEmployeeId());
        }
        if (bus.getAssistantId() != null){
            res.setAssistantName(staffService.getAssistantById(bus.getAssistantId()).getName());
            res.setAssistantId(staffService.getAssistantById(bus.getAssistantId()).getId());
            res.setAssistantEmployeeId(staffService.getAssistantById(bus.getAssistantId()).getEmployeeId());
        }
        return res;
    }




}