package com.xypha.OnlineBus.Buses.Controller;

import com.xypha.OnlineBus.Buses.Dto.BusRequest;
import com.xypha.OnlineBus.Buses.Dto.BusResponse;
import com.xypha.OnlineBus.Buses.Service.BusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/bus")
public class BusController {


    private final BusService busService;

    public BusController(BusService busService) {
        this.busService = busService;
    }

    // Pagination (MyBatis version)
    @GetMapping("/paginated")
    public ResponseEntity<Map<String, Object>> getBusPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        List<BusResponse> buses = busService.getBusPaginated(page, size);
        long totalItems = busService.countBuses();

        Map<String, Object> response = new HashMap<>();
        response.put("buses", buses);
        response.put("currentPage", page);
        response.put("pageSize", size);
        response.put("totalItems", totalItems);
        response.put("totalPages", (int) Math.ceil((double) totalItems / size));

        return ResponseEntity.ok(response);
    }

    // Add Bus
    @PostMapping
    public ResponseEntity<BusResponse> addBus(@Valid @RequestBody BusRequest busRequest) {
        BusResponse saved = busService.addBus(busRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Get all buses
    @GetMapping
    public ResponseEntity<List<BusResponse>> getAllBus() {
        return ResponseEntity.ok(busService.getAllBuses());
    }

    // Get one bus
    @GetMapping("/{id}")
    public ResponseEntity<BusResponse> getBus(@PathVariable Long id) {
        return ResponseEntity.ok(busService.getBusById(id));
    }

    // Update bus
    @PutMapping("/{id}")
    public ResponseEntity<BusResponse> updateBus(
            @PathVariable Long id,
            @Valid @RequestBody BusRequest request
    ) {
        return ResponseEntity.ok(busService.updateBus(id, request));
    }

    // Delete bus
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBus(@PathVariable Long id) {
        busService.deleteBus(id);
        return ResponseEntity.ok("Bus deleted successfully");
    }

    // Upload image + create Bus
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BusResponse> uploadBus(
            @ModelAttribute BusRequest busRequest,
            @RequestPart("image") MultipartFile file
    ) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        if (!file.getContentType().toLowerCase().startsWith("image")) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(null);
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        Path uploadPath = Paths.get("C:\\Users\\Leon\\Desktop\\uploads");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        busRequest.setImgUrl(fileName);

        BusResponse savedBus = busService.addBus(busRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBus);
    }
}
