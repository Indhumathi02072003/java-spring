package com.ic.attendance_service.repository;

import com.ic.attendance_service.entity.AttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<AttendanceLog, UUID> {

    Optional<AttendanceLog> findByEmpIdAndCheckOutIsNull(UUID empId);

    Optional<AttendanceLog>
    findTopByEmpIdAndCheckOutIsNullOrderByCheckInDesc(UUID empId);



    Object findByEmpId(UUID empId);
}

