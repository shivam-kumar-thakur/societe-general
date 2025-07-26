package com.example.analysis.controller;

import com.example.analysis.dto.ResponseDTO;
import com.example.analysis.dto.ServiceResponseDTO;
import com.example.analysis.dto.iocDto.DashboardFilterRequestDTO;
import com.example.analysis.dto.iocDto.RecentIocRequestDTO;
import com.example.analysis.service.IocService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ioc")
public class IocController {

    private  final IocService iocService;

    public IocController(IocService iocService) {
        this.iocService = iocService;
    }

    @PostMapping("/recent")
    public ResponseEntity recentIoc(@RequestBody @Valid RecentIocRequestDTO recentIocRequestDTO){
        ServiceResponseDTO serviceResponseDTO=iocService.findByFilters(recentIocRequestDTO);
        return ResponseEntity.status(serviceResponseDTO.getStatusCode()).body(new ResponseDTO<>(serviceResponseDTO.isStatusFlag(),serviceResponseDTO.getMessage(),serviceResponseDTO.getData()));
    }


    @GetMapping("/ioc-details/{id}")
    public ResponseEntity recentIocDetails(@PathVariable("id") String iocId){
        ServiceResponseDTO serviceResponseDTO=iocService.getRecentIocDetails(iocId);
        return ResponseEntity.status(serviceResponseDTO.getStatusCode()).body(new ResponseDTO<>(serviceResponseDTO.isStatusFlag(),serviceResponseDTO.getMessage(),serviceResponseDTO.getData()));
    }


    // Ioc Dashboard here
    @PostMapping("/dashboard")
    public ResponseEntity getDashboardSummary(@RequestBody DashboardFilterRequestDTO filterDto) {
        ServiceResponseDTO serviceResponseDTO=iocService.getIocDashboardSummaryFiltered(filterDto);
        return ResponseEntity.status(serviceResponseDTO.getStatusCode()).body(new ResponseDTO<>(serviceResponseDTO.isStatusFlag(),serviceResponseDTO.getMessage(),serviceResponseDTO.getData()));
    }

}
