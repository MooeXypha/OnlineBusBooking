package com.xypha.OnlineBus.Routes.Controller;


import com.xypha.OnlineBus.Routes.Dto.RouteRequest;
import com.xypha.OnlineBus.Routes.Dto.RouteResponse;
import com.xypha.OnlineBus.Routes.Entity.Route;
import com.xypha.OnlineBus.Routes.Service.RouteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/route")
@CrossOrigin("*")
public class RouteController {

    @Autowired
    private RouteService routeService;




    @PostMapping
    public ResponseEntity<RouteResponse> addRoute(
            @Valid @RequestBody RouteRequest routeRequest){
            return ResponseEntity.ok(routeService.addRoute(routeRequest));
    }

    @GetMapping
    public ResponseEntity <List<RouteResponse>> getAllRoutes(){
        return ResponseEntity.ok(routeService.getAllRoute());
    }

    @GetMapping("/search")
    public ResponseEntity <Map<String, Object>> searchRoutes(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String destination,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){
    return ResponseEntity.ok(routeService.searchRoutes(source, destination, page, size));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoute(
            @PathVariable Long id
    ){
        routeService.deleteRoute(id);
        return ResponseEntity.ok("Route deleted successfully");
    }


    @PutMapping("/{id}")
    public ResponseEntity <RouteResponse>updateRoute(
            @PathVariable Long id,
            @Valid @RequestBody RouteRequest routeRequest
    ){
        return ResponseEntity.ok(routeService.updateRoute(id, routeRequest));
    }



}
