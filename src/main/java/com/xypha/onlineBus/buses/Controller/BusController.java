package com.xypha.onlineBus.buses.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.buses.Dto.BusRequest;
import com.xypha.onlineBus.buses.Dto.BusResponse;
import com.xypha.onlineBus.buses.Entity.Bus;
import com.xypha.onlineBus.buses.service.BusServiceImpl;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/bus")
public class BusController {


    private final BusServiceImpl busService;

    private final ObjectMapper objectMapper;

    public BusController(BusServiceImpl busService, ObjectMapper objectMapper) {
        this.busService = busService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/paginated")
    public ResponseEntity<ApiResponse<PaginatedResponse<BusResponse>>> getBusPaginated(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {
        PaginatedResponse<BusResponse> buses =
                busService.getBusesPaginatedResponse(offset, limit).getPayload();

        ApiResponse<PaginatedResponse<BusResponse>> response = new ApiResponse<>(
                "SUCCESS",
                "Buses retrieved successfully",
                buses
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ApiResponse<List<BusResponse>> getAllBuses(){
        return busService.getAllBuses();
    }

    // Add Bus
    @PostMapping
    public ApiResponse<BusResponse> addBus(@Valid @RequestBody BusRequest busRequest) {
        return busService.addBus(busRequest);
    }


    // Get one bus
    @GetMapping("/{id}")
    public ApiResponse<BusResponse> getBusById(
            @PathVariable Long id
    ){
    return busService.getBusResponseById(id);
    }

    // Update bus
//    @PutMapping("/{id}")
//    public ApiResponse<BusResponse> updateBus(
//            @PathVariable Long id,
//            @Valid @RequestBody BusRequest request
//    ) {
//        return busService.updateBus(id, request);
//    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<BusResponse> updateBus(
            @PathVariable Long id,
            @RequestPart("busRequestString") String busRequestString,
            @RequestPart(value = "image", required = false) MultipartFile file
    )throws IOException{
        BusRequest busRequest;
        try{
            busRequest= objectMapper.readValue(busRequestString, BusRequest.class);

        }catch (JsonProcessingException e){
            return new ApiResponse<>("Failure","Invalid JSON format for busRequest", null);
    }
        if (file != null && !file.isEmpty()){
            if (!file.getContentType().toLowerCase().startsWith("image")) {
                return new ApiResponse<>("FAILURE", "Unsupported file type", null);
            }

            // upload to ImageKit (reuse your existing code)
            String imgUrl = uploadToImageKit(file);
            busRequest.setImgUrl(imgUrl);
        }

        return busService.updateBus(id, busRequest);
    }

    ///////////////////////////////////Search by BusNumber /driver + assistant by name or employeeID



    // Delete bus
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteBus(@PathVariable Long id) {
        return busService.deleteBus(id);
    }

    // Upload image + create Bus
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<BusResponse> uploadBus(
            @RequestPart(value = "busRequestString", required = true) String busRequestString,
            @RequestPart(value = "image", required = false) MultipartFile file
    ) throws IOException {


        BusRequest busRequest;
        try {
            busRequest = objectMapper.readValue(busRequestString, BusRequest.class);
        } catch (JsonProcessingException e) {
            return new ApiResponse<>("Failure","Invalid JSON format for busRequest", null);
        }
        ////////////////////////////////  Store in cloud ////////////////////////////////
        ////saving in cloud imagekit
        if (file != null && !file.isEmpty()){
            if (!file.getContentType().toLowerCase().startsWith("image")) {
                return new ApiResponse<>("FAILURE", "Unsupported file type", null);
            }
            String privateKey = "private_ju7N56tZcDf1kY6Ws8QnQTd/bvg=";
            String uploadUrl = "https://upload.imagekit.io/api/v1/files/upload";

            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(privateKey, "");
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            ByteArrayResource fileResource =new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename(){
                    return file.getOriginalFilename();
                }
            };
            body.add("file", fileResource);
            body.add("fileName", file.getOriginalFilename());
            body.add("folder", "/bus-images");

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(uploadUrl, requestEntity, String.class);

            //Parse Json To get URL
            ObjectMapper mapper = new ObjectMapper();
            String imgUrl = mapper.readTree(response.getBody()).get("url").asText();
            busRequest.setImgUrl(imgUrl);
        }

        return busService.addBus(busRequest);
    }



    private String uploadToImageKit(MultipartFile file) throws IOException {

        String privateKey = "private_ju7N56tZcDf1kY6Ws8QnQTd/bvg=";
        String uploadUrl = "https://upload.imagekit.io/api/v1/files/upload";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(privateKey, "");
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        body.add("file", fileResource);
        body.add("fileName", file.getOriginalFilename());
        body.add("folder", "/bus-images");

        HttpEntity<MultiValueMap<String, Object>> requestEntity =
                new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response =
                restTemplate.postForEntity(uploadUrl, requestEntity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(response.getBody()).get("url").asText();
    }
}
