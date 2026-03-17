package com.ic.attendance_service.controller;

import com.ic.attendance_service.dto.ApplyLeaveRequestDTO;
import com.ic.attendance_service.dto.LeaveRequestResponseDTO;
import com.ic.attendance_service.repository.LeaveRequestRepository;
import com.ic.attendance_service.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/leave")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveService leaveService;


    @PostMapping("/apply")
    public LeaveRequestResponseDTO applyLeave(@RequestHeader("X-User-Id") UUID empId,
                                              @RequestBody ApplyLeaveRequestDTO request
    ) {
        return leaveService.applyLeave(empId, request);
    }

    @GetMapping("/leaveChecking")
    public List<LeaveRequestResponseDTO> getLeaves(@RequestHeader("X-User-Id") UUID empId)
    {
        return leaveService.getLeaves(empId);
    }

    @PutMapping("/{id}/approve")
    public LeaveRequestResponseDTO approveLeave(@PathVariable UUID id) {
        return leaveService.approveLeave(id);
    }

    @PutMapping("/{id}/reject")
    public LeaveRequestResponseDTO rejectLeave(@PathVariable UUID id) {
        return leaveService.rejectLeave(id);
    }

}

