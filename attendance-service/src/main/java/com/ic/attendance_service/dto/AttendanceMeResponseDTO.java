package com.ic.attendance_service.dto;

import com.ic.attendance_service.enums.LeaveStatus;
import com.ic.attendance_service.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class AttendanceMeResponseDTO {

    // Leave
    private UUID leaveId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private LeaveStatus leaveStatus;
    private LeaveType leaveType;


}

