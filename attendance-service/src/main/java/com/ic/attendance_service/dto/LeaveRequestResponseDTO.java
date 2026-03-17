package com.ic.attendance_service.dto;

import com.ic.attendance_service.enums.LeaveStatus;
import com.ic.attendance_service.enums.LeaveType;

import java.time.LocalDate;
import java.util.UUID;

public class LeaveRequestResponseDTO {

    private UUID id;
    private UUID empId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private LeaveType leaveType;
    private LeaveStatus status;

    public LeaveRequestResponseDTO(UUID id, UUID empId,
                                   LocalDate fromDate,
                                   LocalDate toDate,
                                   LeaveType leaveType,
                                   LeaveStatus status) {
        this.id = id;
        this.empId = empId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.leaveType= leaveType;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEmpId() {
        return empId;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }
}

