package com.ic.attendance_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class AttendanceLogResponseDTO {

    private UUID id;
    private UUID empId;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;

    public AttendanceLogResponseDTO(UUID id, UUID empId,
                                    LocalDateTime checkIn,
                                    LocalDateTime checkOut) {
        this.id = id;
        this.empId = empId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEmpId() {
        return empId;
    }

    public LocalDateTime getCheckIn() {
        return checkIn;
    }

    public LocalDateTime getCheckOut() {
        return checkOut;
    }
}

