package com.ic.attendance_service.dto;

import com.ic.attendance_service.enums.LeaveType;

import java.time.LocalDate;

public class ApplyLeaveRequestDTO {

    private LocalDate fromDate;
    private LocalDate toDate;
    private LeaveType leaveType;


    // getters & setters
    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }
}

