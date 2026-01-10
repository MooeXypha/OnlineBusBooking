package com.xypha.onlineBus.routes.Controller;


import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.routes.Dto.RouteRequest;
import com.xypha.onlineBus.routes.Dto.RouteResponse;
import com.xypha.onlineBus.routes.mapper.RouteMapper;
import com.xypha.onlineBus.routes.Service.RouteServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/route")

public class RouteController {

    @Autowired
    private RouteServiceImpl routeService;

    @Autowired
    private RouteMapper routeMapper;



    @PostMapping
    public ApiResponse<RouteResponse> addRoute(
            @Valid @RequestBody RouteRequest routeRequest){
            return routeService.addRoute(routeRequest);
    }

    @GetMapping("/paginated")
    public ResponseEntity<ApiResponse<PaginatedResponse<RouteResponse>>> getAllRoutes(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ){
        return ResponseEntity.ok(routeService.getAllRoute(offset, limit));
    }

    @GetMapping("/all")
    public ApiResponse<List<RouteResponse>> getAllRoute(){
        return routeService.getAllRoutes();
    }

    @GetMapping("/search")
    public ResponseEntity <ApiResponse<PaginatedResponse<RouteResponse>>> searchRoutes(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String destination,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){
    ApiResponse<PaginatedResponse<RouteResponse>> response = routeService.searchRoutes(source, destination, page, size);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ApiResponse <Void> deleteRoute(
            @PathVariable Long id
    ){
        return routeService.deleteRoute(id);
    }


    @PutMapping("/{id}")
    public ApiResponse<RouteResponse>updateRoute(
            @PathVariable Long id,
            @Valid @RequestBody RouteRequest routeRequest
    ){
        return routeService.updateRoute(id, routeRequest);
    }


    @GetMapping("/{id}")
    public ApiResponse<RouteResponse> getRouteById(
            @PathVariable Long id
    ){
        return routeService.getRouteById(id);
    }


}
