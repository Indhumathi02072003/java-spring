package com.ic.attendance_service.controller;


import com.ic.attendance_service.dto.AttendanceLogResponseDTO;
import com.ic.attendance_service.dto.AttendanceMeResponseDTO;
import com.ic.attendance_service.repository.AttendanceRepository;
import com.ic.attendance_service.service.AttendanceService;
import com.ic.security.models.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceService attendanceService;

    @PostMapping("/check-in/{empId}")
    public AttendanceLogResponseDTO checkIn(@PathVariable UUID empId) {
        return attendanceService.checkIn(empId);
    }

    @PostMapping("/check-out/{empId}")
    public AttendanceLogResponseDTO checkOut(@PathVariable UUID empId) {
        return attendanceService.checkOut(empId);
    }

    @GetMapping("/me")
    public AttendanceMeResponseDTO getMyAttendance(
            @AuthenticationPrincipal com.ic.security.models.Principal principal
    ) {
        UUID empId = UUID.fromString(principal.getUserid());
        return attendanceService.getMyAttendance(empId);
    }


//    @GetMapping("/{empId}")
//    public Object getAttendanceByEmpId(@PathVariable UUID empId) {
//        return attendanceRepository.findByEmpId(empId);
//    }


}