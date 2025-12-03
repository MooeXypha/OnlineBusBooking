package com.xypha.onlineBus.api.controller;

import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.BranchDto;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.api.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/branches")
public class BranchController {

    @Autowired
    private BranchService branchService;

    //Get single branch
    @GetMapping("/{id}")
    public ApiResponse<BranchDto> getBranch (@PathVariable Long id){
        BranchDto branchDto = branchService.getBranchById(id);
        return new ApiResponse<>("SUCCESS", "Branch retrieved successfully", branchDto);
    }


    //Get All Branch
    @GetMapping
    public ApiResponse<PaginatedResponse<BranchDto>> getAllBranches(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ){
        PaginatedResponse<BranchDto> paginateBranches = branchService.getAllBranches(offset,limit);
        return new ApiResponse<>("SUCCESS", "Branches retrieved successfully", paginateBranches);
    }
}
