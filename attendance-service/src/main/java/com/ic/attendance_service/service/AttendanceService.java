package com.ic.attendance_service.service;

import com.ic.attendance_service.dto.AttendanceLogResponseDTO;
import com.ic.attendance_service.dto.AttendanceMeResponseDTO;
import com.ic.attendance_service.entity.AttendanceLog;
import com.ic.attendance_service.entity.LeaveRequest;
import com.ic.attendance_service.repository.AttendanceRepository;
import com.ic.attendance_service.repository.LeaveRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AttendanceService {
    private static final Logger log =
            LoggerFactory.getLogger(AttendanceService.class);

    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, LeaveRequestRepository leaveRequestRepository
    ) {
        this.attendanceRepository = attendanceRepository;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    // CHECK-IN
    public AttendanceLogResponseDTO checkIn(UUID empId) {
        log.info("Check-in started for empId={}", empId);

        AttendanceLog log = AttendanceLog.builder()
                .empId(empId)
                .checkIn(LocalDateTime.now())
                .build();

        AttendanceLog saved = attendanceRepository.save(log);

        log.info("Check-in successful for empId={}, attendanceId={}",
                empId, saved.getId());


        return new AttendanceLogResponseDTO(
                saved.getId(),
                saved.getEmpId(),
                saved.getCheckIn(),
                saved.getCheckOut()
        );
    }

    // CHECK-OUT
    public AttendanceLogResponseDTO checkOut(UUID empId) {

        log.info("Check-out requested for empId={}", empId);

        AttendanceLog log = attendanceRepository
                .findTopByEmpIdAndCheckOutIsNullOrderByCheckInDesc(empId)
                .orElseThrow(() -> new RuntimeException("No active check-in found"));

        log.setCheckOut(LocalDateTime.now());

        AttendanceLog updated = attendanceRepository.save(log);

        log.info("Check-out completed for empId={}, attendanceId={}",
                empId, updated.getId());


        return new AttendanceLogResponseDTO(
                updated.getId(),
                updated.getEmpId(),
                updated.getCheckIn(),
                updated.getCheckOut()
        );
    }

    public AttendanceMeResponseDTO getMyAttendance(UUID empId) {

        // Latest attendance
        Optional<AttendanceLog> attendance =
                attendanceRepository.findTopByEmpIdAndCheckOutIsNullOrderByCheckInDesc(empId);

        // Active leave (today between fromDate & toDate)
        Optional<LeaveRequest> latestLeave  =
                leaveRequestRepository.findTopByEmpIdOrderByFromDateDesc(empId)
                ;

        AttendanceMeResponseDTO dto = new AttendanceMeResponseDTO();

        latestLeave .ifPresent(l -> {
            dto.setLeaveId(l.getId());
            dto.setFromDate(l.getFromDate());
            dto.setToDate(l.getToDate());
            dto.setLeaveStatus(l.getStatus());
            dto.setLeaveType(l.getLeaveType());
        });

        return dto;
    }

}

